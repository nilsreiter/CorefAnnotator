package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.AbstractWindow;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateDocumentProperty;

public class SetLanguageAction extends TargetedIkonAction<AbstractWindow> {
	private static final long serialVersionUID = 1L;

	public SetLanguageAction(AbstractWindow dw) {
		super(dw, Strings.ACTION_SET_DOCUMENT_LANGUAGE, MaterialDesign.MDI_SWITCH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String lang = (String) JOptionPane.showInputDialog(getTarget(),
				Annotator.getString(Strings.DIALOG_LANGUAGE_TITLE), Annotator.getString(Strings.DIALOG_LANGUAGE_PROMPT),
				JOptionPane.QUESTION_MESSAGE, FontIcon.of(MaterialDesign.MDI_SWITCH), Util.getSupportedLanguageNames(),
				Util.getLanguageName(getTarget().getDocumentModel().getLanguage()));
		if (lang != null) {
			Annotator.logger.info("Setting document language to {}.", Util.getLanguage(lang));
			UpdateDocumentProperty udo = new UpdateDocumentProperty(UpdateDocumentProperty.DocumentProperty.LANGUAGE,
					Util.getLanguage(lang));
			getTarget().getDocumentModel().edit(udo);
		}
	}

}