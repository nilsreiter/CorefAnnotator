package de.unistuttgart.ims.coref.annotator.document;

import java.util.Collection;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class BatchOperationDescription implements Op {

	ImmutableList<Op> edits;

	public BatchOperationDescription(Op... edits) {
		this.edits = Lists.immutable.of(edits);
	}

	public BatchOperationDescription(Collection<Op> edits) {
		this.edits = Lists.immutable.withAll(edits);
	}

	public ImmutableList<Op> getEdits() {
		return edits;
	}

}
