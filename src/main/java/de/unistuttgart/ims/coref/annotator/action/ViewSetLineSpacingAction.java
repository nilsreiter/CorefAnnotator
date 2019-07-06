package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.text.StyleConstants;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class ViewSetLineSpacingAction extends DocumentWindowAction {

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

}