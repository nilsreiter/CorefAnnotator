package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class DuplicateMentions implements CoreferenceModelOperation {
	ImmutableSet<Mention> sourceMentions;
	ImmutableSet<Mention> newMentions;

	public DuplicateMentions(Mention... mentions) {
		this.sourceMentions = Sets.immutable.of(mentions);
	}

	public DuplicateMentions(Iterable<Mention> mentions) {
		this.sourceMentions = Sets.immutable.withAll(mentions);
	}

	public ImmutableSet<Mention> getSourceMentions() {
		return sourceMentions;
	}

	public void setSourceMentions(ImmutableSet<Mention> sourceMentions) {
		this.sourceMentions = sourceMentions;
	}

	public ImmutableSet<Mention> getNewMentions() {
		return newMentions;
	}

	public void setNewMentions(ImmutableSet<Mention> newMentions) {
		this.newMentions = newMentions;
	}
}
