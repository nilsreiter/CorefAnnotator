package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class RemoveEntities extends AbstractRemoveOperation<Entity> implements CoreferenceModelOperation {
	public MutableSetMultimap<Entity, Entity> entityEntityGroupMap = Multimaps.mutable.set.empty();

	public RemoveEntities(Entity... entities) {
		super(entities);
	}

	public RemoveEntities(Iterable<Entity> entities) {
		super(entities);
	}

	@Deprecated
	public ImmutableList<Entity> getEntities() {
		return getFeatureStructures();
	}

	@Deprecated
	public void setEntities(ImmutableList<Entity> entities) {
		setFeatureStructures(entities);
	}

}