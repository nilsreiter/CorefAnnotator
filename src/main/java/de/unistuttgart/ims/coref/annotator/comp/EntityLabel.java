package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;

import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Util;
import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import  de.unistuttgart.ims.coref.annotator.api.v2.EntityGroup;

public class EntityLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public EntityLabel(Entity entity) {
		Color entityColor = new Color(entity.getColor());

		boolean isGrey = Util.isX(entity, Constants.ENTITY_FLAG_HIDDEN);
		if (isGrey)
			this.setForeground(Color.GRAY);
		else
			this.setForeground(Color.BLACK);

		this.setText(StringUtils.abbreviate(entity.getLabel(), Constants.UI_MAX_STRING_WIDTH_IN_STATUSBAR));
		if (entity instanceof EntityGroup) {
			this.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT_MULTIPLE, (isGrey ? Color.GRAY : entityColor)));
		} else {
			this.setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, (isGrey ? Color.GRAY : entityColor)));
		}

	}

}
