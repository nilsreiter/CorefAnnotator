package de.unistuttgart.ims.coref.annotator.undo;

import java.util.Collection;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

public class BatchOperationDescription implements EditOperationDescription {

	ImmutableSet<EditOperationDescription> edits;

	public BatchOperationDescription(EditOperationDescription... edits) {
		this.edits = Sets.immutable.of(edits);
	}

	public BatchOperationDescription(Collection<EditOperationDescription> edits) {
		this.edits = Sets.immutable.withAll(edits);
	}

	public ImmutableSet<EditOperationDescription> getEdits() {
		return edits;
	}

}
