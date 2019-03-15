package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class RenameAllEntities implements CoreferenceModelOperation {
	public static enum Strategy {
		FIRST, LONGEST, LAST
	}

	Strategy strategy;
	MutableMap<Entity, String> oldNames = Maps.mutable.empty();

	public RenameAllEntities(Strategy strategy) {
		this.strategy = strategy;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void registerOldName(Entity entity, String oldName) {
		oldNames.put(entity, oldName);
	}

	public MutableMap<Entity, String> getOldNames() {
		return oldNames;
	}
}
