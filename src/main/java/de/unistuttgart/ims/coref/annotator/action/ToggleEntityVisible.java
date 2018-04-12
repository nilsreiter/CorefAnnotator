package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleEntityVisible extends TargetedIkonAction<DocumentWindow> implements CAAction {
	private static final long serialVersionUID = 1L;

	public ToggleEntityVisible(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_TOGGLE_ENTITY_VISIBILITY, MaterialDesign.MDI_ACCOUNT_OUTLINE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCoreferenceModel()
				.edit(new Op.ToggleEntityFlag(Constants.ENTITY_FLAG_HIDDEN,
						Lists.immutable.of(getTarget().getTree().getSelectionPaths())
								.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

	@Override
	public void setEnabled(CATreeSelectionListener l) {
		boolean en = l.isEntity();
		setEnabled(en);
		putValue(Action.SELECTED_KEY, en && l.getFeatureStructures().allSatisfy(fs -> ((Entity) fs).getHidden()));
	}
}