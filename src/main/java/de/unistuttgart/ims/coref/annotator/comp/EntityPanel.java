package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class EntityPanel extends JPanel implements PreferenceChangeListener {

	private static final long serialVersionUID = 1L;

	public EntityPanel(DocumentModel documentModel, Entity entity) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setOpaque(false);

		JLabel mainLabel = new EntityLabel(entity);

		add(mainLabel);

		if (entity.getFlags() != null && documentModel != null)
			for (String flagKey : entity.getFlags()) {
				Flag flag = documentModel.getFlagModel().getFlag(flagKey);
				addFlag(this, flag, Color.BLACK);
			}

	}

	protected void addFlag(JPanel panel, Flag flag, Color color) {
		JLabel l = new JLabel();
		if (color != null)
			l.setForeground(color);
		if (Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS, Defaults.CFG_SHOW_TEXT_LABELS))
			l.setText(Annotator.getString(flag.getLabel(), flag.getLabel()));
		l.setIcon(FontIcon.of(MaterialDesign.valueOf(flag.getIcon()), color));
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		panel.add(l);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(Constants.CFG_SHOW_TEXT_LABELS))
			repaint();
	}

}
