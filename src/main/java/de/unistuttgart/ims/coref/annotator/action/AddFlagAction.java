package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.uima.cas.FeatureStructure;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;

public class AddFlagAction extends TargetedOperationIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	Class<? extends FeatureStructure> targetClass = Entity.class;

	@Deprecated
	public AddFlagAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_ADD_FLAG, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FLAG_TOOLTIP));
		this.operationClass = AddFlag.class;
	}

	public AddFlagAction(DocumentWindow dw, Class<? extends FeatureStructure> targetClass) {
		super(dw, Strings.ACTION_ADD_FLAG, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FLAG_TOOLTIP));
		this.targetClass = targetClass;
		this.operationClass = AddFlag.class;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().edit(new AddFlag(targetClass));
	}

	public static AddFlagAction getAddEntityFlagAction(DocumentWindow dw) {
		AddFlagAction afa = new AddFlagAction(dw, Entity.class);
		afa.putValue(Action.NAME, Annotator.getString(Strings.ACTION_ADD_ENTITY_FLAG));
		afa.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_ENTITY_FLAG_TOOLTIP));
		return afa;
	}

	public static AddFlagAction getAddMentionFlagAction(DocumentWindow dw) {
		AddFlagAction afa = new AddFlagAction(dw, Mention.class);
		afa.putValue(Action.NAME, Annotator.getString(Strings.ACTION_ADD_MENTION_FLAG));
		afa.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_MENTION_FLAG_TOOLTIP));
		return afa;
	}
}
