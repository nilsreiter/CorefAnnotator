package de.unistuttgart.ims.coref.annotator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class Annotator implements AboutHandler, PreferencesHandler, OpenFilesHandler, QuitHandler {

	public static final Logger logger = LogManager.getLogger(Annotator.class);
	Set<DocumentWindow> openFiles = new HashSet<DocumentWindow>();

	Configuration configuration;

	TypeSystemDescription typeSystemDescription;

	PluginManager pluginManager = new PluginManager();

	JFileChooser openDialog;

	public static void main(String[] args) throws UIMAException {

		Annotator a = new Annotator();
		a.fileOpenDialog(a.getPluginManager().getDefaultIOPlugin());
	}

	public Annotator() throws ResourceInitializationException {
		this.pluginManager.init();
		this.initialiseConfiguration();
		this.initialiseTypeSystem();
		this.initialiseDialogs();

	}

	private void initialiseDialogs() {
		openDialog = new JFileChooser();
		openDialog.setMultiSelectionEnabled(true);
		openDialog.setFileFilter(XmiFileFilter.filter);
	}

	protected void initialiseTypeSystem() throws ResourceInitializationException {
		typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription();
	}

	protected void initialiseConfiguration() {
		INIConfiguration defaultConfig = new INIConfiguration();
		INIConfiguration userConfig = new INIConfiguration();

		InputStream is = null;
		try {
			// reading of default properties from inside the war
			is = getClass().getResourceAsStream("/default-config.ini");
			if (is != null) {
				defaultConfig.read(new InputStreamReader(is, "UTF-8"));
				// defaults.load();
			}
		} catch (Exception e) {
			logger.warn("Could not read default configuration.");
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}

		File userConfigFile = null;
		try {
			File homeDirectory = new File(System.getProperty("user.home"));
			logger.debug("user.home: {}", homeDirectory.getAbsolutePath());
			logger.debug("user.home (URI): {}", homeDirectory.toURI().toString());
			userConfigFile = new File(homeDirectory, ".SimpleXmiViewer.ini");
			if (userConfigFile.exists())
				userConfig.read(new FileReader(userConfigFile));
			else
				userConfigFile.createNewFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigurationException | IOException e) {
			logger.warn("Could not read or parse user configuration in file {}. Exception: {}.", userConfigFile,
					e.getMessage());
			e.printStackTrace();
		}

		CombinedConfiguration config = new CombinedConfiguration(new OverrideCombiner());
		config.addConfiguration(userConfig);
		config.addConfiguration(defaultConfig);
		configuration = config;

	}

	public synchronized DocumentWindow open(final File file, IOPlugin flavor) {
		final DocumentWindow v = new DocumentWindow(this);

		/*
		 * new Thread() {
		 * 
		 * @Override public void run() {
		 */
		// logger.info("Loading XMI document from {}.", file);
		v.loadFile(file, flavor);
		/*
		 * } }.run();
		 */
		openFiles.add(v);
		return v;
	}

	public void close(DocumentWindow viewer) {
		openFiles.remove(viewer);
		viewer.dispose();
		if (openFiles.isEmpty())
			this.fileOpenDialog(new DefaultIOPlugin());
	};

	@Override
	public void openFiles(OpenFilesEvent e) {
		for (Object file : e.getFiles()) {
			if (file instanceof File) {
				open((File) file, new DefaultIOPlugin());
			}
		}
	}

	@Override
	public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
		for (DocumentWindow v : openFiles)
			this.close(v);
		System.exit(0);
	}

	@Override
	public void handleAbout(AboutEvent e) {
		// aboutDialog.setVisible(true);
	}

	@Override
	public void handlePreferences(PreferencesEvent e) {
		// prefDialog.setVisible(true);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void warnDialog(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public void fileOpenDialog(IOPlugin flavor) {
		openDialog.setDialogTitle("Open files using " + flavor.getName() + " scheme");
		int r = openDialog.showOpenDialog(null);
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			for (File f : openDialog.getSelectedFiles()) {
				open(f, flavor);
			}
			break;
		default:
			if (openFiles.isEmpty())
				handleQuitRequestWith(null, null);
		}
	}

	public static String getString(String key) {
		return getString(key, Locale.GERMAN);
	}

	public static String getString(String key, Locale locale) {
		ResourceBundle words = ResourceBundle.getBundle("locales/strings", locale);

		return words.getString(key);
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

}
