package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.uima.cas.FeatureStructure;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;

public class AddFlagAction extends TargetedIkonAction<DocumentModel> {

	private static final long serialVersionUID = 1L;

	Class<? extends FeatureStructure> targetClass = Entity.class;

	public AddFlagAction(DocumentModel dw) {
		super(dw, Constants.Strings.ACTION_ADD_FLAG, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FLAG_TOOLTIP));
	}

	public AddFlagAction(DocumentModel dw, Class<? extends FeatureStructure> targetClass) {
		super(dw, Constants.Strings.ACTION_ADD_FLAG, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FLAG_TOOLTIP));
		this.targetClass = targetClass;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().edit(new AddFlag(targetClass));
	}

	public static AddFlagAction getAddEntityFlagAction(DocumentModel dw) {
		AddFlagAction afa = new AddFlagAction(dw, Entity.class);
		afa.putValue(Action.NAME, Annotator.getString(Constants.Strings.ACTION_ADD_ENTITY_FLAG));
		afa.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_ADD_ENTITY_FLAG_TOOLTIP));
		return afa;
	}

	public static AddFlagAction getAddMentionFlagAction(DocumentModel dw) {
		AddFlagAction afa = new AddFlagAction(dw, Mention.class);
		afa.putValue(Action.NAME, Annotator.getString(Constants.Strings.ACTION_ADD_MENTION_FLAG));
		afa.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_ADD_MENTION_FLAG_TOOLTIP));
		return afa;
	}
}
