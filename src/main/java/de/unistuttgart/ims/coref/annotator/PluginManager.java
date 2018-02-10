package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultStylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.Plugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class PluginManager {
	Set<Class<? extends IOPlugin>> ioPlugins;
	Set<Class<? extends StylePlugin>> stylePlugins;
	Map<Class<? extends Plugin>, Plugin> instances = new HashMap<Class<? extends Plugin>, Plugin>();

	public void init() {
		Reflections reflections = new Reflections("de.unistuttgart.ims.coref.annotator.plugin.");
		ioPlugins = reflections.getSubTypesOf(IOPlugin.class);
		// it's unclear why this is found in the first place
		ioPlugins.remove(DefaultIOPlugin.class);
		ioPlugins.remove(AbstractXmiPlugin.class);
		stylePlugins = reflections.getSubTypesOf(StylePlugin.class);
		Annotator.logger.info("Found IOPlugins: {}", StringUtils.join(ioPlugins, ','));
		Annotator.logger.info("Found StylePlugins: {}", StringUtils.join(stylePlugins, ','));

		instances.put(DefaultIOPlugin.class, new DefaultIOPlugin());
		instances.put(DefaultStylePlugin.class, new DefaultStylePlugin());
	}

	public Set<Class<? extends IOPlugin>> getIOPlugins() {
		return ioPlugins;
	}

	public Set<Class<? extends StylePlugin>> getStylePlugins() {
		return stylePlugins;
	}

	public IOPlugin getDefaultIOPlugin() {
		return getIOPlugin(DefaultIOPlugin.class);
	}

	public StylePlugin getDefaultStylePlugin() {
		return getStylePlugin(DefaultStylePlugin.class);
	}

	public Plugin getPlugin(Class<? extends Plugin> cl) {
		if (!instances.containsKey(cl)) {
			Plugin p;
			try {
				Annotator.logger.info("Creating new instance of plugin {}", cl.getName());
				p = cl.newInstance();
				instances.put(cl, p);
			} catch (InstantiationException | IllegalAccessException e) {
				Annotator.logger.catching(e);
			}
		}
		return instances.get(cl);
	}

	public StylePlugin getStylePlugin(Class<? extends StylePlugin> clazz) {
		return (StylePlugin) getPlugin(clazz);
	}

	public IOPlugin getIOPlugin(Class<? extends IOPlugin> cl) {
		return (IOPlugin) getPlugin(cl);
	}

}
