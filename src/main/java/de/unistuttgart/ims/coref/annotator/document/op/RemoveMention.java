package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.Spans;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class RemoveMention extends AbstractRemoveOperation<Mention> implements CoreferenceModelOperation {
	Entity entity;
	ImmutableList<Spans> spans;
	boolean entityAutoDeleted = false;

	public RemoveMention(Mention... mention) {
		super(mention);
		this.spans = getFeatureStructures().collect(m -> new Spans(m));
		this.entity = getFeatureStructures().getFirst().getEntity();
	}

	public RemoveMention(Iterable<Mention> mention) {
		super(mention);
		this.spans = getFeatureStructures().collect(m -> new Spans(m));
		this.entity = getFeatureStructures().getFirst().getEntity();
	}

	public Entity getEntity() {
		return entity;
	}

	public Mention getMention() {
		return getFeatureStructures().getFirst();
	}

	@Deprecated
	public Spans getSpan() {
		return spans.getFirst();
	}

	public ImmutableList<Spans> getSpans() {
		return spans;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Deprecated
	public void setMentions(ImmutableList<Mention> mention) {
		setFeatureStructures(mention);
	}

	@Deprecated
	public ImmutableList<Mention> getMentions() {
		return getFeatureStructures();
	}

	/**
	 * @return the entityAutoDeleted
	 */
	public boolean isEntityAutoDeleted() {
		return entityAutoDeleted;
	}

	/**
	 * @param entityAutoDeleted the entityAutoDeleted to set
	 */
	public void setEntityAutoDeleted(boolean entityAutoDeleted) {
		this.entityAutoDeleted = entityAutoDeleted;
	}
}