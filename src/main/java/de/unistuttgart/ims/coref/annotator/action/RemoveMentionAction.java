package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMention;

public class RemoveMentionAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	Mention m;

	public RemoveMentionAction(DocumentWindow documentWindow, Mention m) {
		super(documentWindow, Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_TOOLTIP));
		this.m = m;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		getTarget().getDocumentModel().edit(new RemoveMention(m));
	}

}