package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.action.ChangeColorForEntity;
import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.action.EntityStatisticsAction;
import de.unistuttgart.ims.coref.annotator.action.FileExportAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAsAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FormEntityGroup;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.action.NewEntityAction;
import de.unistuttgart.ims.coref.annotator.action.ProcessAction;
import de.unistuttgart.ims.coref.annotator.action.RemoveDuplicatesAction;
import de.unistuttgart.ims.coref.annotator.action.RemoveForeignAnnotationsAction;
import de.unistuttgart.ims.coref.annotator.action.SetLanguageAction;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.action.ShowMentionInTreeAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.action.ToggleEntityGeneric;
import de.unistuttgart.ims.coref.annotator.action.ToggleEntitySortOrder;
import de.unistuttgart.ims.coref.annotator.action.ToggleEntityVisible;
import de.unistuttgart.ims.coref.annotator.action.ToggleMentionAmbiguous;
import de.unistuttgart.ims.coref.annotator.action.ToggleMentionDifficult;
import de.unistuttgart.ims.coref.annotator.action.ToggleMentionNonNominal;
import de.unistuttgart.ims.coref.annotator.action.UndoAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontFamilySelectAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeDecreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeIncreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewShowCommentsAction;
import de.unistuttgart.ims.coref.annotator.action.ViewStyleSelectAction;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.api.Meta;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.DocumentState;
import de.unistuttgart.ims.coref.annotator.document.DocumentStateListener;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.Op;
import de.unistuttgart.ims.coref.annotator.plugin.rankings.MatchingRanker;
import de.unistuttgart.ims.coref.annotator.plugin.rankings.PreceedingRanker;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.EntityRankingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.Plugin;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.worker.DocumentModelLoader;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class DocumentWindow extends AbstractWindow implements CaretListener, TreeModelListener,
		CoreferenceModelListener, HasTextView, DocumentStateListener, HasTreeView {

	private static final long serialVersionUID = 1L;

	@Deprecated
	JCas jcas;
	File file;
	@Deprecated
	Annotator mainApplication;

	String segmentAnnotation = null;

	// storing and caching
	Feature titleFeature;
	int mouseClickedPosition = -1;

	// actions
	ActionContainer actions = new ActionContainer();

	// controller
	DocumentModel documentModel;
	HighlightManager highlightManager;

	// Window components
	JTree tree;
	JTextPane textPane;
	StyleContext styleContext = new StyleContext();
	JLabel selectionDetailPanel;
	JSplitPane splitPane;
	JTextField treeSearchField;
	TreeKeyListener treeKeyListener = new TreeKeyListener();
	MutableSet<DocumentStateListener> documentStateListeners = Sets.mutable.empty();

	// Sub windows
	CommentWindow commentsWindow;

	// Menu components
	JMenu documentMenu;
	JMenu recentMenu;
	JMenu windowsMenu;
	JPopupMenu treePopupMenu;
	JPopupMenu textPopupMenu;
	Map<StylePlugin, JRadioButtonMenuItem> styleMenuItem = new HashMap<StylePlugin, JRadioButtonMenuItem>();

	// Settings
	StylePlugin currentStyle;

	// sub windows
	SearchDialog searchPanel;

	public DocumentWindow(Annotator annotator) {
		super();
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
		super.initializeWindow();

		// popup
		treePopupMenu = new JPopupMenu();
		// treePopupMenu.add(this.commentAction);
		treePopupMenu.add(this.actions.deleteAction);
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		treePopupMenu.add(new JCheckBoxMenuItem(this.actions.toggleMentionAmbiguous));
		treePopupMenu.add(new JCheckBoxMenuItem(this.actions.toggleMentionDifficult));
		treePopupMenu.add(new JCheckBoxMenuItem(this.actions.toggleMentionNonNominal));
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		treePopupMenu.add(this.actions.newEntityAction);
		treePopupMenu.add(this.actions.renameAction);
		treePopupMenu.add(this.actions.changeColorAction);
		treePopupMenu.add(this.actions.changeKeyAction);
		treePopupMenu.add(this.actions.mergeSelectedEntitiesAction);
		treePopupMenu.add(this.actions.formGroupAction);
		treePopupMenu.add(this.actions.removeDuplicatesAction);
		treePopupMenu.add(new JCheckBoxMenuItem(this.actions.toggleEntityGeneric));
		treePopupMenu.add(new JCheckBoxMenuItem(this.actions.toggleEntityDisplayed));
		treePopupMenu.add(this.actions.entityStatisticsAction);

		textPopupMenu = new JPopupMenu();
		textPopupMenu.addPopupMenuListener(new PopupListener());

		// initialise panel
		JPanel rightPanel = new JPanel(new BorderLayout());
		tree = new JTree();
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setLargeModel(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setTransferHandler(new MyTreeTransferHandler());
		tree.setCellRenderer(new MyTreeCellRenderer());
		tree.addTreeSelectionListener(new MyTreeSelectionListener(tree));
		tree.addMouseListener(new TreeMouseListener());
		tree.addKeyListener(treeKeyListener);

		treeSearchField = new JTextField();
		EntityFinder entityFinder = new EntityFinder();
		treeSearchField.getDocument().addDocumentListener(entityFinder);
		treeSearchField.addKeyListener(entityFinder);
		rightPanel.setPreferredSize(new Dimension(300, 800));
		rightPanel.add(treeSearchField, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// tool bar
		JToolBar controls = new JToolBar();
		controls.setFocusable(false);
		controls.setRollover(true);
		controls.add(actions.newEntityAction);
		controls.add(actions.renameAction);
		controls.add(actions.changeKeyAction);
		controls.add(actions.changeColorAction);
		controls.add((actions.deleteAction));
		controls.add(actions.formGroupAction);
		controls.add(actions.mergeSelectedEntitiesAction);
		controls.add(actions.showSearchPanelAction);
		getContentPane().add(controls, BorderLayout.NORTH);

		for (Component comp : controls.getComponents())
			comp.setFocusable(false);

		getMiscLabel().setText("Style: " + Annotator.app.getPluginManager().getDefaultStylePlugin().getName());
		getMiscLabel().setToolTipText(Annotator.app.getPluginManager().getDefaultStylePlugin().getDescription());
		getMiscLabel().setPreferredSize(new Dimension(150, 20));

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
		textPane.addKeyListener(new TextViewKeyListener());
		textPane.setCaretPosition(0);
		textPane.addCaretListener(this);
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				actions.copyAction);

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
		setLocationRelativeTo(null);
		Annotator.logger.info("Window initialised.");
	}

	protected void initialiseActions() {
		this.actions.renameAction = new RenameEntityAction();
		this.actions.newEntityAction = new NewEntityAction(this);
		this.actions.changeColorAction = new ChangeColorForEntity(this);
		this.actions.changeKeyAction = new ChangeKeyForEntityAction();
		this.actions.deleteAction = new DeleteAction();
		this.actions.toggleMentionDifficult = new ToggleMentionDifficult(this);
		this.actions.toggleMentionAmbiguous = new ToggleMentionAmbiguous(this);
		this.actions.toggleEntityGeneric = new ToggleEntityGeneric(this);
		this.actions.sortByAlpha = new SortTreeByAlpha();
		this.actions.sortByMentions = new SortTreeByMentions();
		this.actions.fileSaveAction = new FileSaveAction(this);
		this.actions.showSearchPanelAction = new ShowSearchPanelAction(Annotator.app, this);
		this.actions.copyAction = new CopyAction(this);
		this.actions.undoAction = new UndoAction(this);
		this.actions.removeDuplicatesAction = new RemoveDuplicatesAction(this);
		this.actions.entityStatisticsAction = new EntityStatisticsAction(this);

		// disable some at the beginning
		actions.newEntityAction.setEnabled(false);
		actions.renameAction.setEnabled(false);
		actions.changeKeyAction.setEnabled(false);
		actions.changeColorAction.setEnabled(false);
		actions.deleteAction.setEnabled(false);
		actions.formGroupAction.setEnabled(false);
		actions.mergeSelectedEntitiesAction.setEnabled(false);
		actions.toggleMentionDifficult.setEnabled(false);
		actions.toggleMentionAmbiguous.setEnabled(false);
		actions.toggleEntityGeneric.setEnabled(false);
		actions.toggleEntityDisplayed.setEnabled(false);
		actions.undoAction.setEnabled(false);
		actions.entityStatisticsAction.setEnabled(false);

		//
		documentStateListeners.add(actions.undoAction);
		documentStateListeners.add(actions.fileSaveAction);

		Annotator.logger.trace("Actions initialised.");

	}

	protected JMenu initialiseMenuView() {
		JMenu viewMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW));
		viewMenu.add(new ViewFontSizeDecreaseAction(this));
		viewMenu.add(new ViewFontSizeIncreaseAction(this));

		JMenu fontFamilyMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_FONTFAMILY));
		String[] fontFamilies = new String[] { Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED };
		ButtonGroup grp = new ButtonGroup();
		for (String s : fontFamilies) {
			AbstractAction a = new ViewFontFamilySelectAction(this, s);
			JRadioButtonMenuItem radio = new JRadioButtonMenuItem(a);
			fontFamilyMenu.add(radio);
			grp.add(radio);
		}
		// TODO: Disabled for the moment
		// viewMenu.add(fontFamilyMenu);

		viewMenu.addSeparator();

		PluginManager pm = Annotator.app.getPluginManager();

		JMenu viewStyleMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_STYLE));
		grp = new ButtonGroup();
		StylePlugin pl = pm.getDefaultStylePlugin();
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(
				new ViewStyleSelectAction(this, pm.getDefaultStylePlugin()));
		radio1.setSelected(true);
		viewStyleMenu.add(radio1);
		styleMenuItem.put(pl, radio1);
		grp.add(radio1);
		for (Class<? extends StylePlugin> plugin : pm.getStylePlugins()) {
			pl = pm.getStylePlugin(plugin);
			radio1 = new JRadioButtonMenuItem(new ViewStyleSelectAction(this, pl));
			viewStyleMenu.add(radio1);
			styleMenuItem.put(pl, radio1);
			grp.add(radio1);

		}
		viewMenu.add(viewStyleMenu);
		viewMenu.add(new ViewShowCommentsAction(this));
		return viewMenu;

	}

	protected JMenu initialiseMenuTools() {

		JMenu procMenu = new JMenu(Annotator.getString(Strings.MENU_TOOLS_PROC));
		for (Class<? extends ProcessingPlugin> pp : Annotator.app.getPluginManager().getProcessingPlugins()) {
			ProcessAction pa = new ProcessAction(this, Annotator.app.getPluginManager().getPlugin(pp));
			procMenu.add(pa);
			documentStateListeners.add(pa);

		}

		JMenu toolsMenu = new JMenu(Annotator.getString(Strings.MENU_TOOLS));
		toolsMenu.add(procMenu);
		toolsMenu.add(actions.showSearchPanelAction);
		toolsMenu.add(actions.setDocumentLanguageAction);
		toolsMenu.add(actions.clearAction);
		toolsMenu.add(new RemoveForeignAnnotationsAction(this));
		toolsMenu.addSeparator();
		// toolsMenu.add(new ShowHistoryAction(this));
		toolsMenu.add(new ShowLogWindowAction(Annotator.app));
		return toolsMenu;
	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_IMPORT_FROM));
		JMenu fileExportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_EXPORT_AS));

		PluginManager pm = Annotator.app.getPluginManager();
		for (Class<? extends IOPlugin> pluginClass : pm.getIOPlugins()) {
			try {
				IOPlugin plugin = pm.getIOPlugin(pluginClass);
				if (plugin.getImporter() != null)
					fileImportMenu.add(new FileImportAction(Annotator.app, plugin));
				if (plugin.getExporter() != null)
					fileExportMenu.add(new FileExportAction(this, this, plugin));
			} catch (ResourceInitializationException e) {
				Annotator.logger.catching(e);
			}

		}

		JMenu fileMenu = new JMenu(Annotator.getString(Strings.MENU_FILE));
		fileMenu.add(new FileSelectOpenAction(Annotator.app));
		fileMenu.add(Annotator.app.getRecentFilesMenu());
		fileMenu.add(fileImportMenu);
		fileMenu.add(actions.fileSaveAction);
		fileMenu.add(new FileSaveAsAction(this));
		fileMenu.add(fileExportMenu);
		fileMenu.add(actions.closeAction);
		fileMenu.add(Annotator.app.quitAction);

		return fileMenu;
	}

	protected JMenu initialiseMenuEntity() {
		JMenu entityMenu = new JMenu(Annotator.getString(Strings.MENU_EDIT));
		entityMenu.add(new JMenuItem(actions.undoAction));
		entityMenu.add(new JMenuItem(actions.copyAction));
		entityMenu.add(new JMenuItem(actions.deleteAction));
		entityMenu.add(new JMenuItem(actions.commentAction));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionAmbiguous));
		entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionDifficult));
		entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionNonNominal));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		entityMenu.add(new JMenuItem(actions.newEntityAction));
		entityMenu.add(new JMenuItem(actions.renameAction));
		entityMenu.add(new JMenuItem(actions.changeColorAction));
		entityMenu.add(new JMenuItem(actions.changeKeyAction));
		entityMenu.add(new JMenuItem(actions.formGroupAction));
		entityMenu.add(new JCheckBoxMenuItem(actions.toggleEntityGeneric));
		entityMenu.add(new JCheckBoxMenuItem(actions.toggleEntityDisplayed));
		entityMenu.add(actions.entityStatisticsAction);

		JMenu sortMenu = new JMenu(Annotator.getString(Strings.MENU_EDIT_ENTITIES_SORT));
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(this.actions.sortByAlpha);
		JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(this.actions.sortByMentions);
		radio2.setSelected(true);
		ButtonGroup grp = new ButtonGroup();
		grp.add(radio2);
		grp.add(radio1);

		sortMenu.add(radio1);
		sortMenu.add(radio2);
		sortMenu.add(new JCheckBoxMenuItem(this.actions.sortDescending));

		entityMenu.add(sortMenu);
		return entityMenu;
	}

	protected void initialiseMenu() {

		JMenu helpMenu = new JMenu(Annotator.getString(Strings.MENU_HELP));
		helpMenu.add(Annotator.app.helpAction);

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
				actions.closeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		});

		Annotator.logger.info("Initialised menus");
	}

	protected void closeWindow(boolean quit) {
		if (documentModel.isSavable()) {
			Annotator.logger.warn("Closing window with unsaved changes");
		}
		if (searchPanel != null) {
			searchPanel.setVisible(false);
			searchPanel.dispose();
			searchPanel = null;
		}
		Annotator.app.close(this);
	}

	public void loadFile(File file, IOPlugin flavor, String language) {
		if (flavor instanceof DefaultIOPlugin)
			this.file = file;
		else
			this.actions.fileSaveAction.setEnabled(false);

		JCasLoader lai;
		try {
			setMessage(Annotator.getString(Strings.MESSAGE_LOADING));
			setIndeterminateProgress();
			lai = new JCasLoader(jcas -> this.setJCas(jcas), file,
					TypeSystemDescriptionFactory.createTypeSystemDescription(), flavor, language);
			lai.execute();
		} catch (ResourceInitializationException e) {
			Annotator.logger.catching(e);
		}

	}

	@Deprecated
	public synchronized void saveCurrentFile() {
		if (file != null)
			saveToFile(file, Annotator.app.getPluginManager().getDefaultIOPlugin(), false);
	}

	@Deprecated
	public synchronized void saveToFile(File f, IOPlugin plugin, boolean ask) {
		Annotator.logger.info("Exporting into file {} using plugin {}", f, plugin.getName());
		setMessage(Annotator.getString(Strings.MESSAGE_SAVING));

		if (f.exists() && ask) {
			int answer = JOptionPane.showConfirmDialog(this,
					Annotator.getString(Constants.Strings.DIALOG_FILE_EXISTS_OVERWRITE));
			if (answer != JOptionPane.YES_OPTION) {
				setMessage("");
				return;
			}
		}
		getProgressBar().setVisible(true);
		getProgressBar().setIndeterminate(true);

		new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				SimplePipeline.runPipeline(jcas, plugin.getExporter(), plugin.getWriter(f));
				return new Object();
			}

			@Override
			protected void done() {
				progressBar.setVisible(false);
				setMessage("");
				if (plugin == Annotator.app.getPluginManager().getDefaultIOPlugin()) {
					file = f;
					setWindowTitle();
				}
			}

		}.execute();
	}

	@Override
	@Deprecated
	public JCas getJCas() {
		return jcas;
	}

	@Deprecated
	public Annotator getMainApplication() {
		return Annotator.app;
	}

	class SortTreeByAlpha extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByAlpha() {
			super(MaterialDesign.MDI_SORT_ALPHABETICAL);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_ALPHA));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentModel.getTreeModel().setEntitySortOrder(EntitySortOrder.Alphabet);
			documentModel.getTreeModel().getEntitySortOrder().descending = false;
			documentModel.getTreeModel().resort();
			actions.sortDescending.putValue(Action.SELECTED_KEY, false);
		}

	}

	class SortTreeByMentions extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByMentions() {
			super(MaterialDesign.MDI_SORT_NUMERIC);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_MENTIONS));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_MENTIONS_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentModel.getTreeModel().setEntitySortOrder(EntitySortOrder.Mentions);
			documentModel.getTreeModel().getEntitySortOrder().descending = true;
			documentModel.getTreeModel().resort();
			actions.sortDescending.putValue(Action.SELECTED_KEY, true);
		}

	}

	public void setWindowTitle() {
		String fileName = (file != null ? file.getName() : Annotator.getString(Strings.WINDOWTITLE_NEW_FILE));
		String documentTitle = null;
		try {
			if (titleFeature != null)
				documentTitle = jcas.getDocumentAnnotationFs().getFeatureValueAsString(titleFeature);
		} catch (Exception e) {
			Annotator.logger.catching(e);
		}
		if (documentTitle == null)
			documentTitle = "Untitled document";
		setTitle(documentTitle + " (" + fileName + ")"
				+ (documentModel.isSavable() ? " -- " + Annotator.getString(Strings.WINDOWTITLE_EDITED) : ""));
	}

	public void showSearch() {
		if (searchPanel == null) {
			searchPanel = new SearchDialog(this, Annotator.app.getPreferences());
		}
		searchPanel.setVisible(true);
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		Iterator<FeatureStructure> iter = event.iterator(1);
		switch (eventType) {
		case Add:
			while (iter.hasNext()) {
				FeatureStructure fs = iter.next();
				if (fs instanceof Mention || fs instanceof DetachedMentionPart) {
					highlightManager.underline((Annotation) fs);
				} else if (fs instanceof CommentAnchor) {
					highlightManager.highlight((Annotation) fs);
				}
			}
			break;
		case Remove:
			while (iter.hasNext()) {
				FeatureStructure fs = iter.next();
				if (fs instanceof Mention) {
					if (((Mention) fs).getDiscontinuous() != null)
						highlightManager.undraw(((Mention) fs).getDiscontinuous());
					highlightManager.undraw((Annotation) fs);
				} else if (fs instanceof Annotation)
					highlightManager.undraw((Annotation) fs);

			}
			break;
		case Move:
			for (FeatureStructure fs : event)
				if (fs instanceof Mention) {
					highlightManager.undraw((Annotation) fs);
					highlightManager.underline((Mention) fs, new Color(((Entity) event.getArgument2()).getColor()));
				}
			break;
		case Update:
			for (FeatureStructure fs : event) {
				if (fs instanceof Mention) {
					if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
						highlightManager.undraw((Annotation) fs);
					else
						highlightManager.underline((Annotation) fs);
				}
			}
			break;
		default:
			break;
		}

	}

	public void setDocumentModel(DocumentModel model) {

		tree.setModel(model.getTreeModel());
		model.getTreeModel().addTreeModelListener(this);
		model.addDocumentStateListener(this);
		documentModel = model;

		// UI
		documentStateListeners.forEach(dsl -> documentModel.addDocumentStateListener(dsl));
		stopIndeterminateProgress();
		Annotator.logger.debug("Setting loading progress to {}", 100);
		splitPane.setVisible(true);

		// Style
		Meta meta = Util.getMeta(jcas);
		StylePlugin sPlugin = null;

		if (meta.getStylePlugin() != null) {
			Object o = null;
			try {
				Class<?> pureClass = Class.forName(meta.getStylePlugin());
				if (pureClass.isAssignableFrom(Plugin.class)) {
					@SuppressWarnings("unchecked")
					Class<? extends Plugin> pluginClass = (Class<? extends Plugin>) pureClass;
					o = Annotator.app.getPluginManager().getPlugin(pluginClass);
				}
				if (o != null && o instanceof StylePlugin)
					sPlugin = (StylePlugin) o;
			} catch (ClassNotFoundException e) {
				Annotator.logger.catching(e);
			}
		}
		if (sPlugin == null)
			sPlugin = Annotator.app.getPluginManager().getDefaultStylePlugin();

		StyleManager.styleParagraph(textPane.getStyledDocument(), StyleManager.getDefaultParagraphStyle());
		switchStyle(sPlugin);

		// final
		setMessage("");
		setWindowTitle();

		commentsWindow = new CommentWindow(this, documentModel.getCommentsModel());
		documentModel.signal();
		Annotator.logger.info("Document model has been loaded.");
	}

	public void setJCas(JCas jcas) {

		this.jcas = jcas;
		Annotator.logger.info("JCas has been loaded.");
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

		titleFeature = jcas.getTypeSystem().getFeatureByFullName(
				Annotator.app.getPreferences().get(Constants.CFG_WINDOWTITLE, Defaults.CFG_WINDOWTITLE));

		// highlightManager.clearAndDrawAllAnnotations(jcas);

		DocumentModelLoader im = new DocumentModelLoader(cm -> this.setDocumentModel(cm), jcas);
		im.setCoreferenceModelListener(this);
		im.execute();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		actions.newEntityAction.setEnabled(
				!(textPane.getSelectedText() == null || textPane.getSelectionStart() == textPane.getSelectionEnd()));
	}

	public void updateStyle(Object constant, Object value) {
		MutableAttributeSet baseStyle = currentStyle.getBaseStyle();
		baseStyle.addAttribute(constant, value);
		switchStyle(currentStyle);
	}

	public void switchStyle(StylePlugin sv) {
		switchStyle(sv, sv.getBaseStyle());
	}

	public void switchStyle(StylePlugin sv, AttributeSet baseStyle) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				getProgressBar().setValue(0);
				getProgressBar().setVisible(true);
				Annotator.logger.debug("Activating style {}", sv.getClass().getName());

				getProgressBar().setValue(20);

				Map<AttributeSet, org.apache.uima.cas.Type> styles = sv.getSpanStyles(jcas.getTypeSystem(),
						styleContext, baseStyle);
				StyleManager.styleCharacter(textPane.getStyledDocument(), baseStyle);
				if (styles != null)
					for (AttributeSet style : styles.keySet()) {
						StyleManager.style(jcas, textPane.getStyledDocument(), style, styles.get(style));
						getProgressBar().setValue(getProgressBar().getValue() + 10);
					}
				Util.getMeta(jcas).setStylePlugin(sv.getClass().getName());
				currentStyle = sv;
				styleMenuItem.get(sv).setSelected(true);
				getMiscLabel().setText(Annotator.getString(Strings.STATUS_STYLE) + ": " + sv.getName());
				getMiscLabel().setToolTipText(sv.getDescription());
				getMiscLabel().repaint();
				progressBar.setValue(100);
				progressBar.setVisible(false);
			}

		});

	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		tree.expandPath(e.getTreePath().getParentPath());

		// if (e.getTreePath().getLastPathComponent() instanceof EntityGroup)
		// tree.expandPath(e.getTreePath());
		/*
		 * try { TreePath tp = e.getTreePath(); Rectangle r = tree.getPathBounds(tp);
		 * tree.repaint(r); } catch (NullPointerException ex) {
		 * Annotator.logger.catching(ex); }
		 */
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

	class MyTreeTransferHandler extends TransferHandler {

		CATreeNode targetNode;
		FeatureStructure targetFS;

		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
			if (dl.getPath() == null) {
				setMessage("");
				return false;
			}
			TreePath treePath = dl.getPath();
			targetNode = (CATreeNode) treePath.getLastPathComponent();
			targetFS = targetNode.getFeatureStructure();

			// new mention created in text view
			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
				if (targetFS instanceof Mention)
					setMessage(Annotator.getString(Strings.MESSAGE_CREATES_MENTION_PART));
				else if (targetFS instanceof Entity)
					setMessage(Annotator.getString(Strings.MESSAGE_CREATES_MENTION));
				else if (targetFS == null)
					setMessage(Annotator.getString(Strings.MESSAGE_CREATES_ENTITY));
				return true;
			}
			// move existing node
			if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
				if (targetFS instanceof TOP)
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
					&& !info.isDataFlavorSupported(NodeListTransferable.dataFlavor)) {
				return false;
			}

			JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
			TreePath tp = dl.getPath();
			DataFlavor dataFlavor = info.getTransferable().getTransferDataFlavors()[0];

			targetNode = ((CATreeNode) tp.getLastPathComponent());
			targetFS = targetNode.getFeatureStructure();

			if (dataFlavor == PotentialAnnotationTransfer.dataFlavor) {
				try {
					@SuppressWarnings("unchecked")
					ImmutableList<Span> paList = (ImmutableList<Span>) info.getTransferable()
							.getTransferData(PotentialAnnotationTransfer.dataFlavor);
					Op op = null;
					if (targetFS == null) {
						op = new Op.AddMentionsToNewEntity(paList);
					} else if (targetFS instanceof Entity) {
						op = new Op.AddMentionsToEntity((Entity) targetFS, paList);
					} else if (targetFS instanceof Mention) {
						op = new Op.AttachPart((Mention) targetFS, paList.getFirst());
					}
					if (op != null) {
						documentModel.getCoreferenceModel().edit(op);
					}

				} catch (UnsupportedFlavorException | IOException e) {
					Annotator.logger.catching(e);
				}
			} else if (dataFlavor == NodeListTransferable.dataFlavor) {
				try {
					@SuppressWarnings("unchecked")
					ImmutableList<CATreeNode> object = (ImmutableList<CATreeNode>) info.getTransferable()
							.getTransferData(NodeListTransferable.dataFlavor);
					handleNodeMoving(object);
				} catch (UnsupportedFlavorException | IOException e) {
					Annotator.logger.catching(e);
				}
			}

			return true;
		}

		protected boolean handleNodeMoving(ImmutableList<CATreeNode> moved) {
			Annotator.logger.debug("Moving {} things", moved.size());
			Op operation = null;
			if (targetFS instanceof Entity) {
				if (targetFS instanceof EntityGroup) {
					operation = new Op.AddEntityToEntityGroup((EntityGroup) targetFS,
							moved.collect(n -> n.getFeatureStructure()));
				} else
					documentModel.getCoreferenceModel().edit(new Op.MoveMentionsToEntity((Entity) targetFS,
							moved.collect(n -> (n.getFeatureStructure()))));
			} else if (targetFS instanceof Mention)
				operation = new Op.MoveMentionPartToMention((Mention) targetFS, moved.getFirst().getFeatureStructure());
			else
				return false;
			if (operation != null)
				documentModel.getCoreferenceModel().edit(operation);
			return true;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return MOVE | COPY;
		}

		@Override
		public Transferable createTransferable(JComponent comp) {
			JTree tree = (JTree) comp;
			ImmutableList<TreePath> paths = Lists.immutable.of(tree.getSelectionPaths());

			ImmutableList<CATreeNode> nodes = paths.collect(tp -> (CATreeNode) tp.getLastPathComponent())
					.select(n -> n.isEntity() || n.isMention() || n.isMentionPart());
			if (nodes.isEmpty())
				return null;
			return new NodeListTransferable(nodes);
		}

	}

	class RenameEntityAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public RenameEntityAction() {
			super(MaterialDesign.MDI_RENAME_BOX);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_RENAME));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_RENAME_TOOLTIP));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			treeKeyListener.setIgnoreNext(true);
			CATreeNode etn = (CATreeNode) tree.getLastSelectedPathComponent();
			String l = etn.getEntity().getLabel();
			String newLabel = (String) JOptionPane.showInputDialog(textPane,
					Annotator.getString(Strings.DIALOG_RENAME_ENTITY_PROMPT), "", JOptionPane.PLAIN_MESSAGE,
					FontIcon.of(MaterialDesign.MDI_KEYBOARD), null, l);
			if (newLabel != null) {
				Op.RenameEntity op = new Op.RenameEntity(etn.getEntity(), newLabel);
				documentModel.getCoreferenceModel().edit(op);

			}
		}

	}

	class ChangeKeyForEntityAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ChangeKeyForEntityAction() {
			super(Strings.ACTION_SET_SHORTCUT, MaterialDesign.MDI_KEYBOARD);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SET_SHORTCUT_TOOLTIP));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			CATreeNode etn = (CATreeNode) tree.getLastSelectedPathComponent();
			String s = "";
			if (etn.getEntity().getKey() != null)
				s = etn.getEntity().getKey();
			String newKey = (String) JOptionPane.showInputDialog(DocumentWindow.this,
					Annotator.getString(Strings.DIALOG_CHANGE_KEY_PROMPT), "", JOptionPane.PLAIN_MESSAGE,
					FontIcon.of(MaterialDesign.MDI_KEYBOARD), null, s);
			if (newKey != null)
				if (newKey.length() == 1) {
					Character newChar = newKey.charAt(0);
					documentModel.getCoreferenceModel().edit(new Op.UpdateEntityKey(newChar, etn.getEntity()));
				} else {
					JOptionPane.showMessageDialog(DocumentWindow.this,
							Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_MESSAGE),
							Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_TITLE),
							JOptionPane.INFORMATION_MESSAGE);
				}
		}

	}

	class MyTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

		private static final long serialVersionUID = 1L;
		boolean showText = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);

		CATreeNode treeNode;

		protected void addFlag(JPanel panel, String textLabel, Icon icon) {
			JLabel l = new JLabel();
			if (showText)
				l.setText(textLabel);
			l.setIcon(icon);
			panel.add(Box.createRigidArea(new Dimension(5, 5)));
			panel.add(l);
		}

		protected JPanel handleEntity(JPanel panel, JLabel lab1, Entity entity) {
			lab1.setText(entity.getLabel());
			if (Util.isX(entity, Constants.ENTITY_FLAG_HIDDEN) || treeNode.getRank() < 50) {
				lab1.setForeground(Color.GRAY);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_OUTLINE, Color.GRAY));
			} else {
				lab1.setForeground(Color.BLACK);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(entity.getColor())));
			}
			if (entity.getKey() != null) {
				lab1.setText(entity.getKey() + ": " + entity.getLabel() + " (" + treeNode.getChildCount() + ")");
			} else if (!(treeNode.getParent().isEntity()))
				lab1.setText(entity.getLabel() + " (" + treeNode.getChildCount() + ")");
			if (entity instanceof EntityGroup) {
				panel.add(Box.createRigidArea(new Dimension(5, 5)));
				panel.add(new JLabel(FontIcon.of(MaterialDesign.MDI_ACCOUNT_MULTIPLE)));
			}
			if (Util.contains(entity.getFlags(), Constants.ENTITY_FLAG_GENERIC)) {
				addFlag(panel, Annotator.getString(Strings.ENTITY_FLAG_GENERIC), FontIcon.of(MaterialDesign.MDI_CLOUD));
			}
			return panel;
		}

		protected JPanel handleMention(JPanel panel, JLabel lab1, Mention m) {
			lab1.setText(m.getCoveredText());
			if (Util.isNonNominal(m))
				addFlag(panel, Annotator.getString(Strings.MENTION_FLAG_NON_NOMINAL),
						FontIcon.of(MaterialDesign.MDI_FLAG));
			if (Util.isDifficult(m)) {
				addFlag(panel, Annotator.getString(Strings.MENTION_FLAG_DIFFICULT),
						FontIcon.of(MaterialDesign.MDI_ALERT_BOX));
			}
			if (Util.isAmbiguous(m)) {
				addFlag(panel, Annotator.getString(Strings.MENTION_FLAG_AMBIGUOUS),
						FontIcon.of(MaterialDesign.MDI_SHARE_VARIANT));
			}
			lab1.setIcon(FontIcon.of(MaterialDesign.MDI_COMMENT_ACCOUNT));
			return panel;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			// we only handle instances of CATreeNode
			if (!(value instanceof CATreeNode))
				return new JLabel("tree node");

			// get the current tree node
			treeNode = (CATreeNode) value;

			// this is the panel representing the node
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setOpaque(false);

			// this is the main label for the node
			JLabel mainLabel = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);
			panel.add(mainLabel);

			// depending of node type, do different things
			if (treeNode.isEntity())
				return handleEntity(panel, mainLabel, treeNode.getEntity());
			else if (treeNode.isMention()) {
				return this.handleMention(panel, mainLabel, treeNode.getFeatureStructure());
			} else if (documentModel != null && documentModel.getCoreferenceModel() != null
					&& treeNode == tree.getModel().getRoot())
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_PLUS));
			else if (documentModel != null && documentModel.getCoreferenceModel() != null
					&& treeNode.getFeatureStructure() instanceof DetachedMentionPart)
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_TREE));

			return panel;
		}

	}

	class DeleteAction extends IkonAction {
		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			super(Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_TOOLTIP));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			MutableList<TreePath> selection = Lists.mutable.of(tree.getSelectionPaths());
			CATreeNode node = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			FeatureStructure fs = ((CATreeNode) tree.getSelectionPath().getLastPathComponent()).getFeatureStructure();
			Op op = null;
			if (fs instanceof Entity) {
				FeatureStructure parentFs = node.getParent().getFeatureStructure();
				if (parentFs instanceof EntityGroup) {
					op = new Op.RemoveEntitiesFromEntityGroup((EntityGroup) parentFs, node.getEntity());
				} else if (node.isLeaf()) {
					op = new Op.RemoveEntities(selection.collect(tp -> (CATreeNode) tp.getLastPathComponent())
							.collect(tn -> (Entity) tn.getFeatureStructure()));
				}
			} else if (fs instanceof Mention) {
				op = new Op.RemoveMention(selection.collect(tp -> (CATreeNode) tp.getLastPathComponent())
						.collect(tn -> (Mention) tn.getFeatureStructure()));
			} else if (fs instanceof DetachedMentionPart) {
				op = new Op.RemovePart(((DetachedMentionPart) fs).getMention(), (DetachedMentionPart) fs);
			}

			if (op != null)
				getCoreferenceModel().edit(op);
			else
				for (TreePath tp : tree.getSelectionPaths())
					deleteSingle((CATreeNode) tp.getLastPathComponent());
		}

		private void deleteSingle(CATreeNode tn) {
			Op operation = null;
			if (tn.getFeatureStructure() instanceof Mention) {
				int row = tree.getLeadSelectionRow() - 1;
				documentModel.getCoreferenceModel().edit(new Op.RemoveMention(tn.getFeatureStructure()));
				tree.setSelectionRow(row);
			} else if (tn.getFeatureStructure() instanceof EntityGroup) {
				documentModel.getCoreferenceModel().edit(new Op.RemoveEntities(tn.getFeatureStructure()));
			} else if (tn.getFeatureStructure() instanceof DetachedMentionPart) {
				DetachedMentionPart dmp = (DetachedMentionPart) tn.getFeatureStructure();
				documentModel.getCoreferenceModel().edit(new Op.RemovePart(dmp.getMention(), dmp));
			} else if (tn.isEntity()) {
				FeatureStructure parentFs = tn.getParent().getFeatureStructure();
				if (parentFs instanceof EntityGroup) {
					operation = new Op.RemoveEntitiesFromEntityGroup((EntityGroup) parentFs, tn.getEntity());
				} else if (tn.isLeaf()) {
					documentModel.getCoreferenceModel().edit(new Op.RemoveEntities(tn.getEntity()));
				}
			}
			if (operation != null)
				documentModel.getCoreferenceModel().edit(operation);
		}

	}

	class DeleteMentionAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		Mention m;

		public DeleteMentionAction(Mention m) {
			super(Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_TOOLTIP));
			this.m = m;

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			documentModel.getCoreferenceModel().edit(new Op.RemoveMention(m));
		}

	}

	class TextViewKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			if (documentModel.getCoreferenceModel().getKeyMap().containsKey(e.getKeyChar())) {
				e.consume();
				documentModel.getCoreferenceModel().edit(new Op.AddMentionsToEntity(
						documentModel.getCoreferenceModel().getKeyMap().get(e.getKeyChar()), getSelection()));
			} else if (e.getKeyChar() == ' ') {
				if (textPane.getSelectionStart() != textPane.getSelectionEnd()) {
					Rectangle p;
					try {
						p = textPane.modelToView(textPane.getSelectionStart());
						textPopupMenu.show(e.getComponent(), p.x, p.y);
					} catch (BadLocationException e1) {
						Annotator.logger.catching(e1);
					}
				}
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
		public boolean canImport(TransferHandler.TransferSupport info) {

			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
				JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
				Collection<Annotation> mentions = documentModel.getCoreferenceModel().getMentions(dl.getIndex());
				if (mentions.size() > 0)
					return true;
			}
			return false;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}
			JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
			Collection<Annotation> mentions = documentModel.getCoreferenceModel().getMentions(dl.getIndex());
			for (Annotation a : mentions) {
				if (a instanceof Mention) {
					try {
						Transferable pat = info.getTransferable();
						@SuppressWarnings("unchecked")
						ImmutableList<Span> spans = (ImmutableList<Span>) pat
								.getTransferData(PotentialAnnotationTransfer.dataFlavor);
						Mention m = (Mention) a;
						documentModel.getCoreferenceModel().edit(new Op.AddMentionsToEntity(m.getEntity(), spans));

					} catch (UnsupportedFlavorException | IOException e) {
						Annotator.logger.catching(e);
					}
				}
			}
			return true;
		}
	}

	class ClearAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ClearAction() {
			super(Constants.Strings.ACTION_CLEAR, MaterialDesign.MDI_FORMAT_CLEAR);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			// TODO: New operation for clearing
			for (Mention m : JCasUtil.select(jcas, Mention.class))
				documentModel.getCoreferenceModel().edit(new Op.RemoveMention(m));
			for (Entity e : JCasUtil.select(jcas, Entity.class))
				documentModel.getCoreferenceModel().edit(new Op.RemoveEntities(e));

		}

	}

	class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_CLOSE));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (documentModel.isSavable()) {
				int r = JOptionPane.showConfirmDialog(DocumentWindow.this,
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE),
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_TITLE), JOptionPane.OK_CANCEL_OPTION);
				if (r == JOptionPane.OK_OPTION)
					closeWindow(false);
			} else
				closeWindow(false);
		}

	}

	class MergeSelectedEntities extends IkonAction {

		private static final long serialVersionUID = 1L;

		public MergeSelectedEntities() {
			super(Strings.ACTION_MERGE, MaterialDesign.MDI_CALL_MERGE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_MERGE_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentModel.getCoreferenceModel().edit(new Op.MergeEntities(getSelectedEntities()));

		}

	}

	@Deprecated
	class ToggleShowTextInTreeLabels extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleShowTextInTreeLabels() {
			super(Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS, MaterialDesign.MDI_FORMAT_TEXT);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS_TOOLTIP));
			putValue(Action.SELECTED_KEY,
					Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean old = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);
			Annotator.app.getPreferences().putBoolean(Constants.CFG_SHOW_TEXT_LABELS, !old);
			putValue(Action.SELECTED_KEY, !old);
			tree.repaint();
		}

	}

	class TreeMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
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
				Annotator.logger.trace("Right-clicked in text at " + e.getPoint());
				mouseClickedPosition = textPane.viewToModel(e.getPoint());

				// if (textPane.getSelectionStart() != textPane.getSelectionEnd())
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
			int offset = mouseClickedPosition;

			MutableList<Annotation> localAnnotations = Lists.mutable
					.withAll(documentModel.getCoreferenceModel().getMentions(offset));

			MutableList<Annotation> mentions = localAnnotations
					.select(m -> m instanceof Mention || m instanceof DetachedMentionPart);

			JMenu subMenu = new JMenu(Annotator.getString(Constants.Strings.MENU_ENTITIES));
			for (Annotation anno : mentions) {
				if (anno instanceof Mention)
					subMenu.add(this.getMentionItem((Mention) anno, ((Mention) anno).getDiscontinuous()));
				else if (anno instanceof DetachedMentionPart)
					subMenu.add(getMentionItem(((DetachedMentionPart) anno).getMention(), (DetachedMentionPart) anno));
			}
			if (subMenu.getMenuComponentCount() > 0)
				textPopupMenu.add(subMenu);

			MutableList<Annotation> comments = localAnnotations.select(m -> m instanceof CommentAnchor);
			subMenu = new JMenu(Annotator.getString(Constants.Strings.MENU_COMMENTS));
			subMenu.setIcon(FontIcon.of(MaterialDesign.MDI_COMMENT));

			for (Annotation anno : comments) {
				if (anno instanceof CommentAnchor)
					subMenu.add(getCommentItem((CommentAnchor) anno));
			}
			if (subMenu.getMenuComponentCount() > 0)
				textPopupMenu.add(subMenu);

			if (textPane.getSelectionStart() != textPane.getSelectionEnd()) {

				Set<Entity> candidates = Sets.mutable.empty();
				for (EntityRankingPlugin erp : new EntityRankingPlugin[] {
						Annotator.app.getPluginManager().getEntityRankingPlugin(MatchingRanker.class),
						Annotator.app.getPluginManager().getEntityRankingPlugin(PreceedingRanker.class) }) {
					candidates.addAll(erp.rank(getSelection(), getCoreferenceModel(), getJCas()).take(5));
				}
				JMenu candMenu = new JMenu(Annotator.getString(Constants.Strings.MENU_ENTITIES_CANDIDATES));
				candMenu.add(actions.newEntityAction);
				candMenu.addSeparator();
				candidates.forEach(entity -> {
					JMenuItem mi = new JMenuItem(Util.toString(getCoreferenceModel().getLabel(entity)));
					mi.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							getCoreferenceModel().edit(new Op.AddMentionsToEntity(entity, getSelection()));
						}
					});
					mi.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(entity.getColor())));
					candMenu.add(mi);
				});

				textPopupMenu.add(candMenu);

			}

		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			((JPopupMenu) e.getSource()).removeAll();

		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {

		}

		private JMenu getCommentItem(CommentAnchor anno) {
			Comment c = documentModel.getCommentsModel().get(anno);
			StringBuilder b = new StringBuilder();
			if (c.getAuthor() != null)
				b.append(c.getAuthor());
			else
				b.append("Unknown author");
			JMenu subMenu = new JMenu(b.toString());
			subMenu.add("\"" + StringUtils.abbreviateMiddle(c.getValue(), "[...]", 50) + "\"");
			subMenu.add(commentsWindow.commentList.get(c).editAction);
			subMenu.add(commentsWindow.commentList.get(c).deleteAction);
			return subMenu;
		}

		protected JMenu getMentionItem(Mention m, DetachedMentionPart dmp) {
			StringBuilder b = new StringBuilder();
			b.append(m.getAddress());

			String surf = m.getCoveredText();

			if (dmp != null)
				surf += " [,] " + dmp.getCoveredText();
			if (m.getEntity().getLabel() != null)
				b.append(": ").append(m.getEntity().getLabel());

			JMenu mentionMenu = new JMenu(b.toString());
			mentionMenu.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(m.getEntity().getColor())));
			Action a = new ShowMentionInTreeAction(DocumentWindow.this, m);
			mentionMenu.add('"' + surf + '"');
			mentionMenu.add(a);
			mentionMenu.add(new DeleteMentionAction(m));

			return mentionMenu;
		}
	}

	class MyTreeSelectionListener extends CATreeSelectionListener {

		public MyTreeSelectionListener(JTree tree) {
			super(tree);
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			collectData(e);
			actions.renameAction.setEnabled(isSingle() && isEntity());
			actions.changeKeyAction.setEnabled(isSingle() && isEntity());
			actions.changeColorAction.setEnabled(isSingle() && isEntity());

			actions.toggleEntityGeneric.setEnabled(this);

			actions.deleteAction.setEnabled(isDetachedMentionPart() || isMention() || (isEntityGroup() && isLeaf())
					|| (isEntity() && isLeaf()));

			actions.formGroupAction.setEnabled(this);

			actions.mergeSelectedEntitiesAction.setEnabled(!isSingle() && isEntity());

			actions.toggleMentionDifficult.setEnabled(this);
			actions.toggleMentionAmbiguous.setEnabled(this);
			actions.toggleMentionNonNominal.setEnabled(this);
			actions.toggleEntityDisplayed.setEnabled(this);

			actions.removeDuplicatesAction.setEnabled(isEntity());

			actions.entityStatisticsAction.setEnabled(isEntity());

			if (isSingle() && (isMention() || isDetachedMentionPart()))
				annotationSelected(getAnnotation(0));
			else
				annotationSelected(null);
		}

	}

	class CommentAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		Comment comment;

		public CommentAction(Comment c) {
			super(MaterialDesign.MDI_MESSAGE_PLUS);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_COMMENT));
			this.comment = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			commentsWindow.setVisible(true);
			commentsWindow.enterNewComment(textPane.getSelectionStart(), textPane.getSelectionEnd());
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
				documentModel.getTreeModel().getRoot();
				for (int i = 0; i < documentModel.getTreeModel().getRoot().getChildCount(); i++) {
					CATreeNode tn = documentModel.getTreeModel().getRoot().getChildAt(i);
					if (tn.isEntity()) {
						tn.setRank(matches(s, tn) ? 60 : 40);
						tree.scrollRowToVisible(0);
					}
				}
				// cModel.nodeStructureChanged(cModel.getRootNode());
				documentModel.getTreeModel().resort(EntitySortOrder
						.getVisibilitySortOrder(documentModel.getTreeModel().getEntitySortOrder().getComparator()));
			} else {
				for (int i = 0; i < documentModel.getTreeModel().getRoot().getChildCount(); i++) {
					CATreeNode tn = documentModel.getTreeModel().getRoot().getChildAt(i);
					if (tn.isEntity()) {
						tn.setRank(50);

					}
				}
				// cModel.nodeStructureChanged(cModel.getRootNode());
				documentModel.getTreeModel().resort();
			}
		}

		protected boolean matches(String s, CATreeNode e) {
			if (!e.isEntity())
				return false;
			Matcher m;

			if (e.getEntity().getLabel() != null) {
				m = pattern.matcher(e.getEntity().getLabel());
				if (m.find())
					return true;
			}
			StringArray flags = e.getEntity().getFlags();
			if (flags != null)
				for (int i = 0; i < e.getEntity().getFlags().size(); i++) {
					m = pattern.matcher(e.getEntity().getFlags(i));
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

		boolean ignoreNext = false;

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent ev) {
			if (ignoreNext)
				ignoreNext = false;
			else if (tree.hasFocus() && ev.getKeyCode() == KeyEvent.VK_ENTER) {
				int b = textPane.getSelectionStart(), e = textPane.getSelectionEnd();
				if (b != e) {
					for (TreePath tp : tree.getSelectionPaths()) {
						if (((CATreeNode) tp.getLastPathComponent()).isEntity()) {
							CATreeNode etn = (CATreeNode) tp.getLastPathComponent();
							documentModel.getCoreferenceModel()
									.edit(new Op.AddMentionsToEntity(etn.getEntity(), new Span(b, e)));
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

		public boolean isIgnoreNext() {
			return ignoreNext;
		}

		public void setIgnoreNext(boolean ignoreNext) {
			this.ignoreNext = ignoreNext;
		}

	}

	@Override
	public JTree getTree() {
		return tree;
	}

	public CoreferenceModel getCoreferenceModel() {
		return documentModel.getCoreferenceModel();
	}

	public StylePlugin getCurrentStyle() {
		return currentStyle;
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	@Override
	public String getText() {
		return textPane.getText();
	}

	@Override
	public Span getSelection() {
		return new Span(textPane.getSelectionStart(), textPane.getSelectionEnd());
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	@Override
	public void documentStateEvent(DocumentState state) {
		setWindowTitle();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public CommentWindow getCommentsWindow() {
		return commentsWindow;
	}

	class ActionContainer {

		AbstractAction clearAction = new ClearAction();
		AbstractAction closeAction = new CloseAction();
		AbstractAction changeColorAction;
		AbstractAction changeKeyAction;
		AbstractAction commentAction = new CommentAction(null);
		AbstractAction copyAction;
		DeleteAction deleteAction;
		FileSaveAction fileSaveAction;
		ToggleEntityVisible toggleEntityDisplayed = new ToggleEntityVisible(DocumentWindow.this);
		ToggleEntityGeneric toggleEntityGeneric;
		ToggleMentionAmbiguous toggleMentionAmbiguous;
		ToggleMentionDifficult toggleMentionDifficult;
		ToggleMentionNonNominal toggleMentionNonNominal = new ToggleMentionNonNominal(DocumentWindow.this);
		AbstractAction toggleShowTextInTreeLabels;
		AbstractAction toggleTrimWhitespace;
		UndoAction undoAction;
		AbstractAction setDocumentLanguageAction = new SetLanguageAction(DocumentWindow.this);
		AbstractAction showSearchPanelAction;
		AbstractAction sortByAlpha;
		AbstractAction sortByMentions;
		AbstractAction sortDescending = new ToggleEntitySortOrder(DocumentWindow.this);
		FormEntityGroup formGroupAction = new FormEntityGroup(DocumentWindow.this);
		AbstractAction mergeSelectedEntitiesAction = new MergeSelectedEntities();
		AbstractAction newEntityAction;
		AbstractAction renameAction;
		AbstractAction removeDuplicatesAction;
		EntityStatisticsAction entityStatisticsAction;

	}

	public ImmutableSet<Entity> getSelectedEntities() {
		Entity[] entities = new Entity[tree.getSelectionPaths().length];
		for (int i = 0; i < tree.getSelectionPaths().length; i++) {
			entities[i] = ((CATreeNode) tree.getSelectionPaths()[i].getLastPathComponent()).getEntity();
		}
		return Sets.immutable.of(entities);
	}
}
