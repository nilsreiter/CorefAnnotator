package de.unistuttgart.ims.coref.annotator.undo;

import java.util.Collection;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class BatchOperation extends AbstractEditOperation {

	ImmutableSet<EditOperation> edits;

	public BatchOperation(EditOperation... edits) {
		this.edits = Sets.immutable.of(edits);
	}

	public BatchOperation(Collection<EditOperation> edits) {
		this.edits = Sets.immutable.withAll(edits);
	}

	@Override
	public void revert(CoreferenceModel model) {
		for (EditOperation ed : edits) {
			ed.revert(model);
		}
	}

	@Override
	void perform(CoreferenceModel model) {
		for (EditOperation ed : edits) {
			ed.run(model);
		}
	}

}
