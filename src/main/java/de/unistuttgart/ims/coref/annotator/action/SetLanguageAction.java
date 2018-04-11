package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Util;

public class SetLanguageAction extends DocumentWindowAction {
	private static final long serialVersionUID = 1L;

	public SetLanguageAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_SET_DOCUMENT_LANGUAGE, MaterialDesign.MDI_SWITCH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String lang = (String) JOptionPane.showInputDialog(getTarget(),
				Annotator.getString(Strings.DIALOG_LANGUAGE_TITLE), Annotator.getString(Strings.DIALOG_LANGUAGE_PROMPT),
				JOptionPane.QUESTION_MESSAGE, FontIcon.of(MaterialDesign.MDI_SWITCH), Util.getSupportedLanguageNames(),
				Util.getLanguageName(getTarget().getDocumentModel().getLanguage()));
		if (lang != null) {
			Annotator.logger.info("Setting document language to {}.", Util.getLanguage(lang));
			getTarget().getDocumentModel().setLanguage(Util.getLanguage(lang));
		}
	}

}