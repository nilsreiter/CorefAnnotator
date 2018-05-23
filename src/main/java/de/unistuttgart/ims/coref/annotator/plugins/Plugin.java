package de.unistuttgart.ims.coref.annotator.plugins;

/**
 * All plugins should implement this interface to provide some meta data for the
 * UI.
 * 
 * @author reiterns
 *
 */
public interface Plugin {

	/**
	 * Returns a description of the plugin, to be shown as a tooltip and/or in the
	 * help menu.
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * A label for buttons and menu entries
	 * 
	 * @return
	 */
	String getName();

}
