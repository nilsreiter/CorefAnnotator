package de.unistuttgart.ims.coref.annotator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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
import org.apache.uima.resource.metadata.TypeDescription;
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

public class Annotator implements AboutHandler, PreferencesHandler, OpenFilesHandler, QuitHandler {

	static final Logger logger = LogManager.getLogger(Annotator.class);
	Set<DocumentWindow> openFiles = new HashSet<DocumentWindow>();

	Configuration configuration;

	TypeSystemDescription typeSystemDescription;
	@Deprecated
	TypeDescription mentionType;
	@Deprecated
	TypeDescription entityType;

	JFileChooser openDialog;

	public static void main(String[] args) throws UIMAException {

		Annotator a = new Annotator();
		a.fileOpenDialog(CoreferenceFlavor.CRETA);
		a.open(new File("src/test/resources/rjmw.0.xmi"), CoreferenceFlavor.QuaDramA);
	}

	public Annotator() throws ResourceInitializationException {
		this.initialiseConfiguration();
		this.initialiseTypeSystem();
		this.initialiseDialogs();

	}

	private void initialiseDialogs() {
		openDialog = new JFileChooser();
		openDialog.setMultiSelectionEnabled(true);
		openDialog.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".xmi");
			}

			@Override
			public String getDescription() {
				return "UIMA Xmi Files";
			}
		});
	}

	protected void initialiseTypeSystem() throws ResourceInitializationException {
		typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription();
		mentionType = typeSystemDescription.getType(de.unistuttgart.ims.drama.api.Mention.class.getName());
		entityType = typeSystemDescription.getType(de.unistuttgart.ims.drama.api.DiscourseEntity.class.getName());
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

	public synchronized DocumentWindow open(final File file, CoreferenceFlavor flavor) {
		final DocumentWindow v = new DocumentWindow(this);

		/*
		 * new Thread() {
		 * 
		 * @Override public void run() {
		 */
		logger.info("Loading XMI document from {}.", file);
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
			this.fileOpenDialog(CoreferenceFlavor.Default);
	};

	@Override
	public void openFiles(OpenFilesEvent e) {
		for (Object file : e.getFiles()) {
			if (file instanceof File) {
				open((File) file, CoreferenceFlavor.Default);
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

	public void fileOpenDialog(CoreferenceFlavor flavor) {
		openDialog.setDialogTitle("Open files using " + flavor.name() + " scheme");
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

}
