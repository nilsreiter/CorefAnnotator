package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow.LineNumberStyle;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;

public class ViewSetLineNumberStyle extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;
	LineNumberStyle style;

	public ViewSetLineNumberStyle(DocumentWindow dw, LineNumberStyle style) {
		super(dw, MaterialDesign.MDI_FORMAT_LIST_NUMBERS);
		putValue(Action.NAME, Annotator.getString(Strings.MENU_VIEW_LINE_NUMBERS + "." + style.name()));
		this.style = style;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().setLineNumberStyle(style);
	}

}
