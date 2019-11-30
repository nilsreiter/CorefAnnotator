package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.text.StyleConstants;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;

public class ViewFontFamilySelectAction extends TargetedIkonAction<AbstractTextWindow> {

	private static final long serialVersionUID = 1L;

	String fontFamily;

	public ViewFontFamilySelectAction(AbstractTextWindow documentWindow, String family) {
		super(documentWindow, MaterialDesign.MDI_FORMAT_TEXT);
		putValue(Action.NAME, family);
		this.fontFamily = family;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		putValue(Action.SELECTED_KEY, true);
		getTarget().updateStyle(StyleConstants.FontFamily, fontFamily);
	}

}