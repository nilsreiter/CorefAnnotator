package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class ToggleEntityFlag extends ToggleFlag<Entity> {

	public ToggleEntityFlag(String flag, Iterable<Entity> objects) {
		super(flag, objects);
	}

}