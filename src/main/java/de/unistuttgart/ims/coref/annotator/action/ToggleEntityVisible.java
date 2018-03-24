package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleEntityVisible extends TargetedIkonAction<DocumentWindow> {
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
}