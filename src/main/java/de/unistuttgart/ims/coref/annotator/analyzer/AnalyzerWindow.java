package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.AbstractWindow;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.HasDocumentModel;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.action.FileSelectAnalyzeAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.ProcessAction;
import de.unistuttgart.ims.coref.annotator.action.SelectedFileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.SetLanguageAction;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel.EntitySorter;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;

public class AnalyzerWindow extends AbstractWindow implements HasDocumentModel {

	public class ActionListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, Annotator.getString(Strings.ANALZYER_ACTIONS_ + value.toString()),
					index, isSelected, cellHasFocus);
			return this;
		}
	}

	public class ActionListModel extends AbstractListModel<ACTION> {

		private static final long serialVersionUID = 1L;

		@Override
		public ACTION getElementAt(int index) {
			return ACTION.values()[index];

		}

		@Override
		public int getSize() {
			return ACTION.values().length - 1;
		}

	}

	public class ActionListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				actionPanel = actionList.getSelectedValue().getObject(documentModel,
						entityList.getSelectedValuesList());
				innerSplitPane.add(actionPanel, JSplitPane.RIGHT);
				updateActionPanel();
			}
		}

	}

	public class EntityListModel extends AbstractListModel<Entity> implements ListModel<Entity> {

		private static final long serialVersionUID = 1L;
		EntitySorter sorter = EntitySorter.CHILDREN;

		@Override
		public Entity getElementAt(int index) {
			return documentModel.getCoreferenceModel().getEntities(sorter).get(index);
		}

		@Override
		public int getSize() {
			return documentModel.getCoreferenceModel().getEntities(sorter).size();
		}

	}

	public class EntityListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				actionPanel.setEntities(entityList.getSelectedValuesList());
				updateActionPanel();
			}
		}
	}

	class MyTreeCellRenderer extends DefaultListCellRenderer implements PreferenceChangeListener {

		private static final long serialVersionUID = 1L;
		boolean showText = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);

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

		public Icon getEntityIcon() {
			return FontIcon.of(MaterialDesign.MDI_ACCOUNT);
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			// this is the panel representing the node
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setOpaque(false);

			// this is the main label for the node
			JLabel mainLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			panel.add(mainLabel);

			return handleEntity(panel, mainLabel, (Entity) value);
		}

		public Icon getMentionIcon() {
			return FontIcon.of(MaterialDesign.MDI_COMMENT_ACCOUNT);
		}

		protected JPanel handleEntity(JPanel panel, JLabel lab1, Entity entity) {
			lab1.setText(entity.getLabel());

			boolean isGrey = Util.isX(entity, Constants.ENTITY_FLAG_HIDDEN);
			Color entityColor = new Color(entity.getColor());

			if (isGrey) {
				lab1.setForeground(Color.GRAY);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_OUTLINE, Color.GRAY));
			} else {
				lab1.setForeground(Color.BLACK);
				lab1.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, entityColor));
			}

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

		@Override
		public void preferenceChange(PreferenceChangeEvent evt) {
			showText = Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, true);
		}

	}

	private static final long serialVersionUID = 1L;

	JList<ACTION> actionList;
	AnalyzerActionPanel actionPanel;
	DocumentModel documentModel;

	JList<Entity> entityList;

	JSplitPane innerSplitPane;

	JSplitPane outerSplitPane;

	SelectedFileOpenAction openAnnotatorAction = new SelectedFileOpenAction(Annotator.app, null);

	public AnalyzerWindow() {
		init();
	}

	@Override
	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	protected void init() {

		initializeWindow();

		entityList = new JList<Entity>();
		entityList.setCellRenderer(new MyTreeCellRenderer());
		entityList.addListSelectionListener(new EntityListSelectionListener());
		entityList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		entityList.setPreferredSize(new Dimension(200, 600));

		actionList = new JList<ACTION>();
		actionList.setModel(new ActionListModel());
		actionList.addListSelectionListener(new ActionListSelectionListener());
		actionList.setCellRenderer(new ActionListCellRenderer());
		actionList.setPreferredSize(new Dimension(200, 600));

		actionPanel = new AnalyzerActionPanel_Dummy(documentModel, null);

		JPanel entityListPanel = new JPanel();
		entityListPanel.setLayout(new BorderLayout(5, 5));
		entityListPanel.add(new JScrollPane(entityList), BorderLayout.CENTER);
		entityListPanel.add(new JLabel(Annotator.getString(Strings.ANALZYER_ENTITIES)), BorderLayout.NORTH);
		entityListPanel.setPreferredSize(new Dimension(200, 600));

		JPanel actionListPanel = new JPanel();
		actionListPanel.setLayout(new BorderLayout(5, 5));
		actionListPanel.add(new JScrollPane(actionList), BorderLayout.CENTER);
		actionListPanel.add(new JLabel(Annotator.getString(Strings.ANALZYER_ACTIONS)), BorderLayout.NORTH);
		actionListPanel.setPreferredSize(new Dimension(200, 600));

		innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionListPanel, actionPanel);
		outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, entityListPanel, innerSplitPane);
		innerSplitPane.setBorder(BorderFactory.createEmptyBorder());
		add(outerSplitPane, BorderLayout.CENTER);

		// Menus
		JMenu menu = new JMenu(Annotator.getString(Strings.MENU_FILE));
		menu.add(new FileSelectAnalyzeAction());
		menu.add(new FileSelectOpenAction(Annotator.app));
		menu.add(openAnnotatorAction);
		menu.add(new de.unistuttgart.ims.coref.annotator.action.CloseAction());
		menuBar.add(menu);

		JMenu procMenu = new JMenu(Annotator.getString(Strings.MENU_TOOLS_PROC));
		for (Class<? extends ProcessingPlugin> pp : Annotator.app.getPluginManager().getProcessingPlugins()) {
			ProcessAction pa = new ProcessAction(this, Annotator.app.getPluginManager().getPlugin(pp));
			procMenu.add(pa);
		}

		menu = new JMenu(Annotator.getString(Strings.MENU_TOOLS));
		menu.add(procMenu);
		menu.add(new SetLanguageAction(this));
		menu.addSeparator();
		menu.add(new ShowLogWindowAction(Annotator.app));

		menuBar.add(menu);
		menuBar.add(initialiseMenuSettings());

		setJMenuBar(menuBar);

		// toolbar
		JToolBar controls = new JToolBar();
		controls.setFocusable(false);
		controls.setRollover(true);
		controls.add(openAnnotatorAction);
		add(controls, BorderLayout.NORTH);

		add(getStatusBar(), BorderLayout.SOUTH);

		pack();

	}

	protected void initContent() {
		entityList.setModel(new EntityListModel());

		openAnnotatorAction.setFile(documentModel.getFile());

		stopIndeterminateProgress();
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;

		initContent();
		setVisible(true);
		pack();

	}

	protected void updateActionPanel() {
		actionPanel.refresh();
	}
}
