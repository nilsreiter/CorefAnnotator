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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.Logger;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSaveAction;
import de.unistuttgart.ims.coref.annotator.action.ShowSearchPanelAction;
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
	private static final String HELP_MESSAGE = "Instructions for using Coref Annotator";

	JCas jcas;
	File file;
	static Logger logger = Annotator.logger;
	Annotator mainApplication;

	String segmentAnnotation = null;

	// storing and caching
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	RangedCounter spanCounter = new RangedCounter();

	// actions
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
	AbstractAction toggleTrimWhitespace;

	// controller
	CoreferenceModel cModel;

	// Window components
	JTree tree;
	JTextPane textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	StyleContext styleContext = new StyleContext();
	JLabel selectionDetailPanel;
	JPanel statusBar;
	JProgressBar progressBar;
	JSplitPane splitPane;

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
		treePopupMenu.add(this.renameAction);
		treePopupMenu.add(this.changeKeyAction);
		treePopupMenu.add(this.changeColorAction);
		treePopupMenu.add(this.deleteAction);
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionDifficult));
		treePopupMenu.add(new JCheckBoxMenuItem(this.toggleMentionAmbiguous));
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

		rightPanel.setPreferredSize(new Dimension(300, 800));
		rightPanel.add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// tool bar
		JToolBar controls = new JToolBar();
		controls.setFocusable(false);
		controls.add(new JButton(newEntityAction));
		controls.add(new JButton(renameAction));
		controls.add(new JButton(changeKeyAction));
		controls.add(new JButton(changeColorAction));
		controls.add(new JButton(deleteAction));
		controls.add(new JButton(formGroupAction));
		controls.add(new JButton(toggleMentionDifficult));
		getContentPane().add(controls, BorderLayout.NORTH);

		for (Component comp : controls.getComponents())
			comp.setFocusable(false);

		// status bar
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);

		statusBar = new JPanel();
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		statusBar.add(new JLabel(Annotator.class.getPackage().getImplementationTitle() + " "
				+ Annotator.class.getPackage().getImplementationVersion()));
		statusBar.add(progressBar);
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		// initialise text view
		JPanel leftPanel = new JPanel(new BorderLayout());
		hilit = new DefaultHighlighter();
		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setTransferHandler(new TextViewTransferHandler());
		textPane.setHighlighter(hilit);
		textPane.addMouseListener(new TextMouseListener());
		textPane.setCaret(new Caret());

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
		this.toggleTrimWhitespace = new ToggleTrimWhitespace();

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

		for (Class<? extends IOPlugin> pluginClass : mainApplication.getPluginManager().getIOPlugins()) {
			try {
				IOPlugin plugin = pluginClass.newInstance();
				if (plugin.getImporter() != null)
					fileImportMenu.add(new FileImportAction(mainApplication, plugin));
				if (plugin.getExporter() != null)
					fileExportMenu.add(new FileExportAction(plugin));
			} catch (InstantiationException | IllegalAccessException | ResourceInitializationException e) {
				logger.catching(e);
			}

		}

		// fileImportMenu.add(new FileImportQuaDramAAction(mainApplication));
		// fileImportMenu.add(new FileImportDKproAction(mainApplication));
		// fileImportMenu.add(new FileImportCRETAAction(mainApplication));

		JMenu fileMenu = new JMenu(Annotator.getString("menu.file"));
		fileMenu.add(new FileOpenAction(mainApplication));
		fileMenu.add(fileImportMenu);
		fileMenu.add(fileSaveAction);
		fileMenu.add(new FileSaveAsAction());
		fileMenu.add(fileExportMenu);
		fileMenu.add(new JMenuItem(new CloseAction()));
		fileMenu.add(new JMenuItem(new ExitAction()));

		return fileMenu;
	}

	protected JMenu initialiseMenuEntity() {
		JMenu entityMenu = new JMenu(Annotator.getString("menu.entities"));
		entityMenu.add(new JMenuItem(renameAction));
		entityMenu.add(new JMenuItem(newEntityAction));
		entityMenu.add(new JMenuItem(changeColorAction));
		entityMenu.add(new JMenuItem(changeKeyAction));
		entityMenu.add(new JMenuItem(formGroupAction));
		entityMenu.add(new JCheckBoxMenuItem(toggleEntityGeneric));

		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(this.sortByAlpha);
		radio1.setSelected(true);
		JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(this.sortByMentions);
		ButtonGroup grp = new ButtonGroup();
		grp.add(radio2);
		grp.add(radio1);

		entityMenu.addSeparator();

		entityMenu.add(radio1);
		entityMenu.add(radio2);

		entityMenu.add(new JCheckBoxMenuItem(this.sortDescending));

		return entityMenu;
	}

	protected void initialiseMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("Could not set look and feel {}.", e.getMessage());
		}

		// TODO: Disabled for the moment
		// JMenu helpMenu = new JMenu(Annotator.getString("menu.help"));
		// JMenu debugMenu = new JMenu("Debug");

		// windowsMenu = new JMenu(Annotator.getString("menu.windows"));
		// if (segmentAnnotation != null) {
		// documentMenu = new JMenu(Annotator.getString("menu.document"));
		// documentMenu.setEnabled(segmentAnnotation != null);
		// }

		// Menu Items
		JMenuItem aboutMenuItem = new JMenuItem(Annotator.getString("menu.file.about"));
		JMenuItem helpMenuItem = new JMenuItem(Annotator.getString("menu.help.help"));
		recentMenu = new JMenu(Annotator.getString("menu.file.open_recent"));

		menuBar.add(initialiseMenuFile());
		menuBar.add(initialiseMenuEntity());
		menuBar.add(initialiseMenuView());
		menuBar.add(initialiseMenuTools());
		menuBar.add(initialiseMenuSettings());
		// if (segmentAnnotation != null)
		// menuBar.add(documentMenu);
		// menuBar.add(windowsMenu);
		// menuBar.add(helpMenu);

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

		logger.info("Initialised menu bar.");
	}

	protected void closeWindow(boolean quit) {
		mainApplication.close(this);
	}

	public void undrawAnnotation(Annotation a) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (hi != null)
			hilit.removeHighlight(hi);
		if (span != null)
			spanCounter.subtract(span);
	}

	public void drawAnnotation(Annotation a, Color c, boolean dotted, boolean repaint) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (hi != null) {
			hilit.removeHighlight(hi);
			spanCounter.subtract(span);
		}
		try {
			int n = spanCounter.getMax(span);
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(), new UnderlinePainter(c, n * 3, dotted));
			spanCounter.add(span);
			highlightMap.put(a, hi);
			// TODO: this is overkill, but didn't work otherwise
			if (repaint)
				textPane.repaint();

		} catch (BadLocationException e) {
			logger.catching(e);
		}
	}

	public void drawMention(Mention m) {
		drawAnnotation(m, new Color(m.getEntity().getColor()), false, true);
		if (m.getDiscontinuous() != null)
			drawAnnotation(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, true);

	}

	public void drawMention(Mention m, boolean repaint) {
		drawAnnotation(m, new Color(m.getEntity().getColor()), false, false);
		if (m.getDiscontinuous() != null)
			drawAnnotation(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false);
	}

	public void drawAnnotations() {
		hilit.removeAllHighlights();
		highlightMap.clear();
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			drawAnnotation(m, new Color(m.getEntity().getColor()), false, false);
			if (m.getDiscontinuous() != null)
				drawAnnotation(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false);

		}
		textPane.repaint();
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
			logger.catching(e);
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
				progressBar.setValue(1000);
				progressBar.setVisible(false);
			}

		}.execute();
	}

	protected void loadStream(InputStream inputStream, TypeSystemDescription typeSystemDescription, String windowTitle,
			IOPlugin flavor) {

		LoadAndImport lai = new LoadAndImport(this, inputStream, typeSystemDescription, flavor);
		lai.execute();

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
			putValue(Action.NAME, Annotator.getString("action.view.decrease_font_size"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.EXPOSURE_NEG_1));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.EXPOSURE_NEG_1));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Font oldFont = textPane.getFont();
			float oldSize = oldFont.getSize();

			textPane.setFont(oldFont.deriveFont(oldSize - 1f));
		}

	}

	class ViewFontSizeIncreaseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ViewFontSizeIncreaseAction() {
			super();
			putValue(Action.NAME, Annotator.getString("action.view.increase_font_size"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.EXPOSURE_PLUS_1));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.EXPOSURE_PLUS_1));
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

	class SortTreeByAlpha extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByAlpha() {
			putValue(Action.NAME, Annotator.getString("action.sort_alpha"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.SORT_BY_ALPHA));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.SORT_BY_ALPHA));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder = EntitySortOrder.Alphabet;
			cModel.resort();
		}

	}

	class SortTreeByMentions extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SortTreeByMentions() {
			putValue(Action.NAME, Annotator.getString("action.sort_mentions"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder = EntitySortOrder.Mentions;
			cModel.entitySortOrder.descending = true;
			cModel.resort();
		}

	}

	protected void fireModelCreatedEvent() {
		cModel.addTreeModelListener(this);
		tree.setModel(cModel);
		textPane.addKeyListener(cModel);
		textPane.setCaretPosition(0);
		textPane.addCaretListener(this);
		progressBar.setValue(100);
		logger.debug("Setting loading progress to {}", 100);
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
				logger.catching(e);
			}

		} else // if (flavor.getStylePlugin() != null)
			switchStyle(mainApplication.getPluginManager().getDefaultStylePlugin());

		try {
			Feature titleFeature = jcas.getTypeSystem()
					.getFeatureByFullName(mainApplication.getConfiguration().getString("General.windowTitleFeature"));
			String fileName = (file != null ? file.getName() : Annotator.getString("windowtitle.new_file"));
			if (titleFeature != null)
				try {
					setTitle(jcas.getDocumentAnnotationFs().getFeatureValueAsString(titleFeature)
							+ (fileName != null ? " (" + fileName + ")" : ""));
				} catch (Exception e) {
					setTitle((fileName != null ? " (" + fileName + ")" : ""));
				}
			else
				setTitle((fileName != null ? " (" + fileName + ")" : ""));
		} catch (CASRuntimeException e) {
			logger.catching(e);
		}

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
		} catch (NullPointerException e) {
			logger.catching(e);
		}
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		if (e.getTreePath().getLastPathComponent() instanceof EntityGroup)
			tree.expandPath(e.getTreePath());
		try {
			tree.repaint(tree.getPathBounds(e.getTreePath()));
		} catch (NullPointerException ex) {
			logger.catching(ex);
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
	public void mentionAdded(Mention m) {
		drawMention(m);

		// textPane.setCaretPosition(m.getBegin());

	}

	@Override
	public void mentionChanged(Mention m) {
		drawMention(m);
	}

	@Override
	public void mentionRemoved(Mention m) {
		if (m.getDiscontinuous() != null) {
			spanCounter.subtract(new Span(m.getDiscontinuous()));
			hilit.removeHighlight(highlightMap.get(m.getDiscontinuous()));
		}
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
						cModel.addNewEntityMention(pa.getBegin(), pa.getEnd());
					else if (targetFs instanceof Entity)
						cModel.addNewMention((Entity) targetFs, pa.getBegin(), pa.getEnd());
					else if (targetFs instanceof Mention)
						cModel.addDiscontinuousToMention((Mention) targetFs, pa.getBegin(), pa.getEnd());
				} else if (dataFlavor == NodeTransferable.dataFlavor) {
					CATreeNode object = (CATreeNode) info.getTransferable()
							.getTransferData(NodeTransferable.dataFlavor);
					FeatureStructure droppedFs = object.getFeatureStructure();
					if (targetFs instanceof EntityGroup && droppedFs instanceof Entity) {
						cModel.addToGroup((EntityGroup) targetFs, (Entity) droppedFs);
					} else if (targetFs instanceof Entity && droppedFs instanceof Mention) {
						cModel.updateMention((Mention) droppedFs, (Entity) targetFs);
					} else if (targetFs instanceof Mention && droppedFs instanceof DetachedMentionPart) {
						DetachedMentionPart dmp = cModel.removeDiscontinuousMentionPart(
								(Mention) ((CATreeNode) object.getParent()).getFeatureStructure());
						cModel.addDiscontinuousToMention((Mention) targetFs, dmp);
					}
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

	class RenameEntityAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RenameEntityAction() {
			putValue(Action.NAME, Annotator.getString("action.rename"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.rename.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.EDIT));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.EDIT));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String name = JOptionPane.showInputDialog(Annotator.getString("dialog.rename_entity.prompt"));
			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			etn.getFeatureStructure().setLabel(name);
			cModel.nodeChanged(etn);
		}

	}

	class ChangeColorForEntity extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChangeColorForEntity() {
			putValue(Action.NAME, Annotator.getString("action.set_color"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.set_color.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.COLOR_LENS));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.COLOR_LENS));

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
			Color color = new Color(etn.getFeatureStructure().getColor());

			Color newColor = JColorChooser.showDialog(DocumentWindow.this,
					Annotator.getString("dialog.change_color.prompt"), color);
			cModel.updateColor(etn.getFeatureStructure(), newColor);
		}

	}

	class ChangeKeyForEntityAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChangeKeyForEntityAction() {
			putValue(Action.NAME, Annotator.getString("action.set_shortcut"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.set_shortcut.tooltip"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.KEYBOARD));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.KEYBOARD));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String key = JOptionPane.showInputDialog(Annotator.getString("dialog.change_key.prompt"));
			EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();

			char keyCode = key.charAt(0);
			System.err.println(KeyEvent.getKeyText(keyCode) + " " + keyCode);
			cModel.reassignKey(keyCode, etn.getFeatureStructure());
		}

	}

	class NewEntityAction extends MyAction {

		private static final long serialVersionUID = 1L;

		public NewEntityAction() {
			putValue(Action.NAME, Annotator.getString("action.new"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.new.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.PERSON_ADD));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.PERSON_ADD));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.addNewEntityMention(textPane.getSelectionStart(), textPane.getSelectionEnd());
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
				if (e instanceof EntityGroup)
					lab1.setIcon(FontIcon.of(Material.GROUP, new Color(e.getColor())));
				else
					lab1.setIcon(FontIcon.of(Material.PERSON, new Color(e.getColor())));
				if (etn.getKeyCode() != null) {
					lab1.setText(etn.getKeyCode() + ": " + e.getLabel() + " (" + etn.getChildCount() + ")");
				} else if (!(etn.getParent() instanceof EntityTreeNode))
					lab1.setText(e.getLabel() + " (" + etn.getChildCount() + ")");
				if (Util.contains(e.getFlags(), Constants.ENTITY_FLAG_GENERIC)) {
					JLabel l = new JLabel(Annotator.getString("entity.flag.generic"));
					l.setIcon(FontIcon.of(Material.CLOUD));
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(l);
				}
			} else if (catn != null && catn.getFeatureStructure() instanceof Mention) {
				Mention m = (Mention) catn.getFeatureStructure();
				if (Util.isDifficult(m)) {
					JLabel l = new JLabel(Annotator.getString("mention.flag.difficult"));
					l.setIcon(FontIcon.of(Material.WARNING));
					panel.add(Box.createRigidArea(new Dimension(5, 5)));
					panel.add(l);
				}
				if (Util.isAmbiguous(m)) {
					JLabel l = new JLabel(Annotator.getString("mention.flag.ambiguous"));
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

	class DeleteAction extends MyAction {
		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			putValue(Action.NAME, Annotator.getString("action.delete"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.delete.tooltip"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.DELETE));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.DELETE));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getLastSelectedPathComponent();
			if (tn.getFeatureStructure() instanceof Mention)
				cModel.removeMention((Mention) tn.getFeatureStructure());
			else if (tn.getFeatureStructure() instanceof EntityGroup) {
				cModel.removeEntityGroup((EntityGroup) tn.getFeatureStructure());
			} else if (tn.getFeatureStructure() instanceof DetachedMentionPart) {
				DetachedMentionPart dmp = (DetachedMentionPart) tn.getFeatureStructure();
				Mention m = (Mention) ((CATreeNode) tn.getParent()).getFeatureStructure();
				undrawAnnotation(dmp);
				cModel.removeDetachedMentionPart(m, dmp);
			} else if (tn.getFeatureStructure() instanceof Entity) {
				EntityTreeNode etn = (EntityTreeNode) tn;
				FeatureStructure parentFs = ((CATreeNode) etn.getParent()).getFeatureStructure();
				if (parentFs instanceof EntityGroup) {
					cModel.removeEntityFromGroup((EntityGroup) parentFs, (EntityTreeNode) tn);
				} else if (tn.isLeaf()) {
					cModel.removeEntity(etn.getFeatureStructure());
				}
			}
		}

	}

	class DeleteMentionAction extends MyAction {
		private static final long serialVersionUID = 1L;

		Mention m;

		public DeleteMentionAction(Mention m) {
			putValue(Action.NAME, Annotator.getString("action.delete"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.delete.tooltip"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.DELETE));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.DELETE));
			this.m = m;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.removeMention(m);
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
			closeWindow(false);
		}

	}

	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExitAction() {
			putValue(Action.NAME, Annotator.getString("action.quit"));
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
			putValue(Action.NAME, Annotator.getString("action.group"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.group.tooltip"));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.GROUP));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.GROUP));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Entity e1 = (Entity) ((CATreeNode) tree.getSelectionPaths()[0].getLastPathComponent())
					.getFeatureStructure();
			Entity e2 = (Entity) ((CATreeNode) tree.getSelectionPaths()[1].getLastPathComponent())
					.getFeatureStructure();
			cModel.formGroup(e1, e2);
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

	class FileSaveAsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public FileSaveAsAction() {
			putValue(Action.NAME, Annotator.getString("action.save_as"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.SAVE));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.SAVE));

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
				saveToFile(f, mainApplication.getPluginManager().getDefaultIOPlugin());
				break;
			default:
			}

		}
	}

	class ToggleEntitySortOrder extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ToggleEntitySortOrder() {
			putValue(Action.NAME, Annotator.getString("action.sort_revert"));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cModel.entitySortOrder.descending = !cModel.entitySortOrder.descending;
			cModel.resort();
		}
	}

	class ToggleEntityGeneric extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ToggleEntityGeneric() {
			putValue(Action.NAME, Annotator.getString("action.flag_entity_generic"));
			putValue(Action.LARGE_ICON_KEY, getIcon());
			putValue(Action.SMALL_ICON, getIcon());
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.flag_entity_generic.tooltip"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Entity entity = (Entity) tn.getFeatureStructure();
			cModel.toggleFlagEntity(entity, Constants.ENTITY_FLAG_GENERIC);
		}

		public Icon getIcon() {
			return FontIcon.of(Material.CLOUD);
		}

	}

	class ToggleMentionDifficult extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionDifficult() {
			putValue(Action.NAME, Annotator.getString("action.flag_mention_difficult"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.WARNING));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.WARNING));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.flag_mention_difficult.tooltip"));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			cModel.toggleFlagMention(m, Constants.MENTION_FLAG_DIFFICULT);
		}

	}

	class ToggleMentionAmbiguous extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ToggleMentionAmbiguous() {
			putValue(Action.NAME, Annotator.getString("action.flag_mention_ambiguous"));
			putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.SHARE));
			putValue(Action.SMALL_ICON, FontIcon.of(Material.SHARE));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode tn = (CATreeNode) tree.getSelectionPath().getLastPathComponent();
			Mention m = (Mention) tn.getFeatureStructure();
			cModel.toggleFlagMention(m, Constants.MENTION_FLAG_AMBIGUOUS);

		}

	}

	class ToggleTrimWhitespace extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ToggleTrimWhitespace() {
			putValue(Action.NAME, Annotator.getString("action.toggle.trim_whitespace"));
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.toggle.trim_whitespace.tooltip"));
			putValue(Action.SELECTED_KEY,
					mainApplication.getConfiguration().getBoolean(Constants.CFG_TRIM_WHITESPACE, true));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean old = mainApplication.getConfiguration().getBoolean(Constants.CFG_TRIM_WHITESPACE, true);
			mainApplication.getConfiguration().setProperty(Constants.CFG_TRIM_WHITESPACE, !old);
			putValue(Action.SELECTED_KEY, !old);
		}

	}

	abstract class MyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void setEnabled() {
		};
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
				logger.debug("Right-clicked in text at " + e.getPoint());
				int offset = textPane.viewToModel(e.getPoint());
				Collection<Annotation> mentions = cModel.getMentions(offset);
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
					Action a = new ShowMentionInTreeAction(m);
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

	class ShowMentionInTreeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		Mention m;

		public ShowMentionInTreeAction(Mention m) {
			putValue(Action.NAME, Annotator.getString("action.show_mention_in_tree"));
			this.m = m;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CATreeNode node = cModel.mentionMap.get(m);
			Object[] path = cModel.getPathToRoot(node);
			TreePath tp = new TreePath(path);
			tree.setSelectionPath(tp);
			tree.scrollPathToVisible(tp);

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
					isSingle() && isMention() && Util.isDifficult(getMention(0)));

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
			cModel = new CoreferenceModel(jcas, mainApplication.getConfiguration());
			cModel.addCoreferenceModelListener(DocumentWindow.this);

			for (Entity e : JCasUtil.select(jcas, Entity.class)) {
				cModel.addExistingEntity(e);
			}
			publish(60);
			for (EntityGroup eg : JCasUtil.select(jcas, EntityGroup.class)) {
				for (int i = 0; i < eg.getMembers().size(); i++) {
					cModel.insertNodeInto(new EntityTreeNode(eg.getMembers(i)), cModel.entityMap.get(eg), 0);
				}

			}
			publish(70);
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				cModel.connect(m.getEntity(), m);

				drawMention(m, false);
				cModel.characterPosition2AnnotationMap.add(m);
			}
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
				logger.catching(e);
			}
		}

		@Override
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				progressBar.setValue(i);
			}
		}

	}
}
