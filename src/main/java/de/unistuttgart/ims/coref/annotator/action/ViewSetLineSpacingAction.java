package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.text.StyleConstants;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class ViewSetLineSpacingAction extends DocumentWindowAction implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	float spacing;

	public ViewSetLineSpacingAction(DocumentWindow documentWindow, float spacing) {
		super(documentWindow, String.valueOf(spacing), false, MaterialDesign.MDI_FORMAT_LINE_SPACING);

		this.spacing = spacing;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().updateStyle(StyleConstants.LineSpacing, spacing);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == StyleConstants.LineSpacing.toString()) {
			float newSpacing = (float) evt.getNewValue();
			if (newSpacing == this.spacing) {
				this.putValue(Action.SELECTED_KEY, true);
			} else
				this.putValue(Action.SELECTED_KEY, false);
		}

	}

}