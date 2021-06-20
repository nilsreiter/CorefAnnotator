package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Color;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.comp.CompoundIcon;

public abstract class IkonAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	MutableList<Ikon> ikon;
	Color enabledColor = Color.BLACK;
	Color disabledColor = Color.GRAY;
	static int smallIkonSize = 12;
	static int largeIkonSize = 16;

	public IkonAction(Ikon... icon) {
		this.ikon = Lists.mutable.of(icon);
		try {
			putValue(Action.LARGE_ICON_KEY, getLargeIcon(ikon.get(0)));
			putValue(Action.SMALL_ICON, getSmallIcon(ikon.get(0)));
		} catch (UnsupportedOperationException e) {
			Annotator.logger.catching(e);
		}
	}

	public IkonAction(String stringKey, Ikon... icon) {
		this.ikon = Lists.mutable.of(icon);
		putValue(Action.NAME, Annotator.getString(stringKey));
		try {
			if (icon != null) {
				putValue(Action.LARGE_ICON_KEY, getLargeIcon());
				putValue(Action.SMALL_ICON, getSmallIcon());
			}
		} catch (UnsupportedOperationException e) {
			Annotator.logger.catching(e);
		}
	}

	public IkonAction(String stringKey, boolean isKey, Ikon... icon) {
		this.ikon = Lists.mutable.of(icon);
		if (isKey)
			putValue(Action.NAME, Annotator.getString(stringKey));
		else
			putValue(Action.NAME, Annotator.getStringWithDefault(stringKey, stringKey));
		try {
			if (icon != null) {
				putValue(Action.LARGE_ICON_KEY, getLargeIcon());
				putValue(Action.SMALL_ICON, getSmallIcon());
			}
		} catch (UnsupportedOperationException e) {
			Annotator.logger.catching(e);
		}
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		try {
			putValue(Action.LARGE_ICON_KEY, getLargeIcon());
			putValue(Action.SMALL_ICON, getSmallIcon());
		} catch (UnsupportedOperationException e) {
			Annotator.logger.catching(e);
		}
	}

	public Ikon getIkon() {
		return ikon.get(0);
	}

	public void addIkon(Ikon ik) {
		ikon.add(ik);
		putValue(Action.LARGE_ICON_KEY, getLargeIcon());
		putValue(Action.SMALL_ICON, getSmallIcon());

	}

	protected Icon getSmallIcon(Ikon ik) {
		return FontIcon.of(ik, smallIkonSize, (isEnabled() ? enabledColor : disabledColor));
	}

	protected Icon getLargeIcon(Ikon ik) {
		return FontIcon.of(ik, largeIkonSize, (isEnabled() ? enabledColor : disabledColor));
	}

	public Icon getSmallIcon() {
		return new CompoundIcon(ikon.collect(ik -> getSmallIcon(ik)).toArray(new Icon[ikon.size()]));
	}

	public Icon getLargeIcon() {
		return new CompoundIcon(ikon.collect(ik -> getLargeIcon(ik)).toArray(new Icon[ikon.size()]));
	}
}
