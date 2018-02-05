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
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.commons.Counter;
import de.unistuttgart.ims.coref.annotator.action.FileImportCRETAAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportDKproAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportQuaDramAAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DocumentWindow extends JFrame
		implements CaretListener, TreeSelectionListener, TreeModelListener, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;
	private static final String HELP_MESSAGE = "Instructions for using Coref Annotator";

	JCas jcas;
	File file;
	static Logger logger = Annotator.logger;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// storing and caching
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	Counter<Span> spanCounter = new Counter<Span>();

	// actions
	AbstractAction newEntityAction;
	AbstractAction renameAction;
	AbstractAction changeKeyAction;
	AbstractAction changeColorAction;
	AbstractAction deleteMentionAction;
	AbstractAction formGroupAction;

	// controller
	CoreferenceModel cModel;

	// Window components
	JTree tree;
	JTextPane textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	StyleContext styleContext = new StyleContext();

	// Menu components
	JMenuBar menuBar = new JMenuBar();
	JMenu documentMenu;
	JMenu recentMenu;
	JMenu windowsMenu;

	public DocumentWindow(Annotator annotator) {
		super();
		this.mainApplication = annotator;
		this.initialiseActions();
		this.initialiseMenu();
		this.initialiseWindow();

	}

	/*
	 * Initialisation
	 */

	public void initialiseWindow() {
		// initialise panel
		JPanel rightPanel = new JPanel(new BorderLayout());
		tree = new JTree();
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setLargeModel(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setTransferHandler(new PanelTransferHandler());
		tree.setCellRenderer(new CellRenderer());
		tree.addTreeSelectionListener(this);

		rightPanel.add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		JPanel controls = new JPanel();

		controls.add(new JButton(newEntityAction));
		controls.add(new JButton(renameAction));
		controls.add(new JButton(changeKeyAction));
		controls.add(new JButton(changeColorAction));
		controls.add(new JButton(deleteMentionAction));
		rightPanel.add(controls, BorderLayout.NORTH);

		// initialise text view
		JPanel leftPanel = new JPanel(new BorderLayout());
		hilit = new DefaultHighlighter();
		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setTransferHandler(new TextViewTransferHandler());
		textPane.setHighlighter(hilit);

		leftPanel.add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

		// split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

		getContentPane().add(splitPane);
		pack();
		setVisible(true);
	}

	protected void initialiseActions() {
		this.renameAction = new RenameEntityAction();
		this.newEntityAction = new NewEntityAction();
		this.changeColorAction = new ChangeColorForEntity();
		this.changeKeyAction = new ChangeKeyForEntityAction();
		this.deleteMentionAction = new DeleteMentionAction();
		this.formGroupAction = new FormEntityGroup();

		// disable all at the beginning
		newEntityAction.setEnabled(false);
		renameAction.setEnabled(false);
		changeKeyAction.setEnabled(false);
		changeColorAction.setEnabled(false);
		deleteMentionAction.setEnabled(false);
		formGroupAction.setEnabled(false);
	}

	protected JMenu initialiseMenuView() {
		JMenu viewMenu = new JMenu("View");
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
		return viewMenu;

	}

	protected JMenu initialiseMenuTools() {
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.add(new JMenuItem(new ShowSearchPanelAction(mainApplication, this)));

		return toolsMenu;
	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu("Import from ...");
		fileImportMenu.add(new FileImportQuaDramAAction(mainApplication));
		fileImportMenu.add(new FileImportDKproAction(mainApplication));
		fileImportMenu.add(new FileImportCRETAAction(mainApplication));

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new FileOpenAction(mainApplication));
		fileMenu.add(fileImportMenu);
		fileMenu.add(new FileSaveAction(this));
		fileMenu.add(new FileSaveAsAction());
		fileMenu.add(new JMenuItem(new CloseAction()));
		fileMenu.add(new JMenuItem(new ExitAction()));

		return fileMenu;
	}

	protected JMenu initialiseMenuEntity() {
		JMenu entityMenu = new JMenu("Entities");
		entityMenu.add(new JMenuItem(renameAction));
		entityMenu.add(new JMenuItem(newEntityAction));
		entityMenu.add(new JMenuItem(changeColorAction));
		entityMenu.add(new JMenuItem(changeKeyAction));
		entityMenu.add(new JMenuItem(formGroupAction));
		return entityMenu;
	}

	protected void initialiseMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("Could not set look and feel {}.", e.getMessage());
		}

		// top level menus
		JMenu helpMenu = new JMenu("Help");
		// JMenu debugMenu = new JMenu("Debug");
		windowsMenu = new JMenu("Windows");
		if (segmentAnnotation != null) {
			documentMenu = new JMenu("Document");
			documentMenu.setEnabled(segmentAnnotation != null);
		}

		// Menu Items
		JMenuItem aboutMenuItem = new JMenuItem("About");
		JMenuItem helpMenuItem = new JMenuItem("Help");
		recentMenu = new JMenu("Open Recent");

		menuBar.add(initialiseMenuFile());
		menuBar.add(initialiseMenuEntity());
		menuBar.add(initialiseMenuView());
		menuBar.add(initialiseMenuTools());
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

	protected void closeWindow(boolean quit) {
		mainApplication.close(this);
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

	public void drawAnnotations() {
		hilit.removeAllHighlights();
		highlightMap.clear();
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			drawAnnotation(m);
		}
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
		saveToFile(file);
	}

	public synchronized void saveToFile(File f) {
		try {
			XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(f));
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
		switchStyle(jcas, StyleVariant.select(flavor));

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
		this.cModel.addCoreferenceModelListener(this);
		this.cModel.importExistingData();
		this.fireModelCreatedEvent();
	}

	public JCas getJcas() {
		return jcas;
	}

	public Annotator getMainApplication() {
		return mainApplication;
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
			int oldSize = textPane.getFont().getSize();
			textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, oldSize - 1));
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
			int oldSize = textPane.getFont().getSize();
			textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, oldSize + 1));
		}

	}

	class ViewStyleQuaDramAAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewStyleQuaDramAAction() {
			putValue(Action.NAME, "QuaDramA");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switchStyle(jcas, StyleVariant.QuaDramA);
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
			switchStyle(jcas, styleVariant);

		}

	}

	class ViewStyleDefaultAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewStyleDefaultAction() {
			putValue(Action.NAME, "Default");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switchStyle(jcas, StyleVariant.Default);
		}

	}

	protected void fireModelCreatedEvent() {
		cModel.addTreeModelListener(this);
		tree.setModel(cModel);
		textPane.addKeyListener(cModel);
		textPane.setCaretPosition(0);

		textPane.addCaretListener(this);

	}

	protected void fireJCasLoadedEvent() {
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

	}

	@Override
	public void caretUpdate(CaretEvent e) {
		newEntityAction.setEnabled(
				!(textPane.getSelectedText() == null || textPane.getSelectionStart() == textPane.getSelectionEnd()));
	}

	public void switchStyle(JCas jcas, StyleVariant sv) {
		sv.style(jcas, textPane.getStyledDocument(), styleContext);

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		int numberOfSelections = tree.getSelectionPaths().length;
		TreePath tp = e.getNewLeadSelectionPath();
		if (numberOfSelections == 0) {
			renameAction.setEnabled(false);
			changeKeyAction.setEnabled(false);
			changeColorAction.setEnabled(false);
			deleteMentionAction.setEnabled(false);
			formGroupAction.setEnabled(false);
		} else if (numberOfSelections == 1) {
			formGroupAction.setEnabled(false);

			if (tp != null) {
				TreeNode<?> selection = (TreeNode<?>) tree.getSelectionPath().getLastPathComponent();

				FeatureStructure fs = selection.getFeatureStructure();
				renameAction.setEnabled(fs instanceof Entity);
				changeKeyAction.setEnabled(fs instanceof Entity);
				changeColorAction.setEnabled(fs instanceof Entity);
				deleteMentionAction.setEnabled(fs instanceof Mention);
				if (fs instanceof Mention)
					mentionSelected((Mention) fs);
			}
		} else if (numberOfSelections == 2) {
			renameAction.setEnabled(false);
			changeKeyAction.setEnabled(false);
			changeColorAction.setEnabled(false);
			deleteMentionAction.setEnabled(false);

			TreePath[] paths = tree.getSelectionPaths();
			formGroupAction
					.setEnabled(((TreeNode<?>) paths[0].getLastPathComponent()).getFeatureStructure() instanceof Entity
							&& ((TreeNode<?>) paths[1].getLastPathComponent()).getFeatureStructure() instanceof Entity);
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
			FeatureStructure fs = selectedNode.getFeatureStructure();

			// new mention created in text view
			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
				if (fs instanceof Mention)
					return false;
			}
			if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
				if (fs instanceof Mention || fs instanceof TOP)
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
						cModel.addNewEntityMention(pa.getBegin(), pa.getEnd());
					else
						cModel.addNewMention((Entity) entity, pa.getBegin(), pa.getEnd());

				} else if (info.getTransferable().getTransferDataFlavors()[0] == NodeTransferable.dataFlavor) {
					FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();

					TreeNode<Mention> m = (TreeNode<Mention>) info.getTransferable()
							.getTransferData(NodeTransferable.dataFlavor);
					cModel.updateMention(m.getFeatureStructure(), (Entity) entity);

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
			return MOVE | LINK;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Transferable createTransferable(JComponent comp) {
			JTree tree = (JTree) comp;
			TreeNode<?> tn = (TreeNode<?>) tree.getLastSelectedPathComponent();
			if (tn.getFeatureStructure() instanceof Mention)
				return new NodeTransferable<Mention>((TreeNode<Mention>) tn);
			if (tn.getFeatureStructure() instanceof Entity)
				return new NodeTransferable<Entity>((TreeNode<Entity>) tn);
			return null;
		}

	}

	class RenameEntityAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RenameEntityAction() {
			putValue(Action.NAME, "Rename");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String name = JOptionPane.showInputDialog("Enter the new name:");
			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			etn.getFeatureStructure().setLabel(name);
			cModel.nodeChanged(etn);
		}

	}

	class ChangeColorForEntity extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChangeColorForEntity() {
			putValue(Action.NAME, "Color");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			Color color = new Color(etn.getFeatureStructure().getColor());

			Color newColor = JColorChooser.showDialog(null, "Choose new color", color);
			cModel.updateColor(etn.getFeatureStructure(), newColor);
		}

	}

	class ChangeKeyForEntityAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChangeKeyForEntityAction() {
			putValue(Action.NAME, "Shortcut");

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String key = JOptionPane.showInputDialog("Enter the new key");
			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();

			char keyCode = key.charAt(0);
			System.err.println(KeyEvent.getKeyText(keyCode) + " " + keyCode);
			cModel.reassignKey(keyCode, etn.getFeatureStructure());
		}

	}

	class NewEntityAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewEntityAction() {
			putValue(Action.NAME, "New");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.addNewEntityMention(textPane.getSelectionStart(), textPane.getSelectionEnd());
		}

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

	class DeleteMentionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public DeleteMentionAction() {
			putValue(Action.NAME, "Delete");

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			TreeNode<Mention> tn = (TreeNode<Mention>) tree.getLastSelectedPathComponent();
			cModel.removeMention(tn.getFeatureStructure());
		}

	}

	class TextViewTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public int getSourceActions(JComponent comp) {
			return TransferHandler.LINK;
		}

		@Override
		public Transferable createTransferable(JComponent comp) {
			JTextComponent t = (JTextComponent) comp;
			return new PotentialAnnotationTransfer(textPane, t.getSelectionStart(), t.getSelectionEnd());
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

	class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			putValue(Action.NAME, "Close");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			closeWindow(false);
		}

	}

	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExitAction() {
			putValue(Action.NAME, "Quit");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mainApplication.handleQuitRequestWith(new QuitEvent(), new QuitResponse());
		}

	}

	class FormEntityGroup extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FormEntityGroup() {
			putValue(Action.NAME, "Form entity group");
		}

		@Override
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			Entity e1 = ((TreeNode<Entity>) tree.getSelectionPaths()[0].getLastPathComponent()).getFeatureStructure();
			Entity e2 = ((TreeNode<Entity>) tree.getSelectionPaths()[1].getLastPathComponent()).getFeatureStructure();
			cModel.formGroup(e1, e2);
		}

	}

	class FileSaveAsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public FileSaveAsAction() {
			putValue(Action.NAME, "Save as ...");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser saveDialog = new JFileChooser(file.getParentFile());
			saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			saveDialog.setFileFilter(XmiFileFilter.filter);
			saveDialog.setDialogTitle("Save file as ...");
			int r = saveDialog.showSaveDialog(DocumentWindow.this);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				saveToFile(f);
				break;
			default:
			}

		}
	}

}
