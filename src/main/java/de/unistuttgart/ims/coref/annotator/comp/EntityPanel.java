package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import  de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public class EntityPanel extends JPanel implements PreferenceChangeListener, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	Boolean showText = null;

	Entity entity;
	DocumentModel documentModel;

	public EntityPanel(DocumentModel documentModel, Entity entity) {
		this.entity = entity;
		this.documentModel = documentModel;

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setOpaque(false);
		// this.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.setToolTipText(documentModel.getCoreferenceModel().getToolTipText(entity));

		initialize();

	}

	protected void initialize() {
		Annotator.logger.traceEntry();
		JLabel mainLabel = new EntityLabel(entity);

		add(mainLabel);
		if (entity.getFlags() != null && documentModel != null)
			for (String flagKey : entity.getFlags()) {
				if (flagKey == Constants.ENTITY_FLAG_HIDDEN)
					continue;
				Flag flag = documentModel.getFlagModel().getFlag(flagKey);
				addFlag(this, flag, Color.BLACK);
			}
	}

	protected void addFlag(JPanel panel, Flag flag, Color color) {
		JLabel l = new JLabel();
		if (color != null)
			l.setForeground(color);
		if (isShowText())
			l.setText(Annotator.getStringWithDefault(flag.getLabel(), flag.getLabel()));
		l.setIcon(FontIcon.of(MaterialDesign.valueOf(flag.getIcon()), color));
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		panel.add(l);
	}

	public Boolean getShowText() {
		return showText;
	}

	protected boolean isShowText() {
		if (showText == null)
			return Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TEXT_LABELS,
					Defaults.CFG_SHOW_TEXT_LABELS);
		return showText;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		removeAll();
		initialize();
		revalidate();
	}

	public void setShowText(Boolean showText) {
		this.showText = showText;
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Annotator.logger.traceEntry();
		if (event.getType() == Event.Type.Update) {
			if (event.getArguments().contains(entity)) {
				removeAll();
				initialize();
				revalidate();
			}
		}

	}

}
