package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class RemoveMention extends AbstractRemoveOperation<Mention> implements CoreferenceModelOperation {
	Entity entity;
	ImmutableList<Span> spans;
	boolean entityAutoDeleted = false;

	public RemoveMention(Mention... mention) {
		super(mention);
		this.spans = getFeatureStructures().collect(m -> new Span(m));
		this.entity = getFeatureStructures().getFirst().getEntity();
	}

	public RemoveMention(Iterable<Mention> mention) {
		super(mention);
		this.spans = getFeatureStructures().collect(m -> new Span(m));
		this.entity = getFeatureStructures().getFirst().getEntity();
	}

	public Entity getEntity() {
		return entity;
	}

	public Mention getMention() {
		return getFeatureStructures().getFirst();
	}

	public Span getSpan() {
		return spans.getFirst();
	}

	public ImmutableList<Span> getSpans() {
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