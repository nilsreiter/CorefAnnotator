package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
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
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
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
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.AddCurrentSpanToCurrentEntity;
import de.unistuttgart.ims.coref.annotator.action.ChangeColorForEntity;
import de.unistuttgart.ims.coref.annotator.action.ChangeKeyForEntityAction;
import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.action.DeleteAction;
import de.unistuttgart.ims.coref.annotator.action.DeleteAllMentionsInSelection;
import de.unistuttgart.ims.coref.annotator.action.EntityStatisticsAction;
import de.unistuttgart.ims.coref.annotator.action.ExampleExport;
import de.unistuttgart.ims.coref.annotator.action.FileExportAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAsAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FormEntityGroup;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.action.MergeAdjacentMentions;
import de.unistuttgart.ims.coref.annotator.action.MergeSelectedEntities;
import de.unistuttgart.ims.coref.annotator.action.NewEntityAction;
import de.unistuttgart.ims.coref.annotator.action.ProcessAction;
import de.unistuttgart.ims.coref.annotator.action.RemoveDuplicatesAction;
import de.unistuttgart.ims.coref.annotator.action.RemoveForeignAnnotationsAction;
import de.unistuttgart.ims.coref.annotator.action.RemoveSingletons;
import de.unistuttgart.ims.coref.annotator.action.RenameAllEntitiesAction;
import de.unistuttgart.ims.coref.annotator.action.RenameEntityAction;
import de.unistuttgart.ims.coref.annotator.action.SelectNextMentionAction;
import de.unistuttgart.ims.coref.annotator.action.SelectPreviousMentionAction;
import de.unistuttgart.ims.coref.annotator.action.SetLanguageAction;
import de.unistuttgart.ims.coref.annotator.action.ShowDocumentStatistics;
import de.unistuttgart.ims.coref.annotator.action.ShowFlagEditor;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.action.ShowMentionInTreeAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.action.SortTree;
import de.unistuttgart.ims.coref.annotator.action.TargetedIkonAction;
import de.unistuttgart.ims.coref.annotator.action.ToggleEntitySortOrder;
import de.unistuttgart.ims.coref.annotator.action.ToggleFlagAction;
import de.unistuttgart.ims.coref.annotator.action.UndoAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontFamilySelectAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeDecreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeIncreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineNumberStyle;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineSpacingAction;
import de.unistuttgart.ims.coref.annotator.action.ViewStyleSelectAction;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.comp.EntityPanel;
import de.unistuttgart.ims.coref.annotator.comp.FixedTextLineNumber;
import de.unistuttgart.ims.coref.annotator.comp.FlagMenu;
import de.unistuttgart.ims.coref.annotator.comp.ImprovedMessageDialog;
import de.unistuttgart.ims.coref.annotator.comp.SegmentedScrollBar;
import de.unistuttgart.ims.coref.annotator.comp.SortingTreeModelListener;
import de.unistuttgart.ims.coref.annotator.comp.TextLineNumber;
import de.unistuttgart.ims.coref.annotator.comp.Tooltipable;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.DocumentState;
import de.unistuttgart.ims.coref.annotator.document.DocumentStateListener;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.FlagModelListener;
import de.unistuttgart.ims.coref.annotator.document.op.AddEntityToEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AttachPart;
import de.unistuttgart.ims.coref.annotator.document.op.MoveMentionPartToMention;
import de.unistuttgart.ims.coref.annotator.document.op.MoveMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMention;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityName;
import de.unistuttgart.ims.coref.annotator.plugin.rankings.MatchingRanker;
import de.unistuttgart.ims.coref.annotator.plugin.rankings.PreceedingRanker;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.EntityRankingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import de.unistuttgart.ims.coref.annotator.profile.Parser;
import de.unistuttgart.ims.coref.annotator.profile.Profile;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;
import de.unistuttgart.ims.coref.annotator.worker.DocumentModelLoader;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;

public class DocumentWindow extends AbstractTextWindow implements CaretListener, CoreferenceModelListener, HasTextView,
		DocumentStateListener, HasTreeView, HasDocumentModel {

	private static final long serialVersionUID = 1L;

	File file;

	String segmentAnnotation = null;

	// storing and caching
	int mouseClickedPosition = -1;

	// actions
	ActionContainer actions = new ActionContainer();

	// Window components
	JTree tree;
	StyleContext styleContext = new StyleContext();
	JLabel selectionDetailPanel;
	JSplitPane splitPane;
	JTextField treeSearchField;
	MyTreeSelectionListener treeSelectionListener;
	MutableSet<DocumentStateListener> documentStateListeners = Sets.mutable.empty();
	SegmentedScrollBar<Segment> segmentIndicator;
	// Menu components
	JMenu documentMenu;
	JMenu recentMenu;
	JMenu windowsMenu;
	JPopupMenu treePopupMenu;
	JPopupMenu textPopupMenu;
	Map<StylePlugin, JRadioButtonMenuItem> styleMenuItem = new HashMap<StylePlugin, JRadioButtonMenuItem>();
	FlagMenu mentionFlagsInTreePopup, entityFlagsInTreePopup, mentionFlagsInMenuBar, entityFlagsInMenuBar;

	// Settings
	StylePlugin currentStyle;

	// sub windows
	SearchDialog searchPanel;

	// temporary
	transient MutableSet<CATreeNode> expanded = Sets.mutable.empty();

	public DocumentWindow() {
		super();
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DocumentWindowWindowListener());

	}

	/*
	 * Initialisation
	 */
	public void initialise() {
		this.initialiseActions();
		this.initialiseMenu();
		this.initializeWindow();
		this.setVisible(true);
	}

	@Override
	protected void initializeWindow() {
		super.initializeWindow();

		mentionFlagsInTreePopup = new FlagMenu(Annotator.getString(Strings.MENU_FLAGS), this, Mention.class);
		entityFlagsInTreePopup = new FlagMenu(Annotator.getString(Strings.MENU_FLAGS), this, Entity.class);

		// tree popup
		treePopupMenu = new JPopupMenu();
		treePopupMenu.add(this.actions.deleteAction);
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		treePopupMenu.add(mentionFlagsInTreePopup);
		treePopupMenu.add(this.actions.mergeMentions);
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		treePopupMenu.add(this.actions.newEntityAction);
		treePopupMenu.add(this.actions.renameAction);
		treePopupMenu.add(this.actions.changeColorAction);
		treePopupMenu.add(this.actions.changeKeyAction);
		treePopupMenu.add(this.actions.mergeSelectedEntitiesAction);
		treePopupMenu.add(this.actions.formGroupAction);
		treePopupMenu.add(this.actions.removeDuplicatesAction);
		treePopupMenu.add(entityFlagsInTreePopup);
		treePopupMenu.add(this.actions.entityStatisticsAction);

		textPopupMenu = new JPopupMenu();
		textPopupMenu.addPopupMenuListener(new PopupListener());

		// initialise panel

		TreeMouseListener tml = new TreeMouseListener();
		treeSelectionListener = new MyTreeSelectionListener();

		tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(null, false)));
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setLargeModel(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setTransferHandler(new MyTreeTransferHandler());
		tree.setCellRenderer(new MyTreeCellRenderer());
		tree.setCellEditor(new MyTreeCellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()));
		tree.addTreeSelectionListener(treeSelectionListener);
		tree.addTreeSelectionListener(actions.deleteAction);
		tree.addTreeSelectionListener(actions.formGroupAction);
		tree.addTreeSelectionListener(actions.renameAction);
		tree.addTreeSelectionListener(actions.mergeMentions);
		tree.addMouseListener(tml);
		tree.addMouseMotionListener(tml);
		tree.setEditable(true);
		tree.getActionMap().put(AddCurrentSpanToCurrentEntity.class, new AddCurrentSpanToCurrentEntity(this));
		tree.getActionMap().put(DeleteAction.class, actions.deleteAction);
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), AddCurrentSpanToCurrentEntity.class);
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), DeleteAction.class);

		Annotator.app.getPreferences().addPreferenceChangeListener((PreferenceChangeListener) tree.getCellRenderer());

		ToolTipManager.sharedInstance().registerComponent(tree);

		treeSearchField = new JTextField();
		EntityFinder entityFinder = new EntityFinder();
		treeSearchField.getDocument().addDocumentListener(entityFinder);
		treeSearchField.addKeyListener(entityFinder);
		JPanel rightPanel = new JPanel(new BorderLayout());
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
		miscLabel2.setPreferredSize(new Dimension(150, 20));

		// initialise text view
		Caret caret = new Caret();
		TextMouseListener textMouseListener = new TextMouseListener();
		textPane.setPreferredSize(new Dimension(600, 800));
		textPane.setTransferHandler(new TextViewTransferHandler());
		textPane.addMouseListener(textMouseListener);
		textPane.addMouseMotionListener(textMouseListener);
		textPane.setCaret(caret);
		textPane.getCaret().setVisible(true);
		textPane.addCaretListener(new TextCaretListener());
		textPane.addCaretListener(actions.deleteAllAction);
		textPane.addCaretListener(actions.deleteAction);
		textPane.addFocusListener(caret);
		textPane.addKeyListener(new TextViewKeyListener());
		textPane.setCaretPosition(0);
		textPane.addCaretListener(this);
		textPane.getActionMap().put(DeleteAction.class, actions.deleteAction);
		textPane.getActionMap().put(CopyAction.class, new CopyAction(this));
		textPane.getActionMap().put(DeleteAllMentionsInSelection.class, actions.deleteAllAction);
		textPane.getActionMap().put(SelectNextMentionAction.class, new SelectNextMentionAction(this));
		textPane.getActionMap().put(SelectPreviousMentionAction.class, new SelectPreviousMentionAction(this));
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
				CopyAction.class);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), DeleteAction.class);
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
				DeleteAllMentionsInSelection.class);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK),
				SelectNextMentionAction.class);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK),
				SelectPreviousMentionAction.class);

		highlightManager = new HighlightManager(textPane);

		// scrollPane.setRowHeaderView(segmentIndicator);

		segmentIndicator = new SegmentedScrollBar<Segment>(textScrollPane);

		textScrollPane.setVerticalScrollBar(segmentIndicator);
		// leftPanel.add(segmentIndicator, BorderLayout.LINE_START);

		// split pane
		if (false) {
			getContentPane().add(textPanel);
			setPreferredSize(new Dimension(600, 800));
			setLocationRelativeTo(Annotator.app.opening);
			JFrame treeFrame = new JFrame();
			treeFrame.setContentPane(rightPanel);
			treeFrame.setPreferredSize(new Dimension(200, 800));
			treeFrame.pack();
			treeFrame.setLocation(this.getLocation().x + 600, this.getLocation().y);
			treeFrame.setVisible(true);
		} else {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textPanel, rightPanel);
			splitPane.setVisible(true);
			splitPane.setDividerLocation(500);
			setPreferredSize(new Dimension(900, 800));
			setLocationRelativeTo(Annotator.app.opening);
			getContentPane().add(splitPane);
		}
		pack();
		Annotator.logger.info("Window initialised.");
	}

	protected void initialiseActions() {
		this.actions.renameAction = new RenameEntityAction(this);
		this.actions.newEntityAction = new NewEntityAction(this);
		this.actions.changeColorAction = new ChangeColorForEntity(this);
		this.actions.changeKeyAction = new ChangeKeyForEntityAction(this);
		this.actions.deleteAction = new DeleteAction(this);
		this.actions.sortByAlpha = SortTree.getSortByAlphabet(this);
		this.actions.sortByMentions = SortTree.getSortByMention(this);
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
		actions.undoAction.setEnabled(false);
		actions.entityStatisticsAction.setEnabled(false);
		actions.fileSaveAction.setEnabled(false);

		// connect listeners
		documentStateListeners.add(actions.undoAction);
		documentStateListeners.add(actions.fileSaveAction);

		Annotator.logger.trace("Actions initialised.");

	}

	@Override
	protected JMenu initialiseMenuView() {
		JRadioButtonMenuItem radio;
		JMenu viewMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW));
		viewMenu.add(new ViewFontSizeDecreaseAction(this));
		viewMenu.add(new ViewFontSizeIncreaseAction(this));

		ButtonGroup grp = new ButtonGroup();

		JMenu lineSpacingMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_LINE_SPACING));
		lineSpacingMenu.setIcon(FontIcon.of(MaterialDesign.MDI_FORMAT_LINE_SPACING));
		for (int i = 0; i < 10; i++) {
			ViewSetLineSpacingAction action = new ViewSetLineSpacingAction(this, i * 0.5f);
			radio = new JRadioButtonMenuItem(action);
			grp.add(radio);
			lineSpacingMenu.add(radio);
			this.addStyleChangeListener(action);
		}

		viewMenu.add(lineSpacingMenu);

		JMenu fontFamilyMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_FONTFAMILY));
		String[] fontFamilies = new String[] { Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED };
		grp = new ButtonGroup();
		for (String s : fontFamilies) {
			AbstractAction a = new ViewFontFamilySelectAction(this, s);
			radio = new JRadioButtonMenuItem(a);
			fontFamilyMenu.add(radio);
			grp.add(radio);
		}
		// TODO: Disabled for the moment
		// viewMenu.add(fontFamilyMenu);

		grp = new ButtonGroup();
		JMenu lineNumbersMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_LINE_NUMBERS));
		radio = new JRadioButtonMenuItem(actions.lineNumberStyleNone);
		radio.setSelected(true);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		radio = new JRadioButtonMenuItem(actions.lineNumberStyleFixed);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		radio = new JRadioButtonMenuItem(actions.lineNumberStyleDynamic);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		viewMenu.add(lineNumbersMenu);
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
		toolsMenu.add(new RenameAllEntitiesAction(this));
		toolsMenu.add(new RemoveForeignAnnotationsAction(this));
		toolsMenu.add(new ShowFlagEditor(this));
		toolsMenu.add(new ShowDocumentStatistics(this));
		toolsMenu.addSeparator();
		// toolsMenu.add(new ShowHistoryAction(this));
		toolsMenu.add(new ShowLogWindowAction(Annotator.app));
		return toolsMenu;
	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_IMPORT_FROM));
		JMenu fileExportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_EXPORT_AS));

		PluginManager pm = Annotator.app.getPluginManager();

		for (ImportPlugin iplugin : pm.getIOPluginObjects().selectInstancesOf(ImportPlugin.class)) {
			fileImportMenu.add(new FileImportAction(Annotator.app, iplugin));
		}

		for (ExportPlugin plugin : pm.getIOPluginObjects().selectInstancesOf(ExportPlugin.class)) {
			fileExportMenu.add(new FileExportAction(this, this, plugin));

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
		mentionFlagsInMenuBar = new FlagMenu(Annotator.getString(Strings.MENU_FLAGS), this, Mention.class);
		entityFlagsInMenuBar = new FlagMenu(Annotator.getString(Strings.MENU_FLAGS), this, Entity.class);

		JMenu entityMenu = new JMenu(Annotator.getString(Strings.MENU_EDIT));
		entityMenu.add(new JMenuItem(actions.undoAction));
		entityMenu.add(new JMenuItem(actions.copyAction));
		entityMenu.add(new JMenuItem(actions.deleteAction));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		// entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionAmbiguous));
		// entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionDifficult));
		// entityMenu.add(new JCheckBoxMenuItem(actions.toggleMentionNonNominal));
		entityMenu.add(actions.deleteAllAction);
		entityMenu.add(mentionFlagsInMenuBar);
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		entityMenu.add(new JMenuItem(actions.newEntityAction));
		entityMenu.add(new JMenuItem(actions.renameAction));
		entityMenu.add(new JMenuItem(actions.changeColorAction));
		entityMenu.add(new JMenuItem(actions.changeKeyAction));
		entityMenu.add(new JMenuItem(actions.formGroupAction));
		entityMenu.add(new JMenuItem(new RemoveSingletons(this)));
		entityMenu.add(entityFlagsInMenuBar);
		// entityMenu.add(new JCheckBoxMenuItem(actions.toggleEntityGeneric));
		// entityMenu.add(new JCheckBoxMenuItem(actions.toggleEntityDisplayed));
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

		Annotator.logger.info("Initialised menus");
	}

	protected void closeWindow(boolean quit) {
		if (getDocumentModel().isSavable()) {
			Annotator.logger.warn("Closing window with unsaved changes");
		}
		if (searchPanel != null) {
			searchPanel.setVisible(false);
			searchPanel.dispose();
			searchPanel = null;
		}
		Annotator.app.close(this);
	}

	public void loadFile(File file, ImportPlugin flavor, String language) {
		if (flavor instanceof DefaultImportPlugin)
			this.file = file;

		JCasLoader lai;
		setMessage(Annotator.getString(Strings.MESSAGE_LOADING));
		setIndeterminateProgress();
		File profileFile = new File(file.getParentFile(), "profile.xml");
		final Profile profile = new Parser().getProfileOrNull(profileFile);
		if (flavor instanceof UimaImportPlugin) {
			lai = new JCasLoader(file, (UimaImportPlugin) flavor, language, jcas -> {
				this.setJCas(jcas, profile);
			}, ex -> {
				String[] options = new String[] { Annotator.getString("message.wrong_file_version.ok"),
						Annotator.getString("message.wrong_file_version.help") };
				ImprovedMessageDialog.showMessageDialog(this, Annotator.getString("message.wrong_file_version.title"),
						ex.getCause().getLocalizedMessage(), options, new BooleanSupplier[] { () -> {
							return true;
						}, () -> {
							try {
								Desktop.getDesktop().browse(new URI(
										"https://github.com/nilsreiter/CorefAnnotator/wiki/File-format-versions"));
							} catch (IOException | URISyntaxException e) {
								Annotator.logger.catching(e);
							}
							return true;
						} });
				setVisible(false);
				dispose();

			});
			lai.execute();
		}

	}

	@Deprecated
	class SortTreeByAlpha extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByAlpha() {
			super(MaterialDesign.MDI_SORT_ALPHABETICAL);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_ALPHA));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getDocumentModel().getTreeModel().setEntitySortOrder(EntitySortOrder.Alphabet);
			getDocumentModel().getTreeModel().getEntitySortOrder().descending = false;
			getDocumentModel().getTreeModel().resort();
			actions.sortDescending.putValue(Action.SELECTED_KEY, false);
		}

	}

	@Deprecated
	class SortTreeByMentions extends IkonAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByMentions() {
			super(MaterialDesign.MDI_SORT_NUMERIC);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_MENTIONS));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_MENTIONS_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getDocumentModel().getTreeModel().setEntitySortOrder(EntitySortOrder.Mentions);
			getDocumentModel().getTreeModel().getEntitySortOrder().descending = true;
			getDocumentModel().getTreeModel().resort();
			actions.sortDescending.putValue(Action.SELECTED_KEY, true);
		}

	}

	@Override
	public void setLineNumberStyle(LineNumberStyle lns) {
		TextLineNumber tln;
		switch (lns) {
		case FIXED:
			tln = new FixedTextLineNumber(this, 5);
			pcs.addPropertyChangeListener(tln);
			break;
		case DYNAMIC:
			tln = new TextLineNumber(this, 5);
			pcs.addPropertyChangeListener(tln);
			break;
		default:
			tln = null;
		}
		textScrollPane.setRowHeaderView(tln);
		super.setLineNumberStyle(lns);
	}

	public void setWindowTitle() {
		String fileName = (file != null ? file.getName() : Annotator.getString(Strings.WINDOWTITLE_NEW_FILE));

		setTitle(getDocumentModel().getDocumentTitle() + " (" + fileName + ")"
				+ (getDocumentModel().isSavable() ? " -- " + Annotator.getString(Strings.WINDOWTITLE_EDITED) : ""));
	}

	public void showSearch() {
		if (searchPanel == null) {
			searchPanel = new SearchDialog(this, Annotator.app.getPreferences());
		}
		searchPanel.setVisible(true);
	}

	@Override
	protected void entityEventMove(FeatureStructureEvent event) {
		for (FeatureStructure fs : event)
			if (fs instanceof Mention) {
				highlightManager.unUnderline((Annotation) fs);
				highlightManager.underline((Mention) fs, new Color(((Entity) event.getArgument2()).getColor()));
			}
	}

	@Override
	public void setDocumentModel(DocumentModel model) {
		super.setDocumentModel(model);

		MyTreeModelListener modelHandler = new MyTreeModelListener();

		tree.setModel(model.getTreeModel());
		model.addDocumentStateListener(this);
		if (tableOfContents != null)
			tableOfContents.setModel(model.getSegmentModel());

		if (model.hasLineNumbers()) {
			actions.lineNumberStyleFixed.setEnabled(true);
			this.setLineNumberStyle(actions.lineNumberStyleFixed.getStyle());
		}
		actions.newEntityAction.setEnabled(true);
		actions.changeColorAction.setEnabled(true);
		actions.changeKeyAction.setEnabled(true);
		highlightManager.setDocumentModel(model);

		// listeners to the coref model
		model.getCoreferenceModel().addCoreferenceModelListener(this);

		// listeners to the tree model
		model.getTreeModel().addTreeModelListener((TreeModelListener) modelHandler);
		model.getTreeModel().addTreeModelListener((SortingTreeModelListener) modelHandler);
		model.getTreeModel().addEntitySortOrderListener(actions.sortByAlpha);
		model.getTreeModel().addEntitySortOrderListener(actions.sortByMentions);
		model.getTreeModel().addEntitySortOrderListener(actions.sortDescending);

		// listeners to the flag model
		model.getFlagModel().addFlagModelListener(entityFlagsInMenuBar);
		model.getFlagModel().addFlagModelListener(entityFlagsInTreePopup);
		model.getFlagModel().addFlagModelListener(mentionFlagsInMenuBar);
		model.getFlagModel().addFlagModelListener(mentionFlagsInTreePopup);
		model.getFlagModel().addFlagModelListener(modelHandler);

		// listeners to the segment model
		if (model.getSegmentModel().getTopLevelSegments().size() <= Constants.MAX_SEGMENTS_IN_SCROLLBAR) {
			model.getSegmentModel().addListDataListener(segmentIndicator);
			segmentIndicator.setLastCharacterPosition(model.getJcas().getDocumentText().length());
		}

		for (Flag f : model.getFlagModel().getFlags()) {
			ToggleFlagAction a = new ToggleFlagAction(DocumentWindow.this, model.getFlagModel(), f);
			tree.addTreeSelectionListener(a);
			try {
				if (model.getFlagModel().getTargetClass(f).equals(Mention.class)) {
					mentionFlagsInTreePopup.add(f, new JCheckBoxMenuItem(a));
					mentionFlagsInMenuBar.add(f, new JCheckBoxMenuItem(a));
				} else {
					entityFlagsInMenuBar.add(f, new JCheckBoxMenuItem(a));
					entityFlagsInTreePopup.add(f, new JCheckBoxMenuItem(a));
				}
			} catch (ClassNotFoundException e) {
				mentionFlagsInTreePopup.add(f, new JCheckBoxMenuItem(a));
				mentionFlagsInMenuBar.add(f, new JCheckBoxMenuItem(a));
				entityFlagsInMenuBar.add(f, new JCheckBoxMenuItem(a));
				entityFlagsInTreePopup.add(f, new JCheckBoxMenuItem(a));
			}

		}

		// set sorting of tree
		EntitySortOrder eso = EntitySortOrder.valueOf(getDocumentModel().getPreferences()
				.get(Constants.CFG_ENTITY_SORT_ORDER, Defaults.CFG_ENTITY_SORT_ORDER.toString()));
		eso.descending = getDocumentModel().getPreferences().getBoolean(Constants.CFG_ENTITY_SORT_DESCENDING,
				Defaults.CFG_ENTITY_SORT_DESCENDING);
		getDocumentModel().getTreeModel().setEntitySortOrder(eso);
		getDocumentModel().getTreeModel().resort();

		// UI
		documentStateListeners.forEach(dsl -> getDocumentModel().addDocumentStateListener(dsl));
		stopIndeterminateProgress();
		Annotator.logger.debug("Setting loading progress to {}", 100);

		// Style
		StylePlugin sPlugin = null;
		try {
			sPlugin = Annotator.app.getPluginManager().getStylePlugin(model.getStylePlugin());
		} catch (ClassNotFoundException e1) {
			Annotator.logger.catching(e1);
		}

		if (sPlugin == null)
			sPlugin = Annotator.app.getPluginManager().getDefaultStylePlugin();

		StyleManager.styleParagraph(textPane.getStyledDocument(), StyleManager.getDefaultParagraphStyle());
		switchStyle(sPlugin);

		// show profile, if needed
		if (model.getProfile() != null)
			if (model.getProfile().getName() != null)
				miscLabel2.setText(Annotator.getString(Strings.STATUS_PROFILE) + ": " + model.getProfile().getName());
			else
				miscLabel2.setText(Annotator.getString(Strings.STATUS_PROFILE) + ": " + "Unknown");
		miscLabel2.repaint();

		// final
		setMessage("");
		pack();
		getDocumentModel().signal();
		Annotator.logger.info("Document model has been loaded.");

	}

	public void setJCas(JCas jcas) {
		setJCas(jcas, null);
	}

	public void setJCas(JCas jcas, Profile profile) {

		Annotator.logger.info("JCas has been loaded.");
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

		segmentIndicator.setLastCharacterPosition(jcas.getDocumentText().length());

		DocumentModelLoader im = new DocumentModelLoader(cm -> this.setDocumentModel(cm), jcas);
		im.setProfile(profile);
		im.execute();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		actions.newEntityAction.setEnabled(
				!(textPane.getSelectedText() == null || textPane.getSelectionStart() == textPane.getSelectionEnd()));
	}

	@Override
	public void updateStyle(Object constant, Object value) {
		MutableAttributeSet baseStyle = currentStyle.getBaseStyle();
		Object oldValue = baseStyle.getAttribute(constant);
		baseStyle.addAttribute(constant, value);
		pcs.firePropertyChange(constant.toString(), oldValue, value);
		switchStyle(currentStyle);
	}

	@Override
	public void switchStyle(StylePlugin sv) {
		switchStyle(sv, sv.getBaseStyle());
	}

	@Override
	public void switchStyle(StylePlugin sv, AttributeSet baseStyle) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				getProgressBar().setValue(0);
				getProgressBar().setVisible(true);
				Annotator.logger.debug("Activating style {}", sv.getClass().getName());

				getProgressBar().setValue(20);

				Map<AttributeSet, org.apache.uima.cas.Type> styles = sv
						.getSpanStyles(getDocumentModel().getJcas().getTypeSystem(), styleContext, baseStyle);
				StyleManager.styleCharacter(textPane.getStyledDocument(), baseStyle);

				for (Enumeration<?> e = baseStyle.getAttributeNames(); e.hasMoreElements();) {
					Object aName = e.nextElement();
					pcs.firePropertyChange(aName.toString(), null, baseStyle.getAttribute(aName));
				}
				textPane.getStyledDocument().setParagraphAttributes(0, textPane.getDocument().getLength(), baseStyle,
						true);

				if (styles != null)
					for (AttributeSet style : styles.keySet()) {
						StyleManager.style(getDocumentModel().getJcas(), textPane.getStyledDocument(), style,
								styles.get(style));
						getProgressBar().setValue(getProgressBar().getValue() + 10);
					}
				Util.getMeta(getDocumentModel().getJcas()).setStylePlugin(sv.getClass().getName());
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
				if (targetFS instanceof Entity)
					return true;
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
					&& !info.isDataFlavorSupported(NodeListTransferable.dataFlavor)
					&& !info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
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
					Operation operation = null;
					if (targetFS == null) {
						operation = new AddMentionsToNewEntity(paList);
					} else if (targetFS instanceof Entity) {
						operation = new AddMentionsToEntity((Entity) targetFS, paList);
					} else if (targetFS instanceof Mention) {
						operation = new AttachPart((Mention) targetFS, paList.getFirst());
					}
					if (operation != null) {
						getDocumentModel().edit(operation);
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
			} else if (dataFlavor == AnnotationTransfer.dataFlavor) {
				Object a;
				try {
					a = info.getTransferable().getTransferData(dataFlavor);
					@SuppressWarnings("unchecked")
					ImmutableList<Annotation> aList = (ImmutableList<Annotation>) a;
					if (aList.anySatisfy(anno -> anno instanceof Mention) && targetFS instanceof Entity)
						getDocumentModel().edit(
								new MoveMentionsToEntity((Entity) targetFS, aList.selectInstancesOf(Mention.class)));
				} catch (UnsupportedFlavorException | IOException e) {
					Annotator.logger.catching(e);
				}
			}

			return true;
		}

		protected boolean handleNodeMoving(ImmutableList<CATreeNode> moved) {
			Annotator.logger.debug("Moving {} things", moved.size());
			Operation operation = null;
			if (targetFS instanceof Entity) {
				if (moved.anySatisfy(n -> n.getFeatureStructure() instanceof Entity)
						&& targetFS instanceof EntityGroup) {
					operation = new AddEntityToEntityGroup((EntityGroup) targetFS,
							moved.select(n -> n.getFeatureStructure() instanceof Entity)
									.collect(n -> n.getFeatureStructure()));
				}
				if (moved.anySatisfy(n -> n.getFeatureStructure() instanceof Mention))
					getDocumentModel().edit(new MoveMentionsToEntity((Entity) targetFS,
							moved.select(n -> n.getFeatureStructure() instanceof Mention)
									.collect(n -> n.getFeatureStructure())));
			} else if (targetFS instanceof Mention)
				operation = new MoveMentionPartToMention((Mention) targetFS, moved.getFirst().getFeatureStructure());
			else
				return false;
			if (operation != null)
				getDocumentModel().edit(operation);
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

	class MyTreeCellEditor extends DefaultTreeCellEditor {

		public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
			super(tree, renderer);
			editingIcon = FontIcon.of(MaterialDesign.MDI_ACCOUNT);
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			CATreeNode node = (CATreeNode) value;
			Color color = new Color(node.getEntity().getColor());
			Component comp = super.getTreeCellEditorComponent(tree, node.getEntity().getLabel(), isSelected, expanded,
					leaf, row);
			comp.setForeground(color);
			return comp;
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			CATreeNode node = (CATreeNode) lastPath.getLastPathComponent();
			return super.isCellEditable(e) && node.isEntity() && !getDocumentModel().isBlocked(UpdateEntityName.class);
		}
	}

	class MyTreeCellRenderer extends DefaultTreeCellRenderer implements PreferenceChangeListener {

		private static final long serialVersionUID = 1L;
		boolean showText = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);

		CATreeNode treeNode;

		protected void addFlag(JPanel panel, Flag flag, Color color) {
			JLabel l = new JLabel();
			if (color != null)
				l.setForeground(color);
			if (showText)
				l.setText(Annotator.getStringWithDefault(flag.getLabel(), flag.getLabel()));
			l.setIcon(FontIcon.of(MaterialDesign.valueOf(flag.getIcon()), color));
			panel.add(Box.createRigidArea(new Dimension(5, 5)));
			panel.add(l);
		}

		@Deprecated
		protected void addFlag(JPanel panel, String textLabel, Icon icon, Color color) {
			JLabel l = new JLabel();
			if (color != null)
				l.setForeground(color);
			if (showText)
				l.setText(textLabel);
			l.setIcon(icon);
			panel.add(Box.createRigidArea(new Dimension(5, 5)));
			panel.add(l);
		}

		protected JPanel handleEntity(JPanel panel, JLabel lab1, Entity entity) {
			lab1.setText(entity.getLabel());

			boolean isGrey = Util.isX(entity, Constants.ENTITY_FLAG_HIDDEN) || treeNode.getRank() < 50;
			Color entityColor = new Color(entity.getColor());

			if (isGrey) {
				lab1.setForeground(Color.GRAY);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_OUTLINE, Color.GRAY));
			} else {
				lab1.setForeground(Color.BLACK);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, entityColor));
			}

			String visLabel = StringUtils.abbreviate(entity.getLabel(), "…", Constants.UI_MAX_STRING_WIDTH_IN_TREE);

			if (entity.getKey() != null) {
				lab1.setText(visLabel + " [" + entity.getKey() + "] (" + treeNode.getChildCount() + ")");
			} else if (!(treeNode.getParent().isEntity()))
				lab1.setText(visLabel + " (" + treeNode.getChildCount() + ")");
			if (entity instanceof EntityGroup) {
				panel.add(Box.createRigidArea(new Dimension(5, 5)));
				panel.add(new JLabel(FontIcon.of(MaterialDesign.MDI_ACCOUNT_MULTIPLE)));
			}
			if (entity.getFlags() != null)
				for (String flagKey : entity.getFlags()) {
					Flag flag = getDocumentModel().getFlagModel().getFlag(flagKey);
					addFlag(panel, flag, isGrey ? Color.GRAY : Color.BLACK);
				}
			return panel;
		}

		protected JPanel handleMention(JPanel panel, JLabel lab1, Mention m) {
			FlagModel fm = getDocumentModel().getFlagModel();

			// constructing text
			StringBuilder b = new StringBuilder();
			if (Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_LINE_NUMBER_IN_TREE,
					Defaults.CFG_SHOW_LINE_NUMBER_IN_TREE)) {
				Segment segment = getDocumentModel().getSegmentModel().getSegmentAt(m.getBegin());
				AnnotationTreeNode<Segment> tn = getDocumentModel().getSegmentModel().getAnnotationTreeNode(segment);
				String sep = "/";
				String ln = UimaUtil.toString(tn, sep, 20);
				Integer lineNumber = getDocumentModel().getLineNumber(m.getBegin());
				if (lineNumber != null)
					ln = ln + sep + lineNumber.toString();
				if (ln != null) {
					if (ln.startsWith(sep))
						ln = ln.substring(sep.length());
					b.append('(').append(ln).append(')').append(' ');
				}
			}
			b.append(m.getCoveredText());

			lab1.setText(b.toString());
			if (m.getFlags() != null)
				for (String flagKey : m.getFlags()) {
					Flag flag = fm.getFlag(flagKey);
					addFlag(panel, flag, Color.black);
				}

			lab1.setIcon(FontIcon.of(MaterialDesign.MDI_COMMENT_ACCOUNT));
			return panel;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			// we only handle instances of CATreeNode
			if (!(value instanceof CATreeNode))
				return new JLabel();

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
			if (treeNode instanceof Tooltipable)
				panel.setToolTipText(treeNode.getToolTip());

			// depending of node type, do different things
			if (treeNode.isEntity())
				return handleEntity(panel, mainLabel, treeNode.getEntity());
			else if (treeNode.isMention()) {
				return this.handleMention(panel, mainLabel, treeNode.getFeatureStructure());
			} else if (getDocumentModel() != null && getDocumentModel().getCoreferenceModel() != null
					&& treeNode == tree.getModel().getRoot())
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_PLUS));
			else if (getDocumentModel() != null && getDocumentModel().getCoreferenceModel() != null
					&& treeNode.getFeatureStructure() instanceof DetachedMentionPart)
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_TREE));

			return panel;
		}

		@Override
		public void preferenceChange(PreferenceChangeEvent evt) {
			showText = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);
			tree.repaint();
		}

		@Override
		public Icon getLeafIcon() {
			return getMentionIcon();
		}

		@Override
		public Icon getClosedIcon() {
			return getEntityIcon();
		}

		@Override
		public Icon getOpenIcon() {
			return getEntityIcon();
		}

		public Icon getMentionIcon() {
			return FontIcon.of(MaterialDesign.MDI_COMMENT_ACCOUNT);
		}

		public Icon getEntityIcon() {
			return FontIcon.of(MaterialDesign.MDI_ACCOUNT);
		}

	}

	class TextViewKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			CoreferenceModel cModel = getDocumentModel().getCoreferenceModel();

			if (cModel.getKeyMap().containsKey(e.getKeyChar())) {
				e.consume();
				if (Annotator.app.getPreferences().getBoolean(Constants.CFG_REPLACE_MENTION, false)
						&& getSelectedAnnotations(Mention.class).size() == 1) {
					getDocumentModel().edit(new MoveMentionsToEntity(cModel.getKeyMap().get(e.getKeyChar()),
							getSelectedAnnotations(Mention.class)));
				} else {
					getDocumentModel()
							.edit(new AddMentionsToEntity(cModel.getKeyMap().get(e.getKeyChar()), getSelection()));
				}
			} else if (e.getKeyChar() == ' ') {
				Rectangle p;
				try {
					p = getTextPane().modelToView(getTextPane().getSelectionStart());
					textPopupMenu.show(e.getComponent(), p.x, p.y + 15);
				} catch (BadLocationException e1) {
					Annotator.logger.catching(e1);
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
			if (Annotator.app.getPreferences().getBoolean(Constants.CFG_REPLACE_MENTION, false)
					&& getSelectedAnnotations(Mention.class).size() == 1) {
				Mention mention = getSelectedAnnotations(Mention.class).getOnly();
				return new AnnotationTransfer(mention, getDocumentModel().getTreeModel().get(mention));
			} else
				return new PotentialAnnotationTransfer(textPane, t.getSelectionStart(), t.getSelectionEnd());
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {

			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
				JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
				Collection<Annotation> mentions = getDocumentModel().getCoreferenceModel().getMentions(dl.getIndex());
				if (mentions.size() > 0)
					return true;
			} else if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
				try {
					@SuppressWarnings("unchecked")
					ImmutableList<Annotation> annoList = (ImmutableList<Annotation>) info.getTransferable()
							.getTransferData(AnnotationTransfer.dataFlavor);
					return annoList.anySatisfy(a -> a instanceof Mention);
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
			}
			return false;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}
			JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
			Collection<Annotation> mentions = getDocumentModel().getCoreferenceModel().getMentions(dl.getIndex());
			for (Annotation a : mentions) {
				if (a instanceof Mention) {
					try {
						Mention m = (Mention) a;
						Transferable pat = info.getTransferable();
						if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
							@SuppressWarnings("unchecked")
							ImmutableList<Span> spans = (ImmutableList<Span>) pat
									.getTransferData(PotentialAnnotationTransfer.dataFlavor);
							getDocumentModel().edit(new AddMentionsToEntity(m.getEntity(), spans));
						} else if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
							Object annotationList = pat.getTransferData(AnnotationTransfer.dataFlavor);
							if (annotationList instanceof ImmutableList<?>)
								getDocumentModel().edit(new MoveMentionsToEntity(m.getEntity(),
										((ImmutableList<?>) annotationList).selectInstancesOf(Mention.class)));
						}
					} catch (UnsupportedFlavorException | IOException e) {
						Annotator.logger.catching(e);
					}
				}
			}
			return true;
		}
	}

	class ClearAction extends TargetedIkonAction<DocumentWindow> {

		private static final long serialVersionUID = 1L;

		public ClearAction(DocumentWindow dm) {
			super(dm, Strings.ACTION_CLEAR, MaterialDesign.MDI_FORMAT_CLEAR);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			// TODO: New operation for clearing
			for (Mention m : Lists.immutable.withAll(JCasUtil.select(getDocumentModel().getJcas(), Mention.class)))
				getDocumentModel().edit(new RemoveMention(m));
			for (Entity e : Lists.immutable.withAll(JCasUtil.select(getDocumentModel().getJcas(), Entity.class)))
				getDocumentModel().edit(new RemoveEntities(e));
			getDocumentModel().getHistory().clear();
		}

	}

	class TreeMouseListener implements MouseListener, MouseMotionListener {

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
			treeSelectionListener.setEnabled(true);
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			treeSelectionListener.setEnabled(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

	class TextCaretListener implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			int dot = e.getDot();
			int mark = e.getMark();
			int low = Math.min(dot, mark);
			int high = Math.max(dot, mark);

			MutableSet<Mention> mentions = Sets.mutable.empty();
			if (getDocumentModel() != null && getDocumentModel().getCoreferenceModel() != null) {

				// nothing is selected: show all mentions cursor is part of
				MutableSet<? extends Annotation> annotations = Sets.mutable
						.withAll(getDocumentModel().getCoreferenceModel().getMentions(low));
				mentions = annotations.selectInstancesOf(Mention.class);

				// something is selected
				if (dot != mark) {
					ImmutableSet<Mention> ms = getDocumentModel().getCoreferenceModel().getMentions(low, high)
							.selectInstancesOf(Mention.class);
					mentions.addAllIterable(ms);
				}
				setCollectionPanel(mentions.collect(m -> {
					EntityPanel ep = new EntityPanel(getDocumentModel(), m.getEntity());
					Annotator.app.getPreferences().addPreferenceChangeListener(ep);
					getDocumentModel().getCoreferenceModel().addCoreferenceModelListener(ep);
					return ep;
				}));
			}
			if (getDocumentModel() != null)
				highlightSegmentInTOC(dot);

		}

	}

	class TextMouseListener implements MouseListener, MouseMotionListener {

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
			treeSelectionListener.setEnabled(true);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			treeSelectionListener.setEnabled(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	class PopupListener implements PopupMenuListener {
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			int offset = mouseClickedPosition != -1 ? mouseClickedPosition : textPane.getSelectionStart();
			boolean selection = textPane.getSelectionStart() != textPane.getSelectionEnd();

			MutableList<Action> exportActions = Lists.mutable.empty();
			MutableList<JMenuItem> mentionActions = Lists.mutable.empty();
			if (selection) {
				exportActions.add(new ExampleExport(DocumentWindow.this, ExampleExport.Format.MARKDOWN));
				exportActions.add(new ExampleExport(DocumentWindow.this, ExampleExport.Format.PLAINTEXT));
			}

			MutableSet<Annotation> localAnnotations = Sets.mutable
					.withAll(getDocumentModel().getCoreferenceModel().getMentions(offset));

			if (selection)
				for (int i = textPane.getSelectionStart(); i <= textPane.getSelectionEnd(); i++)
					localAnnotations.addAll(getDocumentModel().getCoreferenceModel().getMentions(i));

			MutableSet<Annotation> mentions = localAnnotations
					.select(m -> m instanceof Mention || m instanceof DetachedMentionPart);

			for (Annotation anno : mentions) {
				if (anno instanceof Mention)
					mentionActions.add(this.getMentionItem((Mention) anno, ((Mention) anno).getDiscontinuous()));
				else if (anno instanceof DetachedMentionPart)
					mentionActions
							.add(getMentionItem(((DetachedMentionPart) anno).getMention(), (DetachedMentionPart) anno));
			}

			Set<Entity> candidates = Sets.mutable.empty();
			if (selection) {
				for (EntityRankingPlugin erp : new EntityRankingPlugin[] {
						Annotator.app.getPluginManager().getEntityRankingPlugin(MatchingRanker.class),
						Annotator.app.getPluginManager().getEntityRankingPlugin(PreceedingRanker.class) }) {
					candidates.addAll(erp.rank(getSelection(), getCoreferenceModel(), getJCas()).take(5));
				}
				mentionActions.add(new JMenuItem(actions.deleteAllAction));
			}

			if (selection) {
				textPopupMenu.add(Annotator.getString(Strings.MENU_ENTITIES_CANDIDATES));
				textPopupMenu.getComponent(textPopupMenu.getComponentCount() - 1).setEnabled(false);
				textPopupMenu.add(actions.newEntityAction);
				if (!candidates.isEmpty()) {
					candidates.forEach(entity -> {
						JMenuItem mi = new JMenuItem(Util.toString(getCoreferenceModel().getLabel(entity)));
						mi.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								getDocumentModel().edit(new AddMentionsToEntity(entity, getSelection()));
							}
						});
						mi.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(entity.getColor())));
						textPopupMenu.add(mi);
					});
				}
			}
			if (!mentionActions.isEmpty()) {
				if (selection)
					textPopupMenu.addSeparator();
				textPopupMenu.add(Annotator.getString(Strings.MENU_ENTITIES));
				textPopupMenu.getComponent(textPopupMenu.getComponentCount() - 1).setEnabled(false);
				mentionActions.forEach(mi -> {
					textPopupMenu.add(mi);
				});
			}
			if (!exportActions.isEmpty()) {
				textPopupMenu.addSeparator();
				textPopupMenu.add(Annotator.getString(Strings.ACTION_EXPORT_EXAMPLE));
				textPopupMenu.getComponent(textPopupMenu.getComponentCount() - 1).setEnabled(false);
				exportActions.forEach(ea -> textPopupMenu.add(ea));
			}
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			((JPopupMenu) e.getSource()).removeAll();

		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {

		}

		protected JMenu getMentionItem(Mention m, DetachedMentionPart dmp) {
			StringBuilder b = new StringBuilder();
			b.append(m.getAddress());

			String surf = m.getCoveredText();
			surf = StringUtils.abbreviateMiddle(surf, "...", 20);

			if (dmp != null)
				surf += " [,] " + dmp.getCoveredText();
			if (m.getEntity().getLabel() != null)
				b.append(": ")
						.append(StringUtils.abbreviateMiddle(
								getDocumentModel().getCoreferenceModel().getLabel(m.getEntity()), "...",
								Constants.UI_MAX_STRING_WIDTH_IN_MENU));

			JMenu mentionMenu = new JMenu(b.toString());
			mentionMenu.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(m.getEntity().getColor())));
			Action a = new ShowMentionInTreeAction(DocumentWindow.this, m);
			mentionMenu.add('"' + surf + '"');
			mentionMenu.add(a);
			mentionMenu.add(new DeleteAction(DocumentWindow.this, m));

			return mentionMenu;
		}
	}

	public class MyTreeSelectionListener implements TreeSelectionListener {

		boolean enabled = true;

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (!isEnabled())
				return;
			TreeSelectionUtil tsu = new TreeSelectionUtil(e);

			actions.changeKeyAction.setEnabled(tsu.isSingle() && tsu.isEntity());
			actions.changeColorAction.setEnabled(tsu.isSingle() && tsu.isEntity());

			actions.mergeSelectedEntitiesAction.setEnabled(!tsu.isSingle() && tsu.isEntity());

			actions.removeDuplicatesAction.setEnabled(tsu.isEntity());

			actions.entityStatisticsAction.setEnabled(tsu.isEntity());

			if (tsu.isSingle() && (tsu.isMention() || tsu.isDetachedMentionPart()))
				annotationSelected(tsu.getAnnotation(0));
			else if (tsu.isSingle() && tsu.isEntity()) {
				highlightManager.unHighlight();
				getDocumentModel().getCoreferenceModel().getMentions(tsu.getEntity(0))
						.forEach(m -> highlightManager.highlight(m, new Color(255, 255, 150)));
			} else
				annotationSelected(null);
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	/**
	 * TODO: This should be indexed to make lookup faster
	 *
	 */
	class EntityFinder implements DocumentListener, KeyListener {

		Pattern pattern;

		public void filter(String s) {
			tree.scrollRowToVisible(0);
			getDocumentModel().getTreeModel().rankBySearchString(s);
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

	@Override
	public JTree getTree() {
		return tree;
	}

	public CoreferenceModel getCoreferenceModel() {
		return getDocumentModel().getCoreferenceModel();
	}

	@Override
	public StylePlugin getCurrentStyle() {
		return currentStyle;
	}

	@Override
	public String getText() {
		return textPane.getText();
	}

	@Override
	public Span getSelection() {
		return new Span(textPane.getSelectionStart(), textPane.getSelectionEnd());
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

	class ActionContainer {

		MergeAdjacentMentions mergeMentions = new MergeAdjacentMentions(DocumentWindow.this);
		ClearAction clearAction = new ClearAction(DocumentWindow.this);
		AbstractAction closeAction = new de.unistuttgart.ims.coref.annotator.action.CloseAction();
		ChangeColorForEntity changeColorAction;
		ChangeKeyForEntityAction changeKeyAction;
		AbstractAction copyAction;
		DeleteAction deleteAction;
		DeleteAllMentionsInSelection deleteAllAction = new DeleteAllMentionsInSelection(DocumentWindow.this);
		FileSaveAction fileSaveAction;
		AbstractAction toggleShowTextInTreeLabels;
		AbstractAction toggleTrimWhitespace;
		UndoAction undoAction;
		AbstractAction setDocumentLanguageAction = new SetLanguageAction(DocumentWindow.this);
		AbstractAction showSearchPanelAction;
		SortTree sortByAlpha;
		SortTree sortByMentions;
		ToggleEntitySortOrder sortDescending = new ToggleEntitySortOrder(DocumentWindow.this);
		FormEntityGroup formGroupAction = new FormEntityGroup(DocumentWindow.this);
		MergeSelectedEntities mergeSelectedEntitiesAction = new MergeSelectedEntities(DocumentWindow.this);
		NewEntityAction newEntityAction;
		RenameEntityAction renameAction;
		RemoveDuplicatesAction removeDuplicatesAction;
		EntityStatisticsAction entityStatisticsAction;
		ViewSetLineNumberStyle lineNumberStyleNone = new ViewSetLineNumberStyle(DocumentWindow.this,
				LineNumberStyle.NONE);
		ViewSetLineNumberStyle lineNumberStyleFixed = new ViewSetLineNumberStyle(DocumentWindow.this,
				LineNumberStyle.FIXED);
		ViewSetLineNumberStyle lineNumberStyleDynamic = new ViewSetLineNumberStyle(DocumentWindow.this,
				LineNumberStyle.DYNAMIC);

	}

	public ImmutableSet<Entity> getSelectedEntities() {
		Entity[] entities = new Entity[tree.getSelectionPaths().length];
		for (int i = 0; i < tree.getSelectionPaths().length; i++) {
			entities[i] = ((CATreeNode) tree.getSelectionPaths()[i].getLastPathComponent()).getEntity();
		}
		return Sets.immutable.of(entities);
	}

	public ImmutableSet<Mention> getSelectedMentions() {
		Mention[] entities = new Mention[tree.getSelectionPaths().length];
		for (int i = 0; i < tree.getSelectionPaths().length; i++) {
			entities[i] = ((CATreeNode) tree.getSelectionPaths()[i].getLastPathComponent()).getFeatureStructure();
		}
		return Sets.immutable.of(entities);
	}

	public MyTreeSelectionListener getTreeSelectionListener() {
		return treeSelectionListener;
	}

	public JTextField getTreeSearchField() {
		return treeSearchField;
	}

	class MyTreeModelListener implements SortingTreeModelListener, TreeModelListener, FlagModelListener {
		@Override
		public void treeNodesPreResort(TreeModelEvent e) {
			// store expansion state
			for (int i = 0; i < tree.getRowCount(); i++) {
				if (tree.isExpanded(i)) {
					TreePath tp = tree.getPathForRow(i);
					expanded.add(((CATreeNode) tp.getLastPathComponent()));
				}
			}
		}

		@Override
		public void treeNodesPostResort(TreeModelEvent e) {
			// store expansion state
			for (int i = 0; i < tree.getRowCount(); i++) {
				TreePath tp = tree.getPathForRow(i);
				CATreeNode node = (CATreeNode) tp.getLastPathComponent();
				if (expanded.contains(node)) {
					tree.expandPath(tp);
				}
			}
			expanded.clear();
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			tree.expandPath(e.getTreePath().getParentPath());
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {

		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			tree.expandPath(e.getTreePath());
		}

		@Override
		public void flagEvent(FeatureStructureEvent event) {
			tree.repaint();
		}
	}

	class DocumentWindowWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent ev) {
			if (getDocumentModel().isSavable()) {
				Object[] options = new String[] { Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE_DONT_SAVE),
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE_SAVE),
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE_CANCEL) };
				int r = JOptionPane.showOptionDialog(DocumentWindow.this,
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE),
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_TITLE), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, FontIcon.of(MaterialDesign.MDI_CONTENT_SAVE), options,
						options[1]);
				switch (r) {
				case 1:
					// first save, then close the window
					setIndeterminateProgress();
					SaveJCasWorker worker = new SaveJCasWorker(getFile(), getDocumentModel().getJcas(),
							(file, jcas) -> {
								getDocumentModel().getHistory().clear();
								setWindowTitle();
								stopIndeterminateProgress();
								closeWindow(false);
							});
					worker.execute();
					break;
				case 0:
					closeWindow(false);
					break;
				default:
					break;
				}
			} else
				closeWindow(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			setVisible(false);
			dispose();
		}

	}
}
