package de.unistuttgart.ims.coref.annotator;

import java.util.Set;

import org.reflections.Reflections;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class PluginManager {
	Set<Class<? extends IOPlugin>> plugins;

	public void init() {
		Reflections reflections = new Reflections("de.unistuttgart.ims.coref.annotator");

		plugins = reflections.getSubTypesOf(IOPlugin.class);

	}

	public Set<Class<? extends IOPlugin>> getImportPlugins() {
		return plugins;
	}

}
