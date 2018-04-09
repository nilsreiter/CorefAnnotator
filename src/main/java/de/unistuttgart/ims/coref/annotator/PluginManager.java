package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.reflections.Reflections;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultStylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.EntityRankingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.Plugin;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class PluginManager {
	ImmutableSet<Class<? extends IOPlugin>> ioPlugins;
	ImmutableSet<Class<? extends StylePlugin>> stylePlugins;
	ImmutableSet<Class<? extends EntityRankingPlugin>> rankingPlugins;
	ImmutableSet<Class<? extends ProcessingPlugin>> processingPlugins;
	Map<Class<? extends Plugin>, Plugin> instances = new HashMap<Class<? extends Plugin>, Plugin>();

	public void init() {
		Annotator.logger.trace("Initialising plugin manager");
		Reflections reflections = new Reflections("de.unistuttgart.ims.coref.annotator.plugin.");
		MutableSet<Class<? extends IOPlugin>> ioPlugins = Sets.mutable
				.withAll(reflections.getSubTypesOf(IOPlugin.class));
		// it's unclear why this is found in the first place
		ioPlugins.remove(DefaultIOPlugin.class);
		ioPlugins.remove(AbstractXmiPlugin.class);
		this.ioPlugins = ioPlugins.toImmutable();

		rankingPlugins = Sets.immutable.withAll(reflections.getSubTypesOf(EntityRankingPlugin.class));
		processingPlugins = Sets.immutable.withAll(reflections.getSubTypesOf(ProcessingPlugin.class));

		stylePlugins = Sets.immutable.withAll(reflections.getSubTypesOf(StylePlugin.class));
		Annotator.logger.info("Found IOPlugins: {}", StringUtils.join(ioPlugins, ','));
		Annotator.logger.info("Found StylePlugins: {}", StringUtils.join(stylePlugins.castToCollection(), ','));
		Annotator.logger.info("Found EntityRankingPlugins: {}",
				StringUtils.join(rankingPlugins.castToCollection(), ','));
		Annotator.logger.info("Found ProcessingPlugins: {}",
				StringUtils.join(processingPlugins.castToCollection(), ','));

		instances.put(DefaultIOPlugin.class, new DefaultIOPlugin());
		instances.put(DefaultStylePlugin.class, new DefaultStylePlugin());
	}

	public ImmutableSet<Class<? extends IOPlugin>> getIOPlugins() {
		return ioPlugins;
	}

	public ImmutableSet<Class<? extends StylePlugin>> getStylePlugins() {
		return stylePlugins;
	}

	public IOPlugin getDefaultIOPlugin() {
		return getIOPlugin(DefaultIOPlugin.class);
	}

	public StylePlugin getDefaultStylePlugin() {
		return getStylePlugin(DefaultStylePlugin.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPlugin(Class<T> cl) {
		if (!instances.containsKey(cl)) {
			T p;
			try {
				Annotator.logger.info("Creating new instance of plugin {}", cl.getName());
				p = cl.newInstance();
				instances.put(cl, p);
			} catch (InstantiationException | IllegalAccessException e) {
				Annotator.logger.catching(e);
			}
		}
		return (T) instances.get(cl);
	}

	public StylePlugin getStylePlugin(Class<? extends StylePlugin> clazz) {
		return getPlugin(clazz);
	}

	public IOPlugin getIOPlugin(Class<? extends IOPlugin> cl) {
		return getPlugin(cl);
	}

	public EntityRankingPlugin getEntityRankingPlugin(Class<? extends EntityRankingPlugin> cl) {
		return getPlugin(cl);
	}

	public ImmutableSet<Class<? extends EntityRankingPlugin>> getRankingPlugins() {
		return rankingPlugins;
	}

	public ImmutableSet<Class<? extends ProcessingPlugin>> getProcessingPlugins() {
		return processingPlugins;
	}
}
