package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.api.v1.MentionExtent;

public class RemoveMentionExtent implements CoreferenceModelOperation {
	Mention mention;

	ImmutableList<MentionExtent> mentionExtents;

	public RemoveMentionExtent(Mention mention, ImmutableList<MentionExtent> mentionExtents) {
		this.mention = mention;
		this.mentionExtents = mentionExtents;
	}

	public Mention getMention() {
		return mention;
	}

	public void setMention(Mention mention) {
		this.mention = mention;
	}

	public ImmutableList<MentionExtent> getMentionExtents() {
		return mentionExtents;
	}

	public void setMentionExtents(ImmutableList<MentionExtent> mentionExtents) {
		this.mentionExtents = mentionExtents;
	}
}
