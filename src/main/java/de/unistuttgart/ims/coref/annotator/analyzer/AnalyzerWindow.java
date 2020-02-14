package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.AbstractWindow;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel.EntitySorter;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerWindow extends AbstractWindow {

	public class ActionListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			return this;
		}
	}

	public class ActionListModel extends AbstractListModel<AnalyzerActionPanel.ACTION> {

		private static final long serialVersionUID = 1L;

		@Override
		public AnalyzerActionPanel.ACTION getElementAt(int index) {
			switch (index) {

			default:
				return AnalyzerActionPanel.ACTION.MENTION;
			}
		}

		@Override
		public int getSize() {
			return 2;
		}

	}

	public class ActionListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
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

			String visLabel = StringUtils.abbreviate(entity.getLabel(), "…", Constants.UI_MAX_STRING_WIDTH_IN_TREE);

			if (entity.getKey() != null) {
				lab1.setText(visLabel + " [" + entity.getKey() + "]");
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

	JList<AnalyzerActionPanel.ACTION> actionList;
	AnalyzerActionPanel actionPanel;
	DocumentModel documentModel;

	JList<Entity> entityList;

	JSplitPane innerSplitPane;;

	JSplitPane outerSplitPane;;

	public AnalyzerWindow() {
		init();
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	protected void init() {
		entityList = new JList<Entity>();
		entityList.setCellRenderer(new MyTreeCellRenderer());
		entityList.addListSelectionListener(new EntityListSelectionListener());
		entityList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		actionList = new JList<AnalyzerActionPanel.ACTION>();
		actionList.setModel(new ActionListModel());
		actionList.addListSelectionListener(new ActionListSelectionListener());
		actionList.setCellRenderer(new ActionListCellRenderer());

		innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionList, new JLabel("placeholder"));
		add(innerSplitPane, BorderLayout.CENTER);

		innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionList, new JLabel("bla"));
		outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, entityList, innerSplitPane);

		add(outerSplitPane, BorderLayout.CENTER);
		pack();

	}

	protected void initContent() {
		entityList.setModel(new EntityListModel());
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;

		initContent();
		setVisible(true);
		pack();

	}

	protected void updateActionPanel() {
		if (!entityList.isSelectionEmpty() && !actionList.isSelectionEmpty())
			if (actionPanel == null || actionPanel.getType() != actionList.getSelectedValue()) {
				actionPanel = actionList.getSelectedValue().getObject(documentModel,
						entityList.getSelectedValuesList());
				innerSplitPane.add(actionPanel, JSplitPane.RIGHT);
			} else {
				actionPanel.setEntities(entityList.getSelectedValuesList());
			}
	}
}
