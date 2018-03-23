package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
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
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;
import de.unistuttgart.ims.coref.annotator.action.AnnotatorAction;
import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.action.DocumentWindowAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.action.SetAnnotatorNameAction;
import de.unistuttgart.ims.coref.annotator.action.ShowHistoryAction;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.action.ShowMentionInTreeAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
import de.unistuttgart.ims.coref.annotator.action.TogglePreferenceAction;
import de.unistuttgart.ims.coref.annotator.action.UndoAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontFamilySelectAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeDecreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeIncreaseAction;
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
import de.unistuttgart.ims.coref.annotator.document.Op.AddMentionsToNewEntity;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.worker.DocumentModelLoader;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class DocumentWindow extends JFrame
		implements CaretListener, TreeModelListener, CoreferenceModelListener, TextWindow, DocumentStateListener {

	private static final long serialVersionUID = 1L;

	JCas jcas;
	File file;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// storing and caching
	boolean unsavedChanges = false;
	Feature titleFeature;
	Map<Character, Entity> keyMap = Maps.mutable.empty();

	// actions
	AbstractAction commentAction = new CommentAction(null);
	AbstractAction newEntityAction;
	AbstractAction renameAction;
	AbstractAction changeKeyAction;
	AbstractAction changeColorAction;
	DeleteAction deleteAction;
	AbstractAction formGroupAction, mergeSelectedEntitiesAction = new MergeSelectedEntities();
	ToggleMentionDifficult toggleMentionDifficult;
	ToggleMentionAmbiguous toggleMentionAmbiguous;
	AbstractAction toggleEntityGeneric, toggleEntityDisplayed = new ToggleEntityVisible();
	AbstractAction sortByAlpha;
	AbstractAction sortByMentions, sortDescending = new ToggleEntitySortOrder();
	AbstractAction fileSaveAction, showSearchPanelAction;
	AbstractAction toggleTrimWhitespace, toggleShowTextInTreeLabels, closeAction = new CloseAction();
	AbstractAction toggleMentionNonNominal = new ToggleMentionNonNominal();
	AbstractAction setDocumentLanguageAction = new SetLanguageAction();
	AbstractAction clearAction = new ClearAction();
	AbstractAction copyAction, undoAction;

	// controller
	DocumentModel documentModel;
	HighlightManager highlightManager;

	// Window components
	JTree tree;
	JTextPane textPane;
	StyleContext styleContext = new StyleContext();
	JLabel selectionDetailPanel;
	JPanel statusBar;
	JProgressBar progressBar;
	JSplitPane splitPane;
	JLabel styleLabel, messageLabel;
	JTextField treeSearchField;
	TreeKeyListener treeKeyListener = new TreeKeyListener();

	// Sub windows
	CommentWindow commentsWindow;

	Thread messageVoider;

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
	float lineSpacing = 2f;
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
		// popup
		treePopupMenu = new JPopupMenu();
		// treePopupMenu.add(this.commentAction);
		treePopupMenu.add(this.deleteAction);
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionAmbiguous));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionDifficult));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionNonNominal));
		treePopupMenu.addSeparator();
		treePopupMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		treePopupMenu.add(this.newEntityAction);
		treePopupMenu.add(this.renameAction);
		treePopupMenu.add(this.changeColorAction);
		treePopupMenu.add(this.changeKeyAction);
		treePopupMenu.add(this.mergeSelectedEntitiesAction);
		treePopupMenu.add(this.formGroupAction);
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleEntityGeneric));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleEntityDisplayed));

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
		controls.add(newEntityAction);
		controls.add(renameAction);
		controls.add(changeKeyAction);
		controls.add(changeColorAction);
		controls.add((deleteAction));
		controls.add((formGroupAction));
		controls.add(mergeSelectedEntitiesAction);
		controls.add(showSearchPanelAction);
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

		messageLabel = new JLabel();
		messageLabel.setSize(new Dimension(1, 20));
		statusBar.add(messageLabel);

		JLabel versionLabel = new JLabel(
				Annotator.class.getPackage().getImplementationTitle() + " " + Version.get().toString());
		versionLabel.setPreferredSize(new Dimension(220, 20));
		statusBar.add(versionLabel);

		springs.putConstraint(SpringLayout.EAST, versionLabel, 10, SpringLayout.EAST, statusBar);
		springs.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.EAST, messageLabel);
		springs.putConstraint(SpringLayout.WEST, messageLabel, 10, SpringLayout.WEST, statusBar);
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
		textPane.addKeyListener(new TextViewKeyListener());
		textPane.setCaretPosition(0);
		textPane.addCaretListener(this);
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				copyAction);

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
		this.showSearchPanelAction = new ShowSearchPanelAction(mainApplication, this);
		this.copyAction = new CopyAction(this, mainApplication);
		this.undoAction = new UndoAction(this);

		// disable some at the beginning
		newEntityAction.setEnabled(false);
		renameAction.setEnabled(false);
		changeKeyAction.setEnabled(false);
		changeColorAction.setEnabled(false);
		deleteAction.setEnabled(false);
		formGroupAction.setEnabled(false);
		mergeSelectedEntitiesAction.setEnabled(false);
		toggleMentionDifficult.setEnabled(false);
		toggleMentionAmbiguous.setEnabled(false);
		toggleEntityGeneric.setEnabled(false);
		toggleEntityDisplayed.setEnabled(false);
		undoAction.setEnabled(false);
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

		PluginManager pm = mainApplication.getPluginManager();

		JMenu viewStyleMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_STYLE));
		grp = new ButtonGroup();
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
		viewMenu.add(new ViewShowCommentsAction());
		return viewMenu;

	}

	protected JMenu initialiseMenuSettings() {
		JMenu menu = new JMenu(Annotator.getString(Strings.MENU_SETTINGS));
		menu.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(mainApplication, Constants.SETTING_TRIM_WHITESPACE)));
		menu.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(mainApplication, Constants.SETTING_SHOW_TEXT_LABELS)));
		menu.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(mainApplication, Constants.SETTING_FULL_TOKENS)));
		menu.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(mainApplication, Constants.SETTING_KEEP_TREE_SORTED)));
		menu.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(mainApplication, Constants.SETTING_DELETE_EMPTY_ENTITIES)));
		menu.add(new SetAnnotatorNameAction(mainApplication));
		return menu;

	}

	protected JMenu initialiseMenuTools() {
		JMenu toolsMenu = new JMenu(Annotator.getString(Strings.MENU_TOOLS));
		toolsMenu.add(showSearchPanelAction);
		toolsMenu.add(setDocumentLanguageAction);
		toolsMenu.add(clearAction);
		toolsMenu.addSeparator();
		toolsMenu.add(new ShowHistoryAction(this));
		toolsMenu.add(new ShowLogWindowAction(mainApplication));
		return toolsMenu;
	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_IMPORT_FROM));
		JMenu fileExportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_EXPORT_AS));

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

		JMenu fileMenu = new JMenu(Annotator.getString(Strings.MENU_FILE));
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
		JMenu entityMenu = new JMenu(Annotator.getString(Strings.MENU_EDIT));
		entityMenu.add(new JMenuItem(undoAction));
		entityMenu.add(new JMenuItem(copyAction));
		entityMenu.add(new JMenuItem(deleteAction));
		entityMenu.add(new JMenuItem(commentAction));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_MENTIONS));
		entityMenu.add(new JCheckBoxMenuItem(toggleMentionAmbiguous));
		entityMenu.add(new JCheckBoxMenuItem(toggleMentionDifficult));
		entityMenu.add(new JCheckBoxMenuItem(toggleMentionNonNominal));
		entityMenu.addSeparator();
		entityMenu.add(Annotator.getString(Strings.MENU_EDIT_ENTITIES));
		entityMenu.add(new JMenuItem(newEntityAction));
		entityMenu.add(new JMenuItem(renameAction));
		entityMenu.add(new JMenuItem(changeColorAction));
		entityMenu.add(new JMenuItem(changeKeyAction));
		entityMenu.add(new JMenuItem(formGroupAction));
		entityMenu.add(new JCheckBoxMenuItem(toggleEntityGeneric));
		entityMenu.add(new JCheckBoxMenuItem(toggleEntityDisplayed));

		JMenu sortMenu = new JMenu(Annotator.getString(Strings.MENU_EDIT_ENTITIES_SORT));
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(this.sortByAlpha);
		JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(this.sortByMentions);
		radio2.setSelected(true);
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

		JMenu helpMenu = new JMenu(Annotator.getString(Strings.MENU_HELP));
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

		Annotator.logger.info("Initialised menus");
	}

	protected void closeWindow(boolean quit) {
		if (unsavedChanges) {
			Annotator.logger.warn("Closing window with unsaved changes");
		}
		if (searchPanel != null) {
			searchPanel.setVisible(false);
			searchPanel.dispose();
			searchPanel = null;
		}
		mainApplication.close(this);
	}

	public void loadFile(File file, IOPlugin flavor, String language) {
		if (flavor instanceof DefaultIOPlugin)
			this.file = file;
		else
			this.fileSaveAction.setEnabled(false);

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

	public synchronized void saveCurrentFile() {
		if (file != null)
			saveToFile(file, mainApplication.getPluginManager().getDefaultIOPlugin(), false);
	}

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
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

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
				file = f;
				if (plugin == mainApplication.getPluginManager().getDefaultIOPlugin()) {
					unsavedChanges = false;
					setWindowTitle();
				}
			}

		}.execute();
	}

	public JCas getJcas() {
		return jcas;
	}

	public Annotator getMainApplication() {
		return mainApplication;
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

	class ViewShowCommentsAction extends DocumentWindowAction {

		private static final long serialVersionUID = 1L;

		public ViewShowCommentsAction() {
			super(DocumentWindow.this, Constants.Strings.ACTION_SHOW_COMMENTS, MaterialDesign.MDI_MESSAGE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			commentsWindow.setVisible(true);

		}

	}

	class SetLanguageAction extends AnnotatorAction {
		private static final long serialVersionUID = 1L;

		public SetLanguageAction() {
			super(null, Constants.Strings.ACTION_SET_DOCUMENT_LANGUAGE, MaterialDesign.MDI_SWITCH);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String lang = (String) JOptionPane.showInputDialog(DocumentWindow.this,
					Annotator.getString(Strings.DIALOG_LANGUAGE_TITLE),
					Annotator.getString(Strings.DIALOG_LANGUAGE_PROMPT), JOptionPane.QUESTION_MESSAGE,
					FontIcon.of(MaterialDesign.MDI_SWITCH), Util.getSupportedLanguageNames(),
					Util.getLanguageName(jcas.getDocumentLanguage()));
			if (lang != null) {
				Annotator.logger.info("Setting document language to {}.", Util.getLanguage(lang));
				jcas.setDocumentLanguage(Util.getLanguage(lang));

			}
		}

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
			sortDescending.putValue(Action.SELECTED_KEY, false);
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
			sortDescending.putValue(Action.SELECTED_KEY, true);
		}

	}

	public void setIndeterminateProgress() {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void stopIndeterminateProgress() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	protected void setMessage(String message) {
		setMessage(message, false);
	}

	protected synchronized void setMessage(String message, boolean disappearing) {
		messageLabel.setText(message);
		messageLabel.repaint();
		statusBar.revalidate();

		if (messageVoider != null && messageVoider.isAlive())
			messageVoider.interrupt();

		if (disappearing) {
			messageVoider = new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						setMessage("");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			};
			SwingUtilities.invokeLater(messageVoider);
		}
	}

	public void setProgress(int i) {
		progressBar.setValue(i);
	}

	protected void setWindowTitle() {
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
				+ (unsavedChanges ? " -- " + Annotator.getString(Strings.WINDOWTITLE_EDITED) : ""));
	}

	public void showSearch() {
		if (searchPanel == null) {
			searchPanel = new SearchDialog(this, mainApplication.getPreferences());
		}
		searchPanel.setVisible(true);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		Iterator<FeatureStructure> iter = event.iterator(1);
		switch (eventType) {
		case Add:
			while (iter.hasNext()) {
				FeatureStructure fs = iter.next();
				if (fs instanceof Entity) {
					Entity entity = (Entity) fs;
					if (entity.getKey() != null) {
						keyMap.put(entity.getKey().charAt(0), entity);
					}
				} else if (fs instanceof Mention || fs instanceof DetachedMentionPart) {
					highlightManager.underline((Annotation) fs);
				} else if (fs instanceof CommentAnchor) {
					highlightManager.highlight((Annotation) fs);
				}
			}
			break;
		case Remove:
			while (iter.hasNext()) {
				FeatureStructure fs = iter.next();
				if (fs instanceof Entity) {
					if (((Entity) fs).getKey() != null)
						keyMap.remove(((Entity) fs).getKey().charAt(0));
				} else if (fs instanceof Mention) {
					if (((Mention) fs).getDiscontinuous() != null)
						highlightManager.undraw(((Mention) fs).getDiscontinuous());
					highlightManager.undraw((Annotation) fs);
				} else if (fs instanceof Annotation)
					highlightManager.undraw((Annotation) fs);

			}
			break;
		case Update:
			for (FeatureStructure fs : event) {
				if (fs instanceof Mention) {
					if (((Mention) fs).getEntity().getHidden())
						highlightManager.undraw((Annotation) fs);
					else
						highlightManager.underline((Annotation) fs);
				} else if (fs instanceof Entity) {
					if (((Entity) fs).getKey() != null)
						keyMap.put(((Entity) fs).getKey().charAt(0), (Entity) fs);
				}
			}
		}

	}

	public void setDocumentModel(DocumentModel model) {

		tree.setModel(model.getTreeModel());
		model.getTreeModel().addTreeModelListener(this);
		model.addDocumentStateListener(this);
		documentModel = model;

		// UI
		stopIndeterminateProgress();
		Annotator.logger.debug("Setting loading progress to {}", 100);
		splitPane.setVisible(true);

		// Style
		Meta meta = Util.getMeta(jcas);
		StylePlugin sPlugin = null;

		if (meta.getStylePlugin() != null) {
			Object o;
			try {
				Class<?> cl = Class.forName(meta.getStylePlugin());
				o = mainApplication.getPluginManager().getPlugin(cl);
				if (o instanceof StylePlugin)
					sPlugin = (StylePlugin) o;
			} catch (ClassNotFoundException e) {
				Annotator.logger.catching(e);
			}
		}
		if (sPlugin == null)
			sPlugin = mainApplication.getPluginManager().getDefaultStylePlugin();

		StyleManager.styleParagraph(textPane.getStyledDocument(), StyleManager.getDefaultParagraphStyle());
		switchStyle(sPlugin);

		// final
		setMessage("");

		commentsWindow = new CommentWindow(this, documentModel.getCommentsModel());

		Annotator.logger.info("Document model has been loaded.");
	}

	public void setJCas(JCas jcas) {

		this.jcas = jcas;
		Annotator.logger.info("JCas has been loaded.");
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

		titleFeature = jcas.getTypeSystem().getFeatureByFullName(
				mainApplication.getPreferences().get(Constants.CFG_WINDOWTITLE, Defaults.CFG_WINDOWTITLE));

		// highlightManager.clearAndDrawAllAnnotations(jcas);

		setWindowTitle();
		DocumentModelLoader im = new DocumentModelLoader(cm -> this.setDocumentModel(cm), jcas);
		im.setCoreferenceModelListener(this);
		im.execute();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		newEntityAction.setEnabled(
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
				progressBar.setValue(0);
				progressBar.setVisible(true);
				Annotator.logger.debug("Activating style {}", sv.getClass().getName());

				progressBar.setValue(20);

				Map<AttributeSet, org.apache.uima.cas.Type> styles = sv.getSpanStyles(jcas.getTypeSystem(),
						styleContext, baseStyle);
				StyleManager.styleCharacter(textPane.getStyledDocument(), baseStyle);
				if (styles != null)
					for (AttributeSet style : styles.keySet()) {
						StyleManager.style(jcas, textPane.getStyledDocument(), style, styles.get(style));
						progressBar.setValue(progressBar.getValue() + 10);
					}
				Util.getMeta(jcas).setStylePlugin(sv.getClass().getName());
				currentStyle = sv;
				styleMenuItem.get(sv).setSelected(true);
				styleLabel.setText(Annotator.getString(Strings.STATUS_STYLE) + ": " + sv.getName());
				styleLabel.setToolTipText(sv.getDescription());
				styleLabel.repaint();
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

	class ChangeColorForEntity extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ChangeColorForEntity() {
			super(null, Strings.ACTION_SET_COLOR, MaterialDesign.MDI_FORMAT_COLOR_FILL);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SET_COLOR_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			CATreeNode etn = (CATreeNode) tree.getLastSelectedPathComponent();
			Color color = new Color(etn.getEntity().getColor());

			Color newColor = JColorChooser.showDialog(DocumentWindow.this,
					Annotator.getString(Strings.DIALOG_CHANGE_COLOR_PROMPT), color);
			if (color != newColor) {
				documentModel.getCoreferenceModel().updateColor(etn.getFeatureStructure(), newColor);

			}

		}

	}

	class ChangeKeyForEntityAction extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ChangeKeyForEntityAction() {
			super(mainApplication, Strings.ACTION_SET_SHORTCUT, MaterialDesign.MDI_KEYBOARD);
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
					etn.getEntity().setKey(newKey.substring(0, 1));
					keyMap.put(newChar, etn.getFeatureStructure());

				} else {
					JOptionPane.showMessageDialog(DocumentWindow.this,
							Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_MESSAGE),
							Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_TITLE),
							JOptionPane.INFORMATION_MESSAGE);
				}
		}

	}

	class NewEntityAction extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public NewEntityAction() {
			super(null, Strings.ACTION_NEW, MaterialDesign.MDI_ACCOUNT_PLUS);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_NEW_TOOLTIP));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AddMentionsToNewEntity op = new AddMentionsToNewEntity(getSelection());
			documentModel.getCoreferenceModel().edit(op);
		}

	}

	class MyTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

		private static final long serialVersionUID = 1L;
		boolean showText = mainApplication.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);

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
			if (entity.getHidden() || treeNode.getRank() < 50) {
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
			} else if (documentModel.getCoreferenceModel() != null && treeNode == tree.getModel().getRoot())
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_PLUS));
			else if (documentModel.getCoreferenceModel() != null
					&& treeNode.getFeatureStructure() instanceof DetachedMentionPart)
				mainLabel.setIcon(FontIcon.of(MaterialDesign.MDI_TREE));

			return panel;
		}

	}

	class DeleteAction extends AnnotatorAction {
		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			super(null, Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_TOOLTIP));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

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

	class DeleteMentionAction extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		Mention m;

		public DeleteMentionAction(Mention m) {
			super(null, Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
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
			if (keyMap.containsKey(e.getKeyChar())) {
				e.consume();
				documentModel.getCoreferenceModel()
						.edit(new Op.AddMentionsToEntity(keyMap.get(e.getKeyChar()), getSelection()));
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

	class ClearAction extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ClearAction() {
			super(null, Constants.Strings.ACTION_CLEAR, MaterialDesign.MDI_FORMAT_CLEAR);
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
			if (unsavedChanges) {
				int r = JOptionPane.showConfirmDialog(DocumentWindow.this,
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_MESSAGE),
						Annotator.getString(Strings.DIALOG_UNSAVED_CHANGES_TITLE), JOptionPane.OK_CANCEL_OPTION);
				if (r == JOptionPane.OK_OPTION)
					closeWindow(false);
			} else
				closeWindow(false);
		}

	}

	class MergeSelectedEntities extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public MergeSelectedEntities() {
			super(mainApplication, Strings.ACTION_MERGE, MaterialDesign.MDI_CALL_MERGE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_MERGE_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode[] nodes = new CATreeNode[tree.getSelectionPaths().length];
			Entity[] entities = new Entity[tree.getSelectionPaths().length];
			for (int i = 0; i < tree.getSelectionPaths().length; i++) {
				nodes[i] = (CATreeNode) tree.getSelectionPaths()[i].getLastPathComponent();
				entities[i] = ((CATreeNode) tree.getSelectionPaths()[i].getLastPathComponent()).getEntity();
			}
			documentModel.getCoreferenceModel().edit(new Op.MergeEntities(entities));

		}

	}

	class FormEntityGroup extends AnnotatorAction {
		private static final long serialVersionUID = 1L;

		public FormEntityGroup() {
			super(mainApplication, Strings.ACTION_GROUP, MaterialDesign.MDI_ACCOUNT_MULTIPLE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_GROUP_TOOLTIP));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			Entity e1 = (Entity) ((CATreeNode) tree.getSelectionPaths()[0].getLastPathComponent())
					.getFeatureStructure();
			Entity e2 = (Entity) ((CATreeNode) tree.getSelectionPaths()[1].getLastPathComponent())
					.getFeatureStructure();
			documentModel.getCoreferenceModel().edit(new Op.GroupEntities(e1, e2));

		}

	}

	class FileExportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		IOPlugin plugin;

		public FileExportAction(IOPlugin plugin) {
			putValue(Action.NAME, plugin.getName());
			this.plugin = plugin;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser saveDialog = new JFileChooser(file.getParentFile());
			saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			saveDialog.setFileFilter(plugin.getFileFilter());
			saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_EXPORT_AS_TITLE));
			int r = saveDialog.showSaveDialog(DocumentWindow.this);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				saveToFile(f, plugin, true);
				break;
			default:
			}
		}

	}

	class FileSaveAsAction extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public FileSaveAsAction() {
			super(null, Strings.ACTION_SAVE_AS, MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
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
			saveDialog.setFileFilter(mainApplication.getPluginManager().getDefaultIOPlugin().getFileFilter());
			saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));
			int r = saveDialog.showSaveDialog(DocumentWindow.this);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				if (!f.getName().endsWith(".xmi")) {
					f = new File(f.getAbsolutePath() + ".xmi");
				}
				saveToFile(f, mainApplication.getPluginManager().getDefaultIOPlugin(), true);
				mainApplication.recentFiles.add(0, f);
				mainApplication.refreshRecents();
				break;
			default:
			}

		}
	}

	class ToggleEntitySortOrder extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ToggleEntitySortOrder() {
			super(null, Strings.ACTION_SORT_REVERT, MaterialDesign.MDI_SORT_VARIANT);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentModel.getTreeModel()
					.getEntitySortOrder().descending = !documentModel.getTreeModel().getEntitySortOrder().descending;
			documentModel.getTreeModel().resort();
		}
	}

	class ToggleEntityGeneric extends AnnotatorAction {
		private static final long serialVersionUID = 1L;

		public ToggleEntityGeneric() {
			super(null, Strings.ACTION_FLAG_ENTITY_GENERIC, MaterialDesign.MDI_CLOUD);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FLAG_ENTITY_GENERIC_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (TreePath tp : tree.getSelectionPaths()) {
				CATreeNode tn = (CATreeNode) tp.getLastPathComponent();
				Entity entity = (Entity) tn.getFeatureStructure();
				documentModel.getCoreferenceModel().toggleFlagEntity(entity, Constants.ENTITY_FLAG_GENERIC);
			}

		}
	}

	class ToggleEntityVisible extends AnnotatorAction {
		private static final long serialVersionUID = 1L;

		public ToggleEntityVisible() {
			super(null, Constants.Strings.ACTION_TOGGLE_ENTITY_VISIBILITY, MaterialDesign.MDI_ACCOUNT_OUTLINE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (tree.getSelectionCount() > 0) {
				for (TreePath tp : tree.getSelectionPaths()) {
					CATreeNode tn = (CATreeNode) tp.getLastPathComponent();
					Entity entity = (Entity) tn.getFeatureStructure();
					documentModel.getCoreferenceModel().toggleHidden(entity);
				}
			}
		}
	}

	class ToggleMentionDifficult extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionDifficult() {
			super(null, Strings.ACTION_FLAG_MENTION_DIFFICULT, MaterialDesign.MDI_ALERT_BOX);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FLAG_MENTION_DIFFICULT_TOOLTIP));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			documentModel.getCoreferenceModel().toggleFlagMention(m, Constants.MENTION_FLAG_DIFFICULT);

		}

	}

	class ToggleMentionNonNominal extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionNonNominal() {
			super(null, Strings.ACTION_FLAG_MENTION_NON_NOMINAL, MaterialDesign.MDI_FLAG);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FLAG_MENTION_NON_NOMINAL_TOOLTIP));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (TreePath tp : tree.getSelectionPaths()) {
				CATreeNode tn = (CATreeNode) tp.getLastPathComponent();
				Mention m = (Mention) tn.getFeatureStructure();
				documentModel.getCoreferenceModel().toggleFlagMention(m, Constants.MENTION_FLAG_NON_NOMINAL);
			}

		}

	}

	class ToggleMentionAmbiguous extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionAmbiguous() {
			super(MaterialDesign.MDI_SHARE_VARIANT);
			putValue(Action.NAME, Annotator.getString(Strings.ACTION_FLAG_MENTION_AMBIGUOUS));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			documentModel.getCoreferenceModel().toggleFlagMention(m, Constants.MENTION_FLAG_AMBIGUOUS);

		}

	}

	@Deprecated
	class ToggleShowTextInTreeLabels extends AnnotatorAction {

		private static final long serialVersionUID = 1L;

		public ToggleShowTextInTreeLabels() {
			super(null, Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS, MaterialDesign.MDI_FORMAT_TEXT);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS_TOOLTIP));
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
				MutableList<Annotation> localAnnotations = Lists.mutable
						.withAll(documentModel.getCoreferenceModel().getMentions(offset));
				if (localAnnotations.isEmpty())
					return;

				MutableList<Annotation> mentions = localAnnotations
						.select(m -> m instanceof Mention || m instanceof DetachedMentionPart);

				JMenu subMenu = new JMenu(Annotator.getString(Constants.Strings.MENU_ENTITIES));
				for (Annotation anno : mentions) {
					if (anno instanceof Mention)
						subMenu.add(this.getMentionItem((Mention) anno, ((Mention) anno).getDiscontinuous()));
					else if (anno instanceof DetachedMentionPart)
						subMenu.add(
								getMentionItem(((DetachedMentionPart) anno).getMention(), (DetachedMentionPart) anno));
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
				textPopupMenu.show(e.getComponent(), e.getX(), e.getY());

			}

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

	class MyTreeSelectionListener extends CATreeSelectionListener {

		public MyTreeSelectionListener(JTree tree) {
			super(tree);
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			collectData(e);
			renameAction.setEnabled(isSingle() && isEntity());
			changeKeyAction.setEnabled(isSingle() && isEntity());
			changeColorAction.setEnabled(isSingle() && isEntity());
			toggleEntityGeneric.setEnabled(isEntity());
			toggleEntityGeneric.putValue(Action.SELECTED_KEY,
					isEntity() && fs.allSatisfy(f -> Util.isGeneric((Entity) f)));
			deleteAction.setEnabled(isDetachedMentionPart() || isMention() || (isEntityGroup() && isLeaf())
					|| (isEntity() && isLeaf()));
			formGroupAction.setEnabled(isDouble() && isEntity());
			mergeSelectedEntitiesAction.setEnabled(!isSingle() && isEntity());

			toggleMentionDifficult.setEnabled(isMention());
			toggleMentionDifficult.putValue(Action.SELECTED_KEY,
					isSingle() && isMention() && Util.isDifficult(getMention(0)));

			toggleMentionAmbiguous.setEnabled(isMention());
			toggleMentionAmbiguous.putValue(Action.SELECTED_KEY,
					isSingle() && isMention() && Util.isAmbiguous(getMention(0)));

			toggleMentionNonNominal.setEnabled(isMention());
			toggleMentionNonNominal.putValue(Action.SELECTED_KEY,
					isSingle() && isMention() && Util.isNonNominal(getMention(0)));

			toggleEntityDisplayed.setEnabled(isEntity());
			toggleEntityDisplayed.putValue(Action.SELECTED_KEY,
					isEntity() && fs.allSatisfy(f -> ((Entity) f).getHidden()));

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
		undoAction.setEnabled(state.getHistorySize() > 0);
		fileSaveAction.setEnabled(state.getHistorySize() > 0);
		unsavedChanges = (state.getHistorySize() > 0);
		setWindowTitle();
	}

}
