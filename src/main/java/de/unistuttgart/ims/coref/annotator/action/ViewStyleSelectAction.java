package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AnnotationView;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class ViewStyleSelectAction extends TargetedIkonAction<AnnotationView> {

	private static final long serialVersionUID = 1L;

	StylePlugin styleVariant;

	public ViewStyleSelectAction(AnnotationView dw, StylePlugin style) {
		super(dw, MaterialDesign.MDI_FORMAT_TEXT);
		putValue(Action.NAME, style.getName());
		styleVariant = style;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().switchStyle(styleVariant);

	}

}