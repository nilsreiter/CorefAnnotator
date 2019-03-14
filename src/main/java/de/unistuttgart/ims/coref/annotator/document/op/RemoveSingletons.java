package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class RemoveSingletons extends AbstractRemoveOperation<Entity> implements CoreferenceModelOperation {
	ImmutableList<Mention> mentions;

	public RemoveSingletons() {

	}

	public ImmutableList<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(ImmutableList<Mention> mentions) {
		this.mentions = mentions;
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