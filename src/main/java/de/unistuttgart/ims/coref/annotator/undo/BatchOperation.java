package de.unistuttgart.ims.coref.annotator.undo;

import java.util.Collection;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

public class BatchOperation implements EditOperationDescription {

	ImmutableSet<EditOperationDescription> edits;

	public BatchOperation(EditOperationDescription... edits) {
		this.edits = Sets.immutable.of(edits);
	}

	public BatchOperation(Collection<EditOperationDescription> edits) {
		this.edits = Sets.immutable.withAll(edits);
	}

}
