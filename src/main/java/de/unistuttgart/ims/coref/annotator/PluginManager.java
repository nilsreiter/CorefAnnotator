package de.unistuttgart.ims.coref.annotator;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import de.unistuttgart.ims.coref.annotator.plugin.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugin.DefaultStylePlugin;
import de.unistuttgart.ims.coref.annotator.plugin.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugin.StylePlugin;

public class PluginManager {
	Set<Class<? extends IOPlugin>> ioPlugins;
	Set<Class<? extends StylePlugin>> stylePlugins;

	public void init() {
		Reflections reflections = new Reflections("de.unistuttgart.ims.coref.annotator.plugins");
		ioPlugins = reflections.getSubTypesOf(IOPlugin.class);
		stylePlugins = reflections.getSubTypesOf(StylePlugin.class);
		Annotator.logger.info("Found IOPlugins: {}", StringUtils.join(ioPlugins, ','));
		Annotator.logger.info("Found StylePlugins: {}", StringUtils.join(stylePlugins, ','));
	}

	public Set<Class<? extends IOPlugin>> getIOPlugins() {
		return ioPlugins;
	}

	public Set<Class<? extends StylePlugin>> getStylePlugins() {
		return stylePlugins;
	}

	public IOPlugin getDefaultIOPlugin() {
		return new DefaultIOPlugin();
	}

	public StylePlugin getDefaultStylePlugin() {
		return new DefaultStylePlugin();
	}

}
