package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public interface ICoreferenceModel extends Model {

	public ImmutableSortedSet<Mention> getMentions();
}
