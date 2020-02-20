package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.HasDocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;

public abstract class TargetedOperationIkonAction<T extends HasDocumentModel> extends TargetedIkonAction<T>
		implements OperationAction {

	private static final long serialVersionUID = 1L;

	boolean actionEnabled = true;

	protected Class<? extends Operation> operationClass;

	public TargetedOperationIkonAction(T dw, Ikon ikon) {
		super(dw, ikon);
	}

	public TargetedOperationIkonAction(T dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(dw, stringKey, isKey, ikon);
	}

	public TargetedOperationIkonAction(T dw, String stringKey, Ikon... ikon) {
		super(dw, stringKey, ikon);
	}

	@Override
	public void setEnabled(boolean state) {
		if (getTarget().getDocumentModel() != null)
			actionEnabled = !getTarget().getDocumentModel().isBlocked(getOperationClass());
		super.setEnabled(actionEnabled && state);
	}

	@Override
	public Class<? extends Operation> getOperationClass() {
		return operationClass;
	}

}
