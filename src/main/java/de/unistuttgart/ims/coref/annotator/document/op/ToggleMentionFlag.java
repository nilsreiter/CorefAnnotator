package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

@Deprecated
public class ToggleMentionFlag extends ToggleFlag<Mention> {

	public ToggleMentionFlag(String flag, Iterable<Mention> objects) {
		super(flag, objects);
	}

}