package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;

import javax.swing.JLabel;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;

public class EntityLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public EntityLabel(Entity entity) {
		Color entityColor = new Color(entity.getColor());

		this.setText(entity.getLabel());
		if (entity instanceof EntityGroup) {
			this.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_MULTIPLE, entityColor));
		} else {
			this.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, entityColor));
		}

	}

}
