package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleMentionAmbiguous extends TargetedIkonAction<DocumentWindow> implements CAAction {

	private static final long serialVersionUID = 1L;

	public ToggleMentionAmbiguous(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_SHARE_VARIANT);
		putValue(Action.NAME, Annotator.getString(Strings.ACTION_FLAG_MENTION_AMBIGUOUS));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCoreferenceModel()
				.edit(new Op.ToggleMentionFlag(Constants.MENTION_FLAG_AMBIGUOUS,
						Lists.immutable.of(getTarget().getTree().getSelectionPaths())
								.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

	@Override
	public void setEnabled(CATreeSelectionListener l) {
		boolean en = l.isMention();
		setEnabled(en);
		putValue(Action.SELECTED_KEY, en && l.getFeatureStructures().allSatisfy(fs -> Util.isAmbiguous((Mention) fs)));
	}

}