package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.action.ShowMentionInTreeAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.action.ToggleFullTokensAction;
import de.unistuttgart.ims.coref.annotator.action.ToggleTrimWhitespaceAction;
import de.unistuttgart.ims.coref.annotator.api.AnnotationComment;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.api.Meta;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.Plugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class DocumentWindow extends JFrame implements CaretListener, TreeModelListener, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	JCas jcas;
	File file;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// storing and caching
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	RangedCounter spanCounter = new RangedCounter();
	boolean unsavedChanges = false;
	Feature titleFeature;

	// actions
	AbstractAction commentAction = new CommentAction(null);
	AbstractAction newEntityAction;
	AbstractAction renameAction;
	AbstractAction changeKeyAction;
	AbstractAction changeColorAction;
	DeleteAction deleteAction;
	AbstractAction formGroupAction;
	ToggleMentionDifficult toggleMentionDifficult;
	ToggleMentionAmbiguous toggleMentionAmbiguous;
	ToggleEntityGeneric toggleEntityGeneric;
	AbstractAction sortByAlpha;
	AbstractAction sortByMentions, sortDescending = new ToggleEntitySortOrder();
	AbstractAction fileSaveAction;
	AbstractAction toggleTrimWhitespace, toggleShowTextInTreeLabels, closeAction = new CloseAction();

	// controller
	CoreferenceModel cModel;
	HighlightManager highlightManager;

	// Window components
	JTree tree;
	JTextPane textPane;
	StyleContext styleContext = new StyleContext();
	JLabel selectionDetailPanel;
	JPanel statusBar;
	JProgressBar progressBar;
	JSplitPane splitPane;
	JLabel styleLabel;
	JTextField treeSearchField;

	// Menu components
	JMenuBar menuBar = new JMenuBar();
	JMenu documentMenu;
	JMenu recentMenu;
	JMenu windowsMenu;
	JPopupMenu treePopupMenu;
	JPopupMenu textPopupMenu;
	Map<StylePlugin, JRadioButtonMenuItem> styleMenuItem = new HashMap<StylePlugin, JRadioButtonMenuItem>();

	// Settings
	boolean trimWhitespace = true;

	public DocumentWindow(Annotator annotator) {
		super();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Annotator.logger.error("Could not set look and feel {}.", e.getMessage());
		}

		this.mainApplication = annotator;
		this.initialiseActions();
		this.initialiseMenu();
		this.initialiseWindow();
		this.setVisible(true);

	}

	/*
	 * Initialisation
	 */

	protected void initialiseWindow() {
		// popup
		treePopupMenu = new JPopupMenu();
		// treePopupMenu.add(this.commentAction);
		treePopupMenu.add(this.deleteAction);
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString("menu.edit.mentions"));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionAmbiguous));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionDifficult));
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString("menu.edit.entities"));
		treePopupMenu.add(this.newEntityAction);
		treePopupMenu.add(this.renameAction);
		treePopupMenu.add(this.changeColorAction);
		treePopupMenu.add(this.changeKeyAction);
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleEntityGeneric));

		textPopupMenu = new JPopupMenu();
		textPopupMenu.addPopupMenuListener(new PopupListener());

		// initialise panel
		JPanel rightPanel = new JPanel(new BorderLayout());
		tree = new JTree();
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setLargeModel(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setTransferHandler(new PanelTransferHandler());
		tree.setCellRenderer(new CellRenderer());
		tree.addTreeSelectionListener(new MyTreeSelectionListener());
		tree.addMouseListener(new TreeMouseListener());
		tree.addKeyListener(new TreeKeyListener());

		treeSearchField = new JTextField();
		treeSearchField.getDocument().addDocumentListener(new EntityFinder());
		treeSearchField.addKeyListener(new EntityFinder());
		rightPanel.setPreferredSize(new Dimension(300, 800));
		rightPanel.add(treeSearchField, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// tool bar
		JToolBar controls = new JToolBar();
		controls.setFocusable(false);
		controls.setRollover(true);
		controls.add(newEntityAction);
		controls.add(renameAction);
		controls.add(changeKeyAction);
		controls.add(changeColorAction);
		controls.add((deleteAction));
		controls.add((formGroupAction));
		getContentPane().add(controls, BorderLayout.NORTH);

		for (Component comp : controls.getComponents())
			comp.setFocusable(false);

		// status bar
		SpringLayout springs = new SpringLayout();
		statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(800, 20));
		statusBar.setLayout(springs);

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setPreferredSize(new Dimension(300, 20));

		statusBar.add(progressBar);

		styleLabel = new JLabel();
		styleLabel.setText("Style: " + mainApplication.getPluginManager().getDefaultStylePlugin().getName());
		styleLabel.setToolTipText(mainApplication.getPluginManager().getDefaultStylePlugin().getDescription());
		styleLabel.setPreferredSize(new Dimension(150, 20));
		statusBar.add(styleLabel);

		JLabel versionLabel = new JLabel(Annotator.class.getPackage().getImplementationTitle() + " "
				+ Annotator.class.getPackage().getImplementationVersion());
		versionLabel.setPreferredSize(new Dimension(220, 20));
		statusBar.add(versionLabel);

		springs.putConstraint(SpringLayout.EAST, versionLabel, 10, SpringLayout.EAST, statusBar);
		springs.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, statusBar);
		springs.putConstraint(SpringLayout.EAST, styleLabel, 10, SpringLayout.WEST, versionLabel);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.revalidate();

		// initialise text view
		Caret caret = new Caret();
		JPanel leftPanel = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setTransferHandler(new TextViewTransferHandler());
		textPane.addMouseListener(new TextMouseListener());
		textPane.setCaret(caret);
		textPane.getCaret().setVisible(true);
		textPane.addFocusListener(caret);
		highlightManager = new HighlightManager(textPane);

		leftPanel.add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// split pane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setVisible(false);
		splitPane.setDividerLocation(500);
		getContentPane().add(splitPane);

		setPreferredSize(new Dimension(800, 800));
		pack();
		this.setLocationRelativeTo(null);
		Annotator.logger.info("Window initialised.");
	}

	protected void initialiseActions() {
		this.renameAction = new RenameEntityAction();
		this.newEntityAction = new NewEntityAction();
		this.changeColorAction = new ChangeColorForEntity();
		this.changeKeyAction = new ChangeKeyForEntityAction();
		this.deleteAction = new DeleteAction();
		this.formGroupAction = new FormEntityGroup();
		this.toggleMentionDifficult = new ToggleMentionDifficult();
		this.toggleMentionAmbiguous = new ToggleMentionAmbiguous();
		this.toggleEntityGeneric = new ToggleEntityGeneric();
		this.sortByAlpha = new SortTreeByAlpha();
		this.sortByMentions = new SortTreeByMentions();
		this.fileSaveAction = new FileSaveAction(this);
		this.toggleTrimWhitespace = new ToggleTrimWhitespaceAction(mainApplication);
		this.toggleShowTextInTreeLabels = new ToggleShowTextInTreeLabels();

		// disable some at the beginning
		newEntityAction.setEnabled(false);
		renameAction.setEnabled(false);
		changeKeyAction.setEnabled(false);
		changeColorAction.setEnabled(false);
		deleteAction.setEnabled(false);
		formGroupAction.setEnabled(false);
		toggleMentionDifficult.setEnabled(false);
		toggleMentionAmbiguous.setEnabled(false);
		toggleEntityGeneric.setEnabled(false);
		Annotator.logger.info("Actions initialised.");

	}

	protected JMenu initialiseMenuView() {
		JMenu viewMenu = new JMenu(Annotator.getString("menu.view"));
		viewMenu.add(new JMenuItem(new ViewFontSizeDecreaseAction()));
		viewMenu.add(new JMenuItem(new ViewFontSizeIncreaseAction()));
		viewMenu.addSeparator();

		PluginManager pm = mainApplication.getPluginManager();

		JMenu viewStyleMenu = new JMenu(Annotator.getString("menu.view.style"));
		ButtonGroup grp = new ButtonGroup();
		StylePlugin pl = pm.getDefaultStylePlugin();
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(new ViewStyleSelectAction(pm.getDefaultStylePlugin()));
		radio1.setSelected(true);
		viewStyleMenu.add(radio1);
		styleMenuItem.put(pl, radio1);
		grp.add(radio1);
		for (Class<? extends StylePlugin> plugin : pm.getStylePlugins()) {
			pl = pm.getStylePlugin(plugin);
			radio1 = new JRadioButtonMenuItem(new ViewStyleSelectAction(pl));
			viewStyleMenu.add(radio1);
			styleMenuItem.put(pl, radio1);
			grp.add(radio1);

		}
		viewMenu.add(viewStyleMenu);
		return viewMenu;

	}

	protected JMenu initialiseMenuSettings() {
		JMenu menu = new JMenu(Annotator.getString("menu.settings"));
		menu.add(new JCheckBoxMenuItem(toggleTrimWhitespace));
		menu.add(new JCheckBoxMenuItem(toggleShowTextInTreeLabels));
		menu.add(new JCheckBoxMenuItem(new ToggleFullTokensAction(this.mainApplication)));
		return menu;

	}

	protected JMenu initialiseMenuTools() {
		JMenu toolsMenu = new JMenu(Annotator.getString("menu.tools"));
		toolsMenu.add(new JMenuItem(new ShowSearchPanelAction(mainApplication, this)));
		return toolsMenu;
	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu(Annotator.getString("menu.file.import_from"));
		JMenu fileExportMenu = new JMenu(Annotator.getString("menu.file.export_as"));

		PluginManager pm = mainApplication.getPluginManager();
		for (Class<? extends IOPlugin> pluginClass : pm.getIOPlugins()) {
			try {
				IOPlugin plugin = pm.getIOPlugin(pluginClass);
				if (plugin.getImporter() != null)
					fileImportMenu.add(new FileImportAction(mainApplication, plugin));
				if (plugin.getExporter() != null)
					fileExportMenu.add(new FileExportAction(plugin));
			} catch (ResourceInitializationException e) {
				Annotator.logger.catching(e);
			}

		}

		JMenu fileMenu = new JMenu(Annotator.getString("menu.file"));
		fileMenu.add(new FileOpenAction(mainApplication));
		fileMenu.add(mainApplication.getRecentFilesMenu());
		fileMenu.add(fileImportMenu);
		fileMenu.add(fileSaveAction);
		fileMenu.add(new FileSaveAsAction());
		fileMenu.add(fileExportMenu);
		fileMenu.add(closeAction);
		fileMenu.add(mainApplication.quitAction);

		return fileMenu;
	}

	protected JMenu initialiseMenuEntity() {
		JMenu entityMenu = new JMenu(Annotator.getString("menu.edit"));
		entityMenu.add(new JMenuItem(deleteAction));
		// entityMenu.add(new JMenuItem(commentAction));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString("menu.edit.mentions"));
		entityMenu.add(new JCheckBoxMenuItem(toggleMentionAmbiguous));
		entityMenu.add(new JCheckBoxMenuItem(toggleMentionDifficult));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString("menu.edit.entities"));
		entityMenu.add(new JMenuItem(newEntityAction));
		entityMenu.add(new JMenuItem(renameAction));
		entityMenu.add(new JMenuItem(changeColorAction));
		entityMenu.add(new JMenuItem(changeKeyAction));
		entityMenu.add(new JMenuItem(formGroupAction));
		entityMenu.add(new JCheckBoxMenuItem(toggleEntityGeneric));

		JMenu sortMenu = new JMenu(Annotator.getString("menu.edit.entities.sort"));
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(this.sortByAlpha);
		radio1.setSelected(true);
		JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(this.sortByMentions);
		ButtonGroup grp = new ButtonGroup();
		grp.add(radio2);
		grp.add(radio1);

		sortMenu.add(radio1);
		sortMenu.add(radio2);
		sortMenu.add(new JCheckBoxMenuItem(this.sortDescending));

		entityMenu.add(sortMenu);
		return entityMenu;
	}

	protected void initialiseMenu() {

		JMenu helpMenu = new JMenu(Annotator.getString("menu.help"));
		helpMenu.add(mainApplication.helpAction);

		menuBar.add(initialiseMenuFile());
		menuBar.add(initialiseMenuEntity());
		menuBar.add(initialiseMenuView());
		menuBar.add(initialiseMenuTools());
		menuBar.add(initialiseMenuSettings());
		// if (segmentAnnotation != null)
		// menuBar.add(documentMenu);
		// menuBar.add(windowsMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		// window events
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		});

		Annotator.logger.info("Initialised menu bar.");
	}

	protected void closeWindow(boolean quit) {
		if (unsavedChanges) {
			Annotator.logger.debug("Closing window with unsaved changes");
		}
		mainApplication.close(this);
	}

	public void loadFile(File file, IOPlugin flavor) {
		if (flavor instanceof DefaultIOPlugin)
			this.file = file;
		else
			this.fileSaveAction.setEnabled(false);

		LoadAndImport lai;
		try {
			lai = new LoadAndImport(this, file, TypeSystemDescriptionFactory.createTypeSystemDescription(), flavor);
			lai.execute();
		} catch (ResourceInitializationException e) {
			Annotator.logger.catching(e);
		}

	}

	public synchronized void saveCurrentFile() {
		if (file != null)
			saveToFile(file, mainApplication.getPluginManager().getDefaultIOPlugin());
	}

	public synchronized void saveToFile(File f, IOPlugin plugin) {
		progressBar.setValue(0);
		progressBar.setVisible(true);

		new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				SimplePipeline.runPipeline(jcas, plugin.getExporter());
				XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(f));
				return new Object();
			}

			@Override
			protected void done() {
				progressBar.setValue(100);
				progressBar.setVisible(false);
				file = f;
				unsavedChanges = false;
				setWindowTitle();
			}

		}.execute();
	}

	public JCas getJcas() {
		return jcas;
	}

	public Annotator getMainApplication() {
		return mainApplication;
	}

	class ViewFontSizeDecreaseAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ViewFontSizeDecreaseAction() {
			super(Material.EXPOSURE_NEG_1);
			putValue(Action.NAME, Annotator.getString("action.view.decrease_font_size"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Font oldFont = textPane.getFont();
			float oldSize = oldFont.getSize();
			textPane.setFont(oldFont.deriveFont(oldSize - 1f));
		}

	}

	class ViewFontSizeIncreaseAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ViewFontSizeIncreaseAction() {
			super(Material.EXPOSURE_PLUS_1);
			putValue(Action.NAME, Annotator.getString("action.view.increase_font_size"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Font oldFont = textPane.getFont();
			float oldSize = oldFont.getSize();
			textPane.setFont(oldFont.deriveFont(oldSize + 1f));
		}

	}

	class ViewStyleSelectAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		StylePlugin styleVariant;

		public ViewStyleSelectAction(StylePlugin style) {
			putValue(Action.NAME, style.getName());
			styleVariant = style;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switchStyle(styleVariant);

		}

	}

	class SortTreeByAlpha extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByAlpha() {
			super(MaterialDesign.MDI_SORT_ALPHABETICAL);
			putValue(Action.NAME, Annotator.getString("action.sort_alpha"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder = EntitySortOrder.Alphabet;
			cModel.resort();
		}

	}

	class SortTreeByMentions extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByMentions() {
			super(MaterialDesign.MDI_SORT_NUMERIC);
			putValue(Action.NAME, Annotator.getString("action.sort_mentions"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.sort_mentions.tooltip"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder = EntitySortOrder.Mentions;
			cModel.entitySortOrder.descending = true;
			sortDescending.putValue(Action.SELECTED_KEY, true);
			cModel.resort();
		}

	}

	protected void setWindowTitle() {
		String fileName = (file != null ? file.getName() : Annotator.getString("windowtitle.new_file"));
		String documentTitle;
		if (titleFeature != null)
			documentTitle = jcas.getDocumentAnnotationFs().getFeatureValueAsString(titleFeature);
		else
			documentTitle = "Untitled document";
		setTitle(documentTitle + " (" + fileName + ")"
				+ (unsavedChanges ? " -- " + Annotator.getString("windowtitle.edited") : ""));
	}

	protected synchronized void registerChange() {
		if (unsavedChanges == false) {
			unsavedChanges = true;
			setWindowTitle();
			fileSaveAction.setEnabled(file != null);
		}
	}

	protected void fireModelCreatedEvent() {
		cModel.addTreeModelListener(this);
		tree.setModel(cModel);
		textPane.addKeyListener(new TextViewKeyListener());
		textPane.setCaretPosition(0);
		textPane.addCaretListener(this);
		progressBar.setValue(100);
		Annotator.logger.debug("Setting loading progress to {}", 100);
		splitPane.setVisible(true);
		progressBar.setVisible(false);
	}

	@SuppressWarnings("unchecked")
	protected void fireJCasLoadedEvent() {
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

		Meta meta = Util.getMeta(jcas);

		if (meta.getStylePlugin() != null) {
			Object o;
			try {
				Class<?> cl = Class.forName(meta.getStylePlugin());
				o = mainApplication.getPluginManager().getPlugin((Class<? extends Plugin>) cl);
				if (o instanceof StylePlugin)
					switchStyle((StylePlugin) o);
			} catch (ClassNotFoundException e) {
				Annotator.logger.catching(e);
			}

		} else // if (flavor.getStylePlugin() != null)
			switchStyle(mainApplication.getPluginManager().getDefaultStylePlugin());

		titleFeature = jcas.getTypeSystem().getFeatureByFullName(
				mainApplication.getPreferences().get(Constants.CFG_WINDOWTITLE, Defaults.CFG_WINDOWTITLE));

		setWindowTitle();

		InitializeModel im = new InitializeModel(jcas);
		im.execute();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		newEntityAction.setEnabled(
				!(textPane.getSelectedText() == null || textPane.getSelectionStart() == textPane.getSelectionEnd()));
	}

	public void switchStyle(StylePlugin sv) {
		try {
			Annotator.logger.info("Switching to style {}", sv.getClass().getName());
			if (sv.getBaseStyle() != null)
				StyleManager.style(textPane.getStyledDocument(), sv.getBaseStyle());
			else
				StyleManager.style(textPane.getStyledDocument(), StyleManager.getDefaultStyle());
			Map<Style, org.apache.uima.cas.Type> styles = sv.getSpanStyles(jcas.getTypeSystem(), styleContext,
					StyleManager.getDefaultStyle());
			if (styles != null)
				for (Style style : styles.keySet()) {
					StyleManager.style(jcas, textPane.getStyledDocument(), style, styles.get(style));
				}
			Util.getMeta(jcas).setStylePlugin(sv.getClass().getName());
			styleMenuItem.get(sv).setSelected(true);
			styleLabel.setText("Style: " + sv.getName());
			styleLabel.setToolTipText(sv.getDescription());
			styleLabel.repaint();
		} catch (NullPointerException e) {
			Annotator.logger.catching(e);
		}
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		if (e.getTreePath().getLastPathComponent() instanceof EntityGroup)
			tree.expandPath(e.getTreePath());
		try {
			tree.repaint(tree.getPathBounds(e.getTreePath()));
		} catch (NullPointerException ex) {
			Annotator.logger.catching(ex);
		}
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		tree.repaint(tree.getPathBounds(e.getTreePath()));
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {

	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		tree.expandPath(e.getTreePath());
	}

	@Override
	public void annotationSelected(Annotation m) {
		if (m != null) {
			textPane.setSelectionStart(m.getBegin());
			textPane.setSelectionEnd(m.getEnd());
			// textPane.setCaretPosition(m.getEnd());
			textPane.getCaret().setSelectionVisible(true);
		} else {
			textPane.getCaret().setSelectionVisible(false);
		}
	}

	@Override
	public void mentionAdded(Annotation m) {
		highlightManager.draw(m);
	}

	@Override
	public void annotationChanged(Annotation m) {
		highlightManager.draw(m);
	}

	@Override
	public void annotationRemoved(Annotation m) {
		highlightManager.undraw(m);
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
			CATreeNode selectedNode = (CATreeNode) treePath.getLastPathComponent();
			FeatureStructure fs = selectedNode.getFeatureStructure();

			// new mention created in text view
			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
				if (selectedNode == cModel.groupRootNode)
					return false;
			}
			// move existing mention
			if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
				if (fs instanceof TOP)
					return false;
			}

			return true;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}

			// Check for flavor
			if (!info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)
					&& !info.isDataFlavorSupported(NodeTransferable.dataFlavor)) {
				return false;
			}

			JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
			TreePath tp = dl.getPath();
			// tree.expandPath(tp.getParentPath());
			DataFlavor dataFlavor = info.getTransferable().getTransferDataFlavors()[0];

			try {

				FeatureStructure targetFs = ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure();
				if (dataFlavor == PotentialAnnotationTransfer.dataFlavor) {
					PotentialAnnotation pa = (PotentialAnnotation) info.getTransferable()
							.getTransferData(PotentialAnnotationTransfer.dataFlavor);
					if (targetFs == null)
						cModel.add(pa.getBegin(), pa.getEnd());
					else if (targetFs instanceof Entity)
						cModel.addTo((Entity) targetFs, pa.getBegin(), pa.getEnd());
					else if (targetFs instanceof Mention)
						cModel.addTo((Mention) targetFs, pa.getBegin(), pa.getEnd());
					registerChange();

				} else if (dataFlavor == NodeTransferable.dataFlavor) {
					CATreeNode object = (CATreeNode) info.getTransferable()
							.getTransferData(NodeTransferable.dataFlavor);
					FeatureStructure droppedFs = object.getFeatureStructure();
					if (targetFs instanceof EntityGroup && droppedFs instanceof Entity) {
						cModel.addTo((EntityGroup) targetFs, (Entity) droppedFs);
					} else if (targetFs instanceof Entity && droppedFs instanceof Mention) {
						cModel.moveTo((Mention) droppedFs, (Entity) targetFs);
					} else if (targetFs instanceof Mention && droppedFs instanceof DetachedMentionPart) {
						DetachedMentionPart dmp = cModel
								.removeFrom((Mention) ((CATreeNode) object.getParent()).getFeatureStructure());
						cModel.addTo((Mention) targetFs, dmp);
					}
					registerChange();

				}

			} catch (Exception e1) {
				Annotator.logger.catching(e1);
			}

			return true;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return MOVE | COPY;
		}

		@Override
		public Transferable createTransferable(JComponent comp) {
			JTree tree = (JTree) comp;
			CATreeNode tn = (CATreeNode) tree.getLastSelectedPathComponent();

			if (tn.getFeatureStructure() instanceof Entity || tn.getFeatureStructure() instanceof Mention
					|| tn.getFeatureStructure() instanceof DetachedMentionPart)
				return new NodeTransferable(tn);
			return null;
		}

	}

	class RenameEntityAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public RenameEntityAction() {
			super(Material.EDIT);
			putValue(Action.NAME, Annotator.getString("action.rename"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.rename.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			String l = etn.getFeatureStructure().getLabel();
			String newLabel = (String) JOptionPane.showInputDialog(DocumentWindow.this,
					Annotator.getString("dialog.rename_entity.prompt"), "", JOptionPane.PLAIN_MESSAGE,
					FontIcon.of(Material.KEYBOARD), null, l);
			if (newLabel != null) {
				etn.getFeatureStructure().setLabel(newLabel);
				cModel.nodeChanged(etn);
				registerChange();

			}
		}

	}

	class ChangeColorForEntity extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ChangeColorForEntity() {
			super(Material.COLOR_LENS);
			putValue(Action.NAME, Annotator.getString("action.set_color"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.set_color.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			Color color = new Color(etn.getFeatureStructure().getColor());

			Color newColor = JColorChooser.showDialog(DocumentWindow.this,
					Annotator.getString("dialog.change_color.prompt"), color);
			if (color != newColor) {
				cModel.updateColor(etn.getFeatureStructure(), newColor);
				registerChange();
			}

		}

	}

	class ChangeKeyForEntityAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ChangeKeyForEntityAction() {
			super(Material.KEYBOARD);
			putValue(Action.NAME, Annotator.getString("action.set_shortcut"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.set_shortcut.tooltip"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.KEYBOARD));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.KEYBOARD));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			Character ch = etn.getKeyCode();
			String newKey = (String) JOptionPane.showInputDialog(DocumentWindow.this,
					Annotator.getString("dialog.change_key.prompt"), "", JOptionPane.PLAIN_MESSAGE,
					FontIcon.of(Material.KEYBOARD), null, ch);
			if (newKey != null)
				if (newKey.length() == 1) {
					Character newChar = newKey.charAt(0);
					etn.getFeatureStructure().setKey(newKey.substring(0, 1));
					cModel.reassignKey(newChar, etn.getFeatureStructure());
					registerChange();

				} else {
					JOptionPane.showMessageDialog(DocumentWindow.this,
							Annotator.getString("dialog.change_key.invalid_string.message"),
							Annotator.getString("dialog.change_key.invalid_string.title"),
							JOptionPane.INFORMATION_MESSAGE);
				}
		}

	}

	class NewEntityAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public NewEntityAction() {
			super(Material.PERSON_ADD);
			putValue(Action.NAME, Annotator.getString("action.new"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.new.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.add(textPane.getSelectionStart(), textPane.getSelectionEnd());
			registerChange();

		}

	}

	class CellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			// TODO: split up the code in multiple classes
			boolean showText = mainApplication.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setOpaque(false);
			JLabel lab1 = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);
			panel.add(lab1);
			String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

			lab1.setText(stringValue);
			CATreeNode catn = null;
			if (value instanceof CATreeNode)
				catn = (CATreeNode) value;
			if (value instanceof EntityTreeNode) {
				EntityTreeNode etn = (EntityTreeNode) value;
				Entity e = etn.getFeatureStructure();
				if (!etn.isVisible()) {
					lab1.setForeground(Color.GRAY);
				} else {
					lab1.setForeground(Color.BLACK);
				}
				lab1.setIcon(FontIcon.of(Material.PERSON, new Color(e.getColor())));
				if (etn.getKeyCode() != null) {
					lab1.setText(etn.getKeyCode() + ": " + e.getLabel() + " (" + etn.getChildCount() + ")");
				} else if (!(etn.getParent() instanceof EntityTreeNode))
					lab1.setText(e.getLabel() + " (" + etn.getChildCount() + ")");
				if (e instanceof EntityGroup) {
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(new JLabel(FontIcon.of(Material.GROUP_WORK)));
				}
				if (Util.contains(e.getFlags(), Constants.ENTITY_FLAG_GENERIC)) {
					JLabel l = new JLabel();
					if (showText)
						l.setText(Annotator.getString("entity.flag.generic"));
					l.setIcon(FontIcon.of(Material.CLOUD));
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(l);
				}
			} else if (catn != null && catn.getFeatureStructure() instanceof Mention) {
				Mention m = (Mention) catn.getFeatureStructure();
				if (cModel.comments.containsKey(m)) {
					for (Comment comment : cModel.comments.get(m)) {
						JLabel l = new JLabel(FontIcon.of(Material.COMMENT));
						l.setToolTipText(comment.getValue());
						l.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								System.err.println("!!");
								commentAction.actionPerformed(null);
							}
						});
						panel.add(Box.createRigidArea(new Dimension(5, 5)));
						panel.add(l);
					}
				}
				if (Util.isDifficult(m)) {
					JLabel l = new JLabel();
					if (showText)
						l.setText(Annotator.getString("mention.flag.difficult"));
					l.setIcon(FontIcon.of(Material.WARNING));
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(l);
				}
				if (Util.isAmbiguous(m)) {
					JLabel l = new JLabel();
					if (showText)
						l.setText(Annotator.getString("mention.flag.ambiguous"));
					l.setIcon(FontIcon.of(Material.SHARE));
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(l);
				}

				lab1.setIcon(FontIcon.of(Material.PERSON_PIN));
			} else if (cModel != null && catn == cModel.groupRootNode)
				lab1.setIcon(FontIcon.of(Material.GROUP_WORK));
			else if (cModel != null && catn == cModel.rootNode)
				lab1.setIcon(FontIcon.of(Material.PERSON_ADD));
			else if (cModel != null && catn.getFeatureStructure() instanceof DetachedMentionPart)
				lab1.setIcon(FontIcon.of(Material.CHILD_FRIENDLY));

			return panel;
		}

	}

	class DeleteAction extends IkonAction {
		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			super(Material.DELETE);
			putValue(Action.NAME, Annotator.getString("action.delete"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.delete.tooltip"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			registerChange();
			CATreeNode tn = (CATreeNode) tree.getLastSelectedPathComponent();
			if (tn.getFeatureStructure() instanceof Mention) {
				int row = tree.getLeadSelectionRow() - 1;
				cModel.remove((Mention) tn.getFeatureStructure());
				tree.setSelectionRow(row);
			} else if (tn.getFeatureStructure() instanceof EntityGroup) {
				cModel.remove((EntityGroup) tn.getFeatureStructure());
			} else if (tn.getFeatureStructure() instanceof DetachedMentionPart) {
				DetachedMentionPart dmp = (DetachedMentionPart) tn.getFeatureStructure();
				// highlightManager.undraw(dmp);
				cModel.remove(dmp);
			} else if (tn.getFeatureStructure() instanceof Entity) {
				EntityTreeNode etn = (EntityTreeNode) tn;
				FeatureStructure parentFs = ((CATreeNode) etn.getParent()).getFeatureStructure();
				if (parentFs instanceof EntityGroup) {
					cModel.removeFrom((EntityGroup) parentFs, (EntityTreeNode) tn);
				} else if (tn.isLeaf()) {
					cModel.remove(etn.getFeatureStructure());
				}
			}
		}

	}

	class DeleteMentionAction extends IkonAction {
		private static final long serialVersionUID = 1L;

		Mention m;

		public DeleteMentionAction(Mention m) {
			super(Material.DELETE);
			putValue(Action.NAME, Annotator.getString("action.delete"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.delete.tooltip"));
			this.m = m;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			registerChange();
			cModel.remove(m);
		}

	}

	class TextViewKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			JTextComponent ta = (JTextComponent) e.getSource();
			if (cModel.keyMap.containsKey(e.getKeyChar())) {
				e.consume();
				cModel.addTo(cModel.keyMap.get(e.getKeyChar()), ta.getSelectionStart(), ta.getSelectionEnd());
				registerChange();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {

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
			putValue(Action.NAME, Annotator.getString("action.close"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (unsavedChanges) {
				int r = JOptionPane.showConfirmDialog(DocumentWindow.this,
						Annotator.getString("dialog.unsaved_changes.message"),
						Annotator.getString("dialog.unsaved_changes.title"), JOptionPane.OK_CANCEL_OPTION);
				if (r == JOptionPane.OK_OPTION)
					closeWindow(false);
			} else
				closeWindow(false);
		}

	}

	class FormEntityGroup extends IkonAction {
		private static final long serialVersionUID = 1L;

		Ikon ikon = Material.GROUP;

		public FormEntityGroup() {
			super(Material.GROUP);
			putValue(Action.NAME, Annotator.getString("action.group"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.group.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			Entity e1 = (Entity) ((CATreeNode) tree.getSelectionPaths()[0].getLastPathComponent())
					.getFeatureStructure();
			Entity e2 = (Entity) ((CATreeNode) tree.getSelectionPaths()[1].getLastPathComponent())
					.getFeatureStructure();
			cModel.formGroup(e1, e2);
			registerChange();
		}

	}

	class FileExportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		IOPlugin plugin;

		public FileExportAction(IOPlugin plugin) {
			putValue(Action.NAME, plugin.getName());

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser saveDialog = new JFileChooser(file.getParentFile());
			saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			saveDialog.setFileFilter(XmiFileFilter.filter);
			saveDialog.setDialogTitle(Annotator.getString("dialog.export_as.title"));
			int r = saveDialog.showSaveDialog(DocumentWindow.this);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				saveToFile(f, plugin);
				break;
			default:
			}
		}

	}

	class FileSaveAsAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public FileSaveAsAction() {
			super(MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
			putValue(Action.NAME, Annotator.getString("action.save_as"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser saveDialog;
			if (file == null)
				saveDialog = new JFileChooser();
			else
				saveDialog = new JFileChooser(file.getParentFile());
			saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			saveDialog.setFileFilter(XmiFileFilter.filter);
			saveDialog.setDialogTitle(Annotator.getString("dialog.save_as.title"));
			int r = saveDialog.showSaveDialog(DocumentWindow.this);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				if (!f.getName().endsWith(".xmi")) {
					f = new File(f.getAbsolutePath() + ".xmi");
				}
				saveToFile(f, mainApplication.getPluginManager().getDefaultIOPlugin());
				mainApplication.recentFiles.add(0, f);
				mainApplication.refreshRecents();
				break;
			default:
			}

		}
	}

	class ToggleEntitySortOrder extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleEntitySortOrder() {
			super(MaterialDesign.MDI_SORT_VARIANT);
			putValue(Action.NAME, Annotator.getString("action.sort_revert"));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder.descending = !cModel.entitySortOrder.descending;
			cModel.resort();
		}
	}

	class ToggleEntityGeneric extends IkonAction {
		private static final long serialVersionUID = 1L;

		public ToggleEntityGeneric() {
			super(Material.CLOUD);
			putValue(Action.NAME, Annotator.getString("action.flag_entity_generic"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.flag_entity_generic.tooltip"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Entity entity = (Entity) tn.getFeatureStructure();
			cModel.toggleFlagEntity(entity, Constants.ENTITY_FLAG_GENERIC);
			registerChange();

		}
	}

	class ToggleMentionDifficult extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionDifficult() {
			super(Material.WARNING);
			putValue(Action.NAME, Annotator.getString("action.flag_mention_difficult"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.flag_mention_difficult.tooltip"));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			cModel.toggleFlagMention(m, Constants.MENTION_FLAG_DIFFICULT);
			registerChange();

		}

	}

	class ToggleMentionAmbiguous extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionAmbiguous() {
			super(Material.SHARE);
			putValue(Action.NAME, Annotator.getString("action.flag_mention_ambiguous"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			cModel.toggleFlagMention(m, Constants.MENTION_FLAG_AMBIGUOUS);
			registerChange();

		}

	}

	class ToggleShowTextInTreeLabels extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleShowTextInTreeLabels() {
			super(MaterialDesign.MDI_FORMAT_TEXT);
			putValue(Action.NAME, Annotator.getString("action.toggle.show_text_labels"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.toggle.show_text_labels.tooltip"));
			putValue(Action.SELECTED_KEY,
					mainApplication.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean old = mainApplication.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);
			mainApplication.getPreferences().putBoolean(Constants.CFG_SHOW_TEXT_LABELS, !old);
			putValue(Action.SELECTED_KEY, !old);
			tree.repaint();
		}

	}

	class TreeMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				int row = tree.getClosestRowForLocation(e.getX(), e.getY());
				tree.setSelectionRow(row);
				treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

	}

	class TextMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				Annotator.logger.debug("Right-clicked in text at " + e.getPoint());
				int offset = textPane.viewToModel(e.getPoint());
				Collection<Annotation> mentions = cModel.getMentions(offset);
				if (mentions.isEmpty())
					return;
				textPopupMenu.add(Annotator.getString("menu.entities"));
				for (Annotation anno : mentions) {
					StringBuilder b = new StringBuilder();
					b.append(anno.getAddress());
					Mention m = null;
					DetachedMentionPart dmp = null;
					if (anno instanceof Mention) {
						m = (Mention) anno;
						dmp = m.getDiscontinuous();
					} else if (anno instanceof DetachedMentionPart) {
						dmp = (DetachedMentionPart) anno;
						m = dmp.getMention();
					}
					String surf = m.getCoveredText();
					if (dmp != null)
						surf += " [,] " + dmp.getCoveredText();
					if (m.getEntity().getLabel() != null)
						b.append(": ").append(m.getEntity().getLabel());

					JMenu mentionMenu = new JMenu(b.toString());
					mentionMenu.setIcon(FontIcon.of(Material.PERSON, new Color(m.getEntity().getColor())));
					Action a = new ShowMentionInTreeAction(DocumentWindow.this, m);
					mentionMenu.add('"' + surf + '"');
					mentionMenu.add(a);
					mentionMenu.add(new DeleteMentionAction(m));

					textPopupMenu.add(mentionMenu);
				}
				textPopupMenu.show(e.getComponent(), e.getX(), e.getY());

			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	class PopupListener implements PopupMenuListener {
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			((JPopupMenu) e.getSource()).removeAll();

		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {

		}

	}

	class MyTreeSelectionListener implements TreeSelectionListener {

		TreeSelectionEvent currentEvent = null;
		int num;

		// selected things
		TreePath[] paths;
		CATreeNode[] nodes;
		FeatureStructure[] fs;

		private void collectData(TreeSelectionEvent e) {
			currentEvent = e;
			num = tree.getSelectionCount();
			paths = new TreePath[num];
			nodes = new CATreeNode[num];
			fs = new FeatureStructure[num];

			try {
				paths = tree.getSelectionPaths();

				fs = new FeatureStructure[paths.length];
				for (int i = 0; i < paths.length; i++) {
					nodes[i] = (CATreeNode) paths[i].getLastPathComponent();
					fs[i] = nodes[i].getFeatureStructure();
				}
			} catch (NullPointerException ex) {
			}

		}

		private boolean isSingle() {
			return num == 1;
		}

		private boolean isDouble() {
			return num == 2;
		}

		private boolean isEntity() {
			for (FeatureStructure f : fs)
				if (!(f instanceof Entity))
					return false;
			return true;
		}

		private boolean isDetachedMentionPart() {
			for (FeatureStructure f : fs)
				if (!(f instanceof DetachedMentionPart))
					return false;
			return true;
		}

		private boolean isMention() {
			return (fs[0] instanceof Mention);
		}

		private boolean isEntityGroup() {
			return (fs[0] instanceof EntityGroup);
		}

		private boolean isLeaf() {
			for (TreeNode n : nodes)
				if (!n.isLeaf())
					return false;
			return true;
		}

		private Entity getEntity(int i) {
			return (Entity) fs[i];
		}

		private Annotation getAnnotation(int i) {
			return (Annotation) fs[i];
		}

		private Mention getMention(int i) {
			return (Mention) fs[i];
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			collectData(e);
			renameAction.setEnabled(isSingle() && isEntity());
			changeKeyAction.setEnabled(isSingle() && isEntity());
			changeColorAction.setEnabled(isSingle() && isEntity());
			toggleEntityGeneric.setEnabled(isSingle() && isEntity());
			toggleEntityGeneric.putValue(Action.SELECTED_KEY, isSingle() && isEntity() && Util.isGeneric(getEntity(0)));
			deleteAction.setEnabled(isSingle()
					&& (isDetachedMentionPart() || isMention() || isEntityGroup() || (isEntity() && isLeaf())));
			formGroupAction.setEnabled(isDouble() && isEntity());

			toggleMentionDifficult.setEnabled(isSingle() && isMention());
			toggleMentionDifficult.putValue(Action.SELECTED_KEY,
					isSingle() && isMention() && Util.isDifficult(getMention(0)));

			toggleMentionAmbiguous.setEnabled(isSingle() && isMention());
			toggleMentionAmbiguous.putValue(Action.SELECTED_KEY,
					isSingle() && isMention() && Util.isAmbiguous(getMention(0)));

			if (isSingle() && (isMention() || isDetachedMentionPart()))
				annotationSelected(getAnnotation(0));
			else
				annotationSelected(null);
		}

	}

	class InitializeModel extends SwingWorker<CoreferenceModel, Integer> {

		JCas jcas;

		public InitializeModel(JCas jcas) {
			this.jcas = jcas;
		}

		@Override
		protected CoreferenceModel doInBackground() throws Exception {
			CoreferenceModel cModel;
			cModel = new CoreferenceModel(jcas, mainApplication.getPreferences());
			cModel.addCoreferenceModelListener(DocumentWindow.this);

			for (Entity e : JCasUtil.select(jcas, Entity.class)) {
				cModel.add(e);

			}
			publish(60);
			for (EntityGroup eg : JCasUtil.select(jcas, EntityGroup.class))
				for (int i = 0; i < eg.getMembers().size(); i++)
					cModel.insertNodeInto(new EntityTreeNode(eg.getMembers(i)), cModel.get(eg), 0);

			publish(70);
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				cModel.addTo(cModel.get(m.getEntity()), cModel.add(m));
				cModel.registerAnnotation(m);
			}
			highlightManager.clearAndDrawAllAnnotations(jcas);
			textPane.repaint();
			publish(75);
			return cModel;
		}

		@Override
		protected void done() {
			try {
				cModel = get();
				fireModelCreatedEvent();
			} catch (InterruptedException | ExecutionException e) {
				Annotator.logger.catching(e);
			}
		}

		@Override
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				progressBar.setValue(i);
			}
		}

	}

	class CommentAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		Comment comment;

		public CommentAction(Comment c) {
			putValue(Action.NAME, Annotator.getString("action.comment"));
			this.comment = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea msg = new JTextArea();
			msg.setRows(5);
			msg.setColumns(30);
			msg.setLineWrap(true);
			msg.setWrapStyleWord(true);
			if (comment != null)
				msg.setText(comment.getValue());

			JScrollPane scrollPane = new JScrollPane(msg);
			int r = JOptionPane.showConfirmDialog(DocumentWindow.this, scrollPane, "Enter your comment",
					JOptionPane.OK_CANCEL_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				if (comment != null) {
					comment.setValue(msg.getText());
				} else if (textPane.getSelectionEnd() != textPane.getSelectionStart()) {
					Annotation tgt = new Annotation(jcas);
					tgt.setBegin(textPane.getSelectionStart());
					tgt.setEnd(textPane.getSelectionEnd());
					tgt.addToIndexes();
					AnnotationComment com = new AnnotationComment(jcas);
					com.setValue(msg.getText());
					com.setAnnotation(tgt);
					com.addToIndexes();
					highlightManager.draw(tgt, Color.YELLOW, false, true);
				}
				/*
				 * else if (e.getSource() instanceof Component) { } Component
				 * comp = (Component) e.getSource(); TreePath tp =
				 * tree.getClosestPathForLocation(comp.getX(), comp.getY());
				 * System.err.println(tp); } else if (tree.getSelectionCount()
				 * == 1) { CATreeNode node = (CATreeNode)
				 * tree.getSelectionPath().getLastPathComponent(); if
				 * (node.getFeatureStructure() instanceof Mention) {
				 * MentionComment c = new MentionComment(jcas);
				 * c.setValue(msg.getText()); c.setMention((Mention)
				 * node.getFeatureStructure()); c.addToIndexes();
				 * cModel.comments.put(node.getFeatureStructure(), c); } }
				 */
			}

		}

	}

	/**
	 * TODO: This should be indexed to make lookup faster
	 *
	 */
	class EntityFinder implements DocumentListener, KeyListener {

		Pattern pattern;

		public void filter(String s) {
			pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
			if (s.length() >= 1) {
				for (int i = 0; i < cModel.rootNode.getChildCount(); i++) {
					TreeNode tn = cModel.rootNode.getChildAt(i);
					if (tn instanceof EntityTreeNode) {
						EntityTreeNode etn = (EntityTreeNode) tn;
						etn.setVisible(matches(s, etn));
						tree.scrollRowToVisible(0);
					}
				}
				cModel.nodeStructureChanged(cModel.rootNode);
				cModel.resort(EntitySortOrder.getVisibilitySortOrder(cModel.entitySortOrder.getComparator()));
			} else {
				for (int i = 0; i < cModel.rootNode.getChildCount(); i++) {
					TreeNode tn = cModel.rootNode.getChildAt(i);
					if (tn instanceof EntityTreeNode) {
						EntityTreeNode etn = (EntityTreeNode) tn;
						etn.setVisible(true);
					}
				}
				cModel.nodeStructureChanged(cModel.rootNode);
				cModel.resort();
			}
		}

		protected boolean matches(String s, EntityTreeNode e) {
			if (e.getFeatureStructure() == null)
				return false;
			Matcher m;
			if (e.getFeatureStructure().getLabel() != null) {
				m = pattern.matcher(e.getFeatureStructure().getLabel());
				if (m.find())
					return true;
			}
			StringArray flags = e.getFeatureStructure().getFlags();
			if (flags != null)
				for (int i = 0; i < e.getFeatureStructure().getFlags().size(); i++) {
					m = pattern.matcher(e.getFeatureStructure().getFlags(i));
					if (m.find())
						return true;
				}
			for (int i = 0; i < e.getChildCount(); i++) {
				FeatureStructure child = e.getChildAt(i).getFeatureStructure();
				if (child instanceof Annotation) {
					String mc = ((Annotation) child).getCoveredText();
					m = pattern.matcher(mc);
					if (m.find())
						return true;
				}
			}
			return false;

		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			filter(treeSearchField.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			filter(treeSearchField.getText());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			filter(treeSearchField.getText());
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				tree.grabFocus();
				tree.addSelectionRow(1);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

	}

	class TreeKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent ev) {
			if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				int b = textPane.getSelectionStart(), e = textPane.getSelectionEnd();
				if (b != e) {
					for (TreePath tp : tree.getSelectionPaths()) {
						if (tp.getLastPathComponent() instanceof EntityTreeNode) {
							EntityTreeNode etn = (EntityTreeNode) tp.getLastPathComponent();
							cModel.addTo(etn.getFeatureStructure(), b, e);
						}
					}
					treeSearchField.setText("");
					textPane.grabFocus();
				}
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				if (tree.getLeadSelectionRow() == 0)
					treeSearchField.grabFocus();
			}

		}

	}

	public JTree getTree() {
		return tree;
	}

	public CoreferenceModel getCoreferenceModel() {
		return cModel;
	}
}
