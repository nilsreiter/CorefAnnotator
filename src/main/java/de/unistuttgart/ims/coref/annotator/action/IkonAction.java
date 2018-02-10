package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Color;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

public abstract class IkonAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	Ikon ikon;
	Color enabledColor = Color.BLACK;
	Color disabledColor = Color.GRAY;

	public IkonAction(Ikon icon) {
		this.ikon = icon;
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(ikon, enabledColor));
		putValue(Action.SMALL_ICON, FontIcon.of(ikon, enabledColor));

	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(ikon, (b ? enabledColor : disabledColor)));
		putValue(Action.SMALL_ICON, FontIcon.of(ikon, (b ? enabledColor : disabledColor)));

	}

	public Ikon getIkon() {
		return ikon;
	}

	public void setIkon(Ikon ikon) {
		this.ikon = ikon;
	}
}
