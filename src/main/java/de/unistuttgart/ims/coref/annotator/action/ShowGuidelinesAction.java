package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.profile.Profile;

public class ShowGuidelinesAction extends IkonAction {

	private static final long serialVersionUID = 1L;
	Profile profile;

	public ShowGuidelinesAction(Profile profile) {
		super(MaterialDesign.MDI_BOOK_OPEN_PAGE_VARIANT);
		StringBuilder b = new StringBuilder();
		String s = profile.getGuidelines().getTitle();
		if (s != null)
			b.append(s);
		else
			b.append(Annotator.getString(Strings.ACTION_GUIDELINES));

		String v = profile.getGuidelines().getVersion();
		if (v != null) {
			b.append(' ');
			b.append(v);
		}
		this.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_GUIDELINES_TOOLTIP));
		this.putValue(Action.NAME, b.toString());
		this.profile = profile;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (java.awt.Desktop.isDesktopSupported()) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
				java.net.URI uri;
				try {
					uri = new java.net.URI(profile.getGuidelines().getUrl());
					desktop.browse(uri);

				} catch (URISyntaxException | IOException e1) {
					Annotator.logger.catching(e1);
				}
			}
		}
	}

}
