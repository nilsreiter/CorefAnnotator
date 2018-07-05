package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class MoveMentionPartToMention extends MoveOp<DetachedMentionPart, Mention> {
	Mention from, to;
	DetachedMentionPart part;

	public MoveMentionPartToMention(Mention target, DetachedMentionPart part) {
		super(part.getMention(), target, part);
	}

}