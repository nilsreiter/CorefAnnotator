package de.unistuttgart.ims.coref.annotator;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.apache.logging.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.coref.annotator.action.FileImportQuaDramAAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;

public class DocumentWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String HELP_MESSAGE = "Instructions for using Xmi Viewer";

	JCas jcas;
	File file;
	static Logger logger = Annotator.logger;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// controller
	CoreferenceModel cModel;

	// Window components
	CasTextView viewer;
	DetailsPanel panel;

	// Menu components
	JMenuBar menuBar = new JMenuBar();
	JMenu documentMenu;
	JMenu recentMenu;
	JMenu windowsMenu;

	// Listeners
	List<LoadingListener> loadingListeners = new LinkedList<LoadingListener>();

	public DocumentWindow(Annotator annotator) {
		super();
		this.mainApplication = annotator;
		this.initialiseMenu();
		this.initialiseWindow();

	}

	public void initialiseWindow() {
		viewer = new CasTextView(this);
		panel = new DetailsPanel(this);

		loadingListeners.add(panel);
		loadingListeners.add(viewer);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewer, panel);

		getContentPane().add(splitPane);
		pack();
		setVisible(true);
	}

	protected void closeWindow(boolean quit) {
		mainApplication.close(this);
	}

	public void loadFile(File file, CoreferenceFlavor flavor) {
		this.file = file;
		try {
			logger.info("Loading XMI document from {}.", file);
			loadStream(new FileInputStream(file), TypeSystemDescriptionFactory.createTypeSystemDescription(),
					file.getName(), flavor);
		} catch (FileNotFoundException e) {
			logger.warn("File {} not found.", file);
			mainApplication.warnDialog("File " + file.getAbsolutePath() + " could not be found.", "File not found");
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void saveCurrentFile() {
		try {
			XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(file));
		} catch (FileNotFoundException | SAXException e) {
			e.printStackTrace();
			mainApplication.warnDialog(e.getMessage(), e.toString());
		}
	}

	protected void loadStream(InputStream inputStream, TypeSystemDescription typeSystemDescription, String windowTitle,
			CoreferenceFlavor flavor) {
		// load type system and CAS
		try {
			jcas = JCasFactory.createJCas(typeSystemDescription);

		} catch (UIMAException e1) {
			logger.error(e1.getMessage());
			e1.printStackTrace();
			System.exit(1);
		}
		try {
			logger.info("Deserialising input stream.");
			XmiCasDeserializer.deserialize(inputStream, jcas.getCas(), true);
		} catch (SAXException e1) {
			logger.error(e1.getMessage());

			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			logger.error(e1.getMessage());

			e1.printStackTrace();
			System.exit(1);
		}
		try {
			SimplePipeline.runPipeline(jcas, flavor.getAnalysisEngine());
		} catch (AnalysisEngineProcessException | ResourceInitializationException e1) {
			e1.printStackTrace();
		}
		this.fireJCasLoadedEvent();

		try {
			Feature titleFeature = jcas.getTypeSystem()
					.getFeatureByFullName(mainApplication.getConfiguration().getString("General.windowTitleFeature"));
			if (titleFeature != null)
				try {
					setTitle(jcas.getDocumentAnnotationFs().getFeatureValueAsString(titleFeature)
							+ (windowTitle != null ? " (" + windowTitle + ")" : ""));
				} catch (Exception e) {
					setTitle((windowTitle != null ? " (" + windowTitle + ")" : ""));
				}
			else
				setTitle((windowTitle != null ? " (" + windowTitle + ")" : ""));
		} catch (CASRuntimeException e) {
			logger.error(e.getMessage());
		}

		this.cModel = new CoreferenceModel(jcas);
		this.cModel.addCoreferenceModelListener(viewer);
		this.cModel.importExistingData();
		this.fireModelCreatedEvent();
	}

	protected void initialiseMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("Could not set look and feel {}.", e.getMessage());
		}

		// top level menus
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenu viewMenu = new JMenu("View");
		JMenu toolsMenu = new JMenu("Tools");
		JMenu debugMenu = new JMenu("Debug");
		windowsMenu = new JMenu("Windows");
		if (segmentAnnotation != null) {
			documentMenu = new JMenu("Document");
			documentMenu.setEnabled(segmentAnnotation != null);
		}

		// file menu
		fileMenu.add(new FileOpenAction(mainApplication));
		fileMenu.add(new FileSaveAction(this));
		JMenu fileImportMenu = new JMenu("Import from ...");
		fileMenu.add(fileImportMenu);
		fileImportMenu.add(new FileImportQuaDramAAction(mainApplication));

		// View menu
		viewMenu.add(new JMenuItem(new ViewFontSizeDecreaseAction()));
		viewMenu.add(new JMenuItem(new ViewFontSizeIncreaseAction()));
		viewMenu.addSeparator();

		JRadioButtonMenuItem sortAlphaButton = new JRadioButtonMenuItem(new AbstractAction("Sort alphabetically") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder = EntitySortOrder.Alphabet;
				cModel.resort();

			}
		});
		sortAlphaButton.setSelected(true);

		JRadioButtonMenuItem sortMentionsButton = new JRadioButtonMenuItem(new AbstractAction("Sort by mentions") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder = EntitySortOrder.Mentions;
				cModel.resort();
			}
		});
		ButtonGroup grp = new ButtonGroup();
		grp.add(sortMentionsButton);
		grp.add(sortAlphaButton);

		viewMenu.add(sortAlphaButton);
		viewMenu.add(sortMentionsButton);

		viewMenu.add(new JCheckBoxMenuItem(new AbstractAction("Revert sort order") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder.descending = !cModel.entitySortOrder.descending;
				cModel.resort();
			}

		}));

		// Menu Items
		JMenuItem aboutMenuItem = new JMenuItem("About");
		JMenuItem helpMenuItem = new JMenuItem("Help");
		JMenuItem exitMenuItem = new JMenuItem("Quit");
		recentMenu = new JMenu("Open Recent");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(toolsMenu);
		if (segmentAnnotation != null)
			menuBar.add(documentMenu);
		menuBar.add(windowsMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		// window events
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainApplication.close((DocumentWindow) e.getSource());
			}
		});

		// Event Handlling of "Quit" Menu Item
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				mainApplication.handleQuitRequestWith(new QuitEvent(), new QuitResponse());
			}
		});

		// Event Handlling of "Close" Menu Item
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// savePreferences();
				closeWindow(false);
			}
		});

		// Event Handlling of "About" Menu Item
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				mainApplication.handleAbout(new AboutEvent());
			}
		});

		// Event Handlling of "Help" Menu Item
		helpMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JOptionPane.showMessageDialog(DocumentWindow.this, HELP_MESSAGE, "Annotation Viewer Help",
						JOptionPane.PLAIN_MESSAGE);
			}
		});

		logger.info("Initialised window.");
	}

	public JCas getJcas() {
		return jcas;
	}

	public Annotator getMainApplication() {
		return mainApplication;
	}

	public CasTextView getViewer() {
		return viewer;
	}

	class ViewFontSizeDecreaseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewFontSizeDecreaseAction() {
			super();
			putValue(Action.NAME, "Decrease Font Size");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int oldSize = viewer.getTextPane().getFont().getSize();
			viewer.getTextPane().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, oldSize - 1));
		}

	}

	class ViewFontSizeIncreaseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewFontSizeIncreaseAction() {
			super();
			putValue(Action.NAME, "Increase Font Size");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int oldSize = viewer.getTextPane().getFont().getSize();
			viewer.getTextPane().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, oldSize + 1));
		}

	}

	protected void fireModelCreatedEvent() {
		for (LoadingListener ll : loadingListeners)
			ll.modelCreated(cModel);
	}

	protected void fireJCasLoadedEvent() {
		for (LoadingListener ll : loadingListeners)
			ll.jcasLoaded(jcas);
	}

}
