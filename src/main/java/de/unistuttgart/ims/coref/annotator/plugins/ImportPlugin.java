package de.unistuttgart.ims.coref.annotator.plugins;

public interface ImportPlugin extends IOPlugin {
	Class<? extends StylePlugin> getStylePlugin();

}
