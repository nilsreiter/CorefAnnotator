package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.commons.Counter;
import de.unistuttgart.ims.coref.annotator.action.ChangeColorForEntity;
import de.unistuttgart.ims.coref.annotator.action.ChangeKeyForEntityAction;
import de.unistuttgart.ims.coref.annotator.action.DeleteMentionAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportCRETAAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportDKproAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportQuaDramAAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.NewEntityAction;
import de.unistuttgart.ims.coref.annotator.action.RenameEntityAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DocumentWindow extends JFrame implements CaretListener, TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String HELP_MESSAGE = "Instructions for using Xmi Viewer";

	JCas jcas;
	File file;
	static Logger logger = Annotator.logger;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// actions
	NewEntityAction newEntityAction;
	RenameEntityAction renameEntityAction;
	ChangeKeyForEntityAction changeKeyForEntityAction;
	ChangeColorForEntity changeColorForEntityAction;

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
	JMenu entityMenu;
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
		panel = new DetailsPanel();

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
		viewer.switchStyle(jcas, StyleVariant.select(flavor));

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
		this.initialiseMenuAfterModelCreation();
	}

	protected void initialiseMenuAfterModelCreation() {
		this.newEntityAction = new NewEntityAction(cModel, viewer.getTextPane());
		this.changeColorForEntityAction = new ChangeColorForEntity(cModel, panel.tree);
		this.changeKeyForEntityAction = new ChangeKeyForEntityAction(cModel, panel.tree);
		this.renameEntityAction = new RenameEntityAction(cModel, panel.tree);

		entityMenu.add(new JMenuItem(newEntityAction));
		entityMenu.add(new JMenuItem(changeColorForEntityAction));
		entityMenu.add(new JMenuItem(changeKeyForEntityAction));
		entityMenu.add(new JMenuItem(renameEntityAction));
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
		entityMenu = new JMenu("Entities");
		// JMenu debugMenu = new JMenu("Debug");
		windowsMenu = new JMenu("Windows");
		if (segmentAnnotation != null) {
			documentMenu = new JMenu("Document");
			documentMenu.setEnabled(segmentAnnotation != null);
		}

		// Menu Items
		JMenuItem aboutMenuItem = new JMenuItem("About");
		JMenuItem helpMenuItem = new JMenuItem("Help");
		JMenuItem exitMenuItem = new JMenuItem("Quit");
		recentMenu = new JMenu("Open Recent");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		// file menu
		fileMenu.add(new FileOpenAction(mainApplication));

		fileMenu.add(new FileSaveAction(this));
		JMenu fileImportMenu = new JMenu("Import from ...");
		fileMenu.add(fileImportMenu);
		fileImportMenu.add(new FileImportQuaDramAAction(mainApplication));
		fileImportMenu.add(new FileImportDKproAction(mainApplication));
		fileImportMenu.add(new FileImportCRETAAction(mainApplication));

		fileMenu.add(closeMenuItem);
		fileMenu.add(exitMenuItem);

		// tools menu
		toolsMenu.add(new JMenuItem(new ShowSearchPanelAction(mainApplication, this)));

		// View menu
		viewMenu.add(new JMenuItem(new ViewFontSizeDecreaseAction()));
		viewMenu.add(new JMenuItem(new ViewFontSizeIncreaseAction()));
		viewMenu.addSeparator();

		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(new AbstractAction("Sort alphabetically") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder = EntitySortOrder.Alphabet;
				cModel.resort();

			}
		});
		radio1.setSelected(true);

		JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(new AbstractAction("Sort by mentions") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder = EntitySortOrder.Mentions;
				cModel.resort();
			}
		});
		ButtonGroup grp = new ButtonGroup();
		grp.add(radio2);
		grp.add(radio1);

		viewMenu.add(radio1);
		viewMenu.add(radio2);

		viewMenu.add(new JCheckBoxMenuItem(new AbstractAction("Revert sort order") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cModel.entitySortOrder.descending = !cModel.entitySortOrder.descending;
				cModel.resort();
			}

		}));

		JMenu viewStyleMenu = new JMenu("Style");
		grp = new ButtonGroup();
		int i = 0;
		for (StyleVariant sv : StyleVariant.values()) {
			radio1 = new JRadioButtonMenuItem(new ViewStyleSelectAction(sv));
			viewStyleMenu.add(radio1);
			if ((i++) == 0)
				radio1.setSelected(true);
			grp.add(radio1);

		}
		viewMenu.add(viewStyleMenu);

		menuBar.add(fileMenu);
		menuBar.add(entityMenu);

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

	class ViewStyleQuaDramAAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewStyleQuaDramAAction() {
			putValue(Action.NAME, "QuaDramA");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			viewer.switchStyle(jcas, StyleVariant.QuaDramA);
		}

	}

	class ViewStyleSelectAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		StyleVariant styleVariant;

		public ViewStyleSelectAction(StyleVariant style) {
			putValue(Action.NAME, style.name());
			styleVariant = style;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			viewer.switchStyle(jcas, styleVariant);

		}

	}

	class ViewStyleDefaultAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewStyleDefaultAction() {
			putValue(Action.NAME, "Default");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			viewer.switchStyle(jcas, StyleVariant.Default);
		}

	}

	protected void fireModelCreatedEvent() {
		for (LoadingListener ll : loadingListeners)
			ll.modelCreated(cModel, this);
	}

	protected void fireJCasLoadedEvent() {
		for (LoadingListener ll : loadingListeners)
			ll.jcasLoaded(jcas);
	}

	@Override
	public void caretUpdate(CaretEvent e) {

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		this.changeColorForEntityAction.setEnabled(e.getPath().getPathCount() == 2);
		this.changeKeyForEntityAction.setEnabled(e.getPath().getPathCount() == 2);
		this.renameEntityAction.setEnabled(e.getPath().getPathCount() == 2);
	}

	class CasTextView extends JPanel implements LoadingListener, CoreferenceModelListener {

		private static final long serialVersionUID = 1L;

		JTextPane textPane;
		Highlighter hilit;
		Highlighter.HighlightPainter painter;
		StyleContext styleContext = new StyleContext();
		Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
		Counter<Span> spanCounter = new Counter<Span>();

		Object selectionHighlight = null;

		public CasTextView(DocumentWindow dw) {
			super(new BorderLayout());
			this.hilit = new DefaultHighlighter();
			this.textPane = new JTextPane();
			// this.textPane.setWrapStyleWord(true);
			// this.textPane.setLineWrap(true);
			this.setPreferredSize(new Dimension(500, 800));
			textPane.setDragEnabled(true);
			textPane.setEditable(false);
			textPane.setTransferHandler(new TextViewTransferHandler(this));
			// textPane.setFont(textPane.getFont().deriveFont(0, 13));
			textPane.setHighlighter(hilit);

			add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

		}

		protected void initStyles() {
			Style speakerTagStyle = styleContext.addStyle("Speaker", null);
			speakerTagStyle.addAttribute(StyleConstants.Bold, true);
		}

		public void drawAnnotation(Mention a) {
			Object hi = highlightMap.get(a);
			Span span = new Span(a);
			if (hi != null) {
				hilit.removeHighlight(hi);
				spanCounter.subtract(span);
			}
			try {
				int n = spanCounter.get(span);
				hi = hilit.addHighlight(a.getBegin(), a.getEnd(),
						new UnderlinePainter(new Color(a.getEntity().getColor()), n * 3));
				spanCounter.add(span);
				highlightMap.put(a, hi);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public JTextComponent getTextPane() {
			return textPane;
		}

		public void drawAnnotations() {
			hilit.removeAllHighlights();
			highlightMap.clear();
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				drawAnnotation(m);
			}
		}

		class TextViewTransferHandler extends TransferHandler {

			private static final long serialVersionUID = 1L;

			CasTextView textView;

			public TextViewTransferHandler(CasTextView tv) {
				textView = tv;
			}

			@Override
			public int getSourceActions(JComponent comp) {
				return TransferHandler.LINK;
			}

			@Override
			public Transferable createTransferable(JComponent comp) {
				JTextComponent t = (JTextComponent) comp;
				return new PotentialAnnotationTransfer(textView, t.getSelectionStart(), t.getSelectionEnd());
			}

			@Override
			protected void exportDone(JComponent c, Transferable t, int action) {
				// TODO: export an Annotation object
				// drawAnnotations();
			}

			@Override
			public boolean canImport(TransferHandler.TransferSupport info) {

				if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {

				}
				return false;
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}
				JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
				dl.getDropPoint();

				int index = dl.getIndex();
				System.err.println(index);
				return false;
			}
		}

		@Override
		public void jcasLoaded(JCas jcas) {
			/*
			 * StringContent c = new
			 * StringContent(jcas.getDocumentText().length()); try {
			 * c.insertString(0, jcas.getDocumentText()); } catch
			 * (BadLocationException e) { e.printStackTrace(); } JCasDocument
			 * document = new JCasDocument(jcas);
			 * textPane.setStyledDocument(document);
			 */
			textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
			textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

		}

		@Override
		public void modelCreated(CoreferenceModel model, DocumentWindow dw) {
			textPane.addKeyListener(model);
			textPane.setCaretPosition(0);
		}

		@Override
		public void mentionAdded(Mention m) {
			drawAnnotation(m);
		}

		@Override
		public void mentionChanged(Mention m) {
			drawAnnotation(m);
		}

		@Override
		public void mentionRemoved(Mention m) {
			spanCounter.subtract(new Span(m));
			hilit.removeHighlight(highlightMap.get(m));
		}

		@Override
		public void mentionSelected(Mention m) {
			if (m != null) {
				textPane.setCaretPosition(m.getEnd());
				textPane.setSelectionStart(m.getBegin());
				textPane.setSelectionEnd(m.getEnd());
			} else {
				textPane.setSelectionStart(0);
				textPane.setSelectionEnd(0);
			}
		}

		public void switchStyle(JCas jcas, StyleVariant sv) {
			sv.style(jcas, textPane.getStyledDocument(), styleContext);

		}
	}

	class DetailsPanel extends JPanel
			implements TreeSelectionListener, TreeModelListener, LoadingListener, CaretListener {
		private static final long serialVersionUID = 1L;

		JTree tree;
		Map<Long, Mention> mentionCache;

		AbstractAction renameAction;
		AbstractAction changeKeyAction;
		AbstractAction changeColorAction;
		AbstractAction newEntityAction;
		AbstractAction deleteMentionAction;

		JButton renameActionButton;
		JButton changeKeyActionButton;
		JButton changeColorActionButton;
		JButton newEntityActionButton;
		JButton deleteMentionActionButton;

		public DetailsPanel() {
			super(new BorderLayout());

			this.initialiseUi();

		}

		protected void initialiseUi() {
			tree = new JTree();
			tree.getSelectionModel().addTreeSelectionListener(this);
			tree.setVisibleRowCount(-1);
			tree.setDragEnabled(true);
			tree.setLargeModel(true);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

			tree.setTransferHandler(new PanelTransferHandler());
			tree.setCellRenderer(new CellRenderer());

			add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

			JPanel controls = new JPanel();

			newEntityActionButton = new JButton("new");
			newEntityActionButton.setEnabled(false);
			renameActionButton = new JButton("rename");
			changeKeyActionButton = new JButton("change key");
			changeColorActionButton = new JButton("rename");
			deleteMentionActionButton = new JButton("delete mention");

			controls.add(newEntityActionButton);
			controls.add(renameActionButton);
			controls.add(changeKeyActionButton);
			controls.add(changeColorActionButton);
			controls.add(deleteMentionActionButton);
			this.add(controls, BorderLayout.NORTH);
		}

		private void displayDropLocation(final String string) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, string);
				}
			});
		}

		class CellRenderer extends DefaultTreeCellRenderer {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				JLabel s = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
						hasFocus);
				if (value instanceof EntityTreeNode) {
					EntityTreeNode etn = (EntityTreeNode) value;
					Entity e = etn.getFeatureStructure();
					s.setIcon(new ColorIcon(new Color(e.getColor())));
					if (etn.getKeyCode() != null) {
						s.setText(etn.getKeyCode() + ": " + s.getText() + " (" + etn.getChildCount() + ")");
					} else
						s.setText(s.getText() + " (" + etn.getChildCount() + ")");
				} else {
					s.setOpaque(false);
				}

				return s;
			}

		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath tp = e.getNewLeadSelectionPath();
			if (tp != null) {
				TreeNode<?> selection = (TreeNode<?>) tp.getLastPathComponent();
				renameAction.setEnabled(!(selection.isLeaf() || selection.isRoot()));
				changeKeyAction.setEnabled(!(selection.isLeaf() || selection.isRoot()));
				changeColorAction.setEnabled(!(selection.isLeaf() || selection.isRoot()));
				deleteMentionAction.setEnabled(selection.isLeaf());
			}
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			tree.expandPath(e.getTreePath());
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			tree.expandPath(e.getTreePath());

		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			tree.expandPath(e.getTreePath());

		}

		@Override
		public void jcasLoaded(JCas jcas) {

		}

		@Override
		public void modelCreated(CoreferenceModel model, DocumentWindow dw) {
			model.addTreeModelListener(this);

			renameAction = new RenameEntityAction(model, tree);
			renameAction.setEnabled(false);
			renameActionButton.setAction(renameAction);

			changeKeyAction = new ChangeKeyForEntityAction(model, tree);
			changeKeyAction.setEnabled(false);
			changeKeyActionButton.setAction(changeKeyAction);

			changeColorAction = new ChangeColorForEntity(model, tree);
			changeColorAction.setEnabled(false);
			changeColorActionButton.setAction(changeColorAction);

			newEntityAction = new NewEntityAction(model, dw.viewer.textPane);
			newEntityAction.setEnabled(true);
			newEntityActionButton.setAction(newEntityAction);

			deleteMentionAction = new DeleteMentionAction(model, tree);
			deleteMentionAction.setEnabled(true);
			deleteMentionActionButton.setAction(deleteMentionAction);

			tree.setModel(model);
			tree.addTreeSelectionListener(model);

			dw.viewer.textPane.addCaretListener(this);

		}

		class PanelTransferHandler extends TransferHandler {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canImport(TransferHandler.TransferSupport info) {
				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				if (dl.getPath() == null)
					return false;
				TreePath treePath = dl.getPath();
				TreeNode<?> selectedNode = (TreeNode<?>) treePath.getLastPathComponent();

				// new mention created in text view
				if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
					if (selectedNode.isLeaf())
						return false;
				}
				if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
					if (selectedNode.isLeaf() || selectedNode.isRoot())
						return false;
				}

				return true;
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				// Check for String flavor
				if (!info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)
						&& !info.isDataFlavorSupported(NodeTransferable.dataFlavor)) {
					displayDropLocation("List doesn't accept a drop of this type.");
					return false;
				}

				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				TreePath tp = dl.getPath();
				tree.expandPath(tp);

				try {
					if (info.getTransferable().getTransferDataFlavors()[0] == PotentialAnnotationTransfer.dataFlavor) {
						FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();
						PotentialAnnotation pa = (PotentialAnnotation) info.getTransferable()
								.getTransferData(PotentialAnnotationTransfer.dataFlavor);
						if (entity == null)
							((CoreferenceModel) tree.getModel()).addNewEntityMention(pa.getBegin(), pa.getEnd());
						else
							((CoreferenceModel) tree.getModel()).addNewMention((Entity) entity, pa.getBegin(),
									pa.getEnd());

					} else if (info.getTransferable().getTransferDataFlavors()[0] == NodeTransferable.dataFlavor) {
						FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();

						TreeNode<Mention> m = (TreeNode<Mention>) info.getTransferable()
								.getTransferData(NodeTransferable.dataFlavor);
						((CoreferenceModel) tree.getModel()).updateMention(m.getFeatureStructure(), (Entity) entity);

					}

				} catch (UnsupportedFlavorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}

			@Override
			public int getSourceActions(JComponent c) {
				return MOVE;
			}

			@Override
			public Transferable createTransferable(JComponent comp) {
				JTree tree = (JTree) comp;
				@SuppressWarnings("unchecked")
				TreeNode<Mention> tn = (TreeNode<Mention>) tree.getLastSelectedPathComponent();
				return new NodeTransferable<Mention>(tn);
			}

		}

		@Override
		public void caretUpdate(CaretEvent e) {
			newEntityAction.setEnabled(e.getDot() != e.getMark());
		}

	}
}
