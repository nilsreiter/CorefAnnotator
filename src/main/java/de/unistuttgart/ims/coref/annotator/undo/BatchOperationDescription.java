package de.unistuttgart.ims.coref.annotator.undo;

import java.util.Collection;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class BatchOperationDescription implements EditOperationDescription {

	ImmutableList<EditOperationDescription> edits;

	public BatchOperationDescription(EditOperationDescription... edits) {
		this.edits = Lists.immutable.of(edits);
	}

	public BatchOperationDescription(Collection<EditOperationDescription> edits) {
		this.edits = Lists.immutable.withAll(edits);
	}

	public ImmutableList<EditOperationDescription> getEdits() {
		return edits;
	}

}
