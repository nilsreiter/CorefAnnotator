package de.unistuttgart.ims.coref.annotator.action;

import de.unistuttgart.ims.coref.annotator.document.op.Operation;

public interface OperationAction {
	Class<? extends Operation> getOperationClass();

}
