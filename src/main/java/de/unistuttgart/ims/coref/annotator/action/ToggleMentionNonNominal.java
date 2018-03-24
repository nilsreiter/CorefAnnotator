package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.AnnotationView;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleMentionNonNominal extends TargetedIkonAction<AnnotationView> {

	private static final long serialVersionUID = 1L;

	public ToggleMentionNonNominal(AnnotationView dw) {
		super(dw, Strings.ACTION_FLAG_MENTION_NON_NOMINAL, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FLAG_MENTION_NON_NOMINAL_TOOLTIP));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCoreferenceModel()
				.edit(new Op.ToggleMentionFlag(Constants.MENTION_FLAG_NON_NOMINAL,
						Lists.immutable.of(getTarget().getTree().getSelectionPaths())
								.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

}