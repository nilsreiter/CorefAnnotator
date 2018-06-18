package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;
import de.unistuttgart.ims.coref.annotator.comp.ColorIcon;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class NewRelation extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public NewRelation(DocumentWindow dw) {
		super(dw, "action.new_relation", MaterialDesign.MDI_ACCOUNT_NETWORK);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox<Entity> entitySelector1 = new JComboBox<Entity>();
		JComboBox<Entity> entitySelector2 = new JComboBox<Entity>();
		JComboBox<EntityRelationType> relationSelector = new JComboBox<EntityRelationType>();
		entitySelector1.setRenderer(new EntityListCellRenderer());
		entitySelector2.setRenderer(new EntityListCellRenderer());
		relationSelector.setRenderer(new EntityRelationTypeListCellRenderer());
		for (Entity entity : getTarget().getDocumentModel().getCoreferenceModel()
				.getEntities(CoreferenceModel.EntitySorter.CHILDREN)) {
			entitySelector1.addItem(entity);
			entitySelector2.addItem(entity);
		}
		for (EntityRelationType ert : getTarget().getDocumentModel().getRelationModel().getRelationTypes()) {
			relationSelector.addItem(ert);
		}

		JDialog dialog = new JDialog();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(entitySelector1);
		panel.add(relationSelector);
		panel.add(entitySelector2);

		JButton okButton = new JButton("ok");
		dialog.getContentPane().add(panel, BorderLayout.CENTER);
		dialog.getContentPane().add(okButton, BorderLayout.SOUTH);
		dialog.setVisible(true);
		dialog.pack();

	}

	class EntityListCellRenderer extends JLabel implements ListCellRenderer<Entity> {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<? extends Entity> list, Entity value, int index,
				boolean isSelected, boolean cellHasFocus) {
			String s = "";
			if (value.getLabel() != null)
				s = StringUtils.abbreviateMiddle(value.getLabel(), "[...]", 30);
			setText(s);
			setIcon(new ColorIcon(15, 15, new Color(value.getColor())));
			return this;
		}
	}

	class EntityRelationTypeListCellRenderer extends JLabel implements ListCellRenderer<EntityRelationType> {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<? extends EntityRelationType> list,
				EntityRelationType value, int index, boolean isSelected, boolean cellHasFocus) {
			String s = "";
			if (value != null && value.getLabel() != null)
				s = StringUtils.abbreviateMiddle(value.getLabel(), "[...]", 30);
			setText(s);
			return this;
		}
	}

}
