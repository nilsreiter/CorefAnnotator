package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.document.Model;

public interface Operation2<M extends Model> extends Operation {
	public static final int STATE_OK = 0;

	String getUIKey();

	int isApplicable(M model);

	Object edit(M model);

	Object undo(M model);
}
