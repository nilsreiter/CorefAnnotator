package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleMentionDifficult extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ToggleMentionDifficult(DocumentWindow dw) {
		super(dw, Strings.ACTION_FLAG_MENTION_DIFFICULT, MaterialDesign.MDI_ALERT_BOX);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FLAG_MENTION_DIFFICULT_TOOLTIP));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCoreferenceModel()
				.edit(new Op.ToggleMentionFlag(Constants.MENTION_FLAG_DIFFICULT,
						Lists.immutable.of(getTarget().getTree().getSelectionPaths())
								.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

}