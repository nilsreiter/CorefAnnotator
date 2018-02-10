package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import de.unistuttgart.ims.coref.annotator.action.ExitAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.HelpAction;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class Annotator implements AboutHandler, PreferencesHandler, OpenFilesHandler, QuitHandler {

	public static final Logger logger = LogManager.getLogger(Annotator.class);

	static ResourceBundle rbundle;

	Set<DocumentWindow> openFiles = new HashSet<DocumentWindow>();

	List<File> recentFiles;

	Configuration configuration;

	TypeSystemDescription typeSystemDescription;

	PluginManager pluginManager = new PluginManager();

	JFileChooser openDialog;

	JFrame opening;
	JPanel statusBar;

	AbstractAction openAction, quitAction = new ExitAction(), helpAction = new HelpAction();

	Preferences p = Preferences.userNodeForPackage(Annotator.class);

	public static Annotator app;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					app = new Annotator();
					app.showOpening();
				} catch (ResourceInitializationException e) {
					logger.catching(e);
				}
			}

		});
	}

	public Annotator() throws ResourceInitializationException {
		this.pluginManager.init();
		this.recentFiles = loadRecentFiles();
		this.initialiseConfiguration();
		this.initialiseActions();
		this.initialiseTypeSystem();
		this.initialiseDialogs();

	}

	protected void initialiseDialogs() {
		openDialog = new JFileChooser();
		openDialog.setMultiSelectionEnabled(true);
		openDialog.setFileFilter(XmiFileFilter.filter);
		opening = getOpeningDialog();
	}

	protected void initialiseActions() {
		openAction = new FileOpenAction(this);
	}

	protected JFrame getOpeningDialog() {

		JFrame opening = new JFrame();
		opening.setLocationByPlatform(true);
		// opening.setLocationRelativeTo(null);
		opening.setPreferredSize(new Dimension(300, 400));
		opening.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				opening.dispose();
				handleQuitRequestWith(null, null);
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(new JLabel(Annotator.getString("dialog.splash.default")));

		JPanel panel = new JPanel();
		panel.add(new JButton(openAction));
		panel.add(new JButton(quitAction));
		panel.add(new JButton(helpAction));
		panel.add(new JLabel(getClass().getPackage().getImplementationVersion()));
		mainPanel.add(panel);

		mainPanel.add(new JLabel(Annotator.getString("dialog.splash.recent")));
		panel = new JPanel();
		for (int i = 0; i < Math.min(recentFiles.size(), 8); i++) {
			File f = recentFiles.get(i);
			JButton button = new JButton();
			button.setText(f.getName());
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					open(f, getPluginManager().getDefaultIOPlugin());
				}
			});
			panel.add(button);
		}
		mainPanel.add(panel);

		mainPanel.add(new JLabel(Annotator.getString("dialog.splash.import")));
		panel = new JPanel();
		for (Class<? extends IOPlugin> plugin : getPluginManager().getIOPlugins()) {
			IOPlugin p = getPluginManager().getIOPlugin(plugin);
			try {
				if (p.getImporter() != null) {
					AbstractAction importAction = new FileImportAction(this, p);
					panel.add(new JButton(importAction));
				}
			} catch (ResourceInitializationException e1) {
				logger.catching(e1);
			}
		}
		mainPanel.add(panel);

		for (Component c : mainPanel.getComponents())
			((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel versionLabel = new JLabel(Annotator.class.getPackage().getImplementationTitle() + " "
				+ Annotator.class.getPackage().getImplementationVersion());

		statusBar = new JPanel();
		statusBar.add(versionLabel);

		opening.getContentPane().add(mainPanel, BorderLayout.CENTER);
		opening.getContentPane().add(statusBar, BorderLayout.SOUTH);
		opening.pack();
		return opening;
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
		DocumentWindow v = new DocumentWindow(this);
		v.setVisible(true);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				v.loadFile(file, flavor);
			}
		};

		SwingUtilities.invokeLater(runnable);
		openFiles.add(v);
		if (flavor instanceof DefaultIOPlugin)
			recentFiles.add(0, file);
		return v;
	}

	public void close(DocumentWindow viewer) {
		openFiles.remove(viewer);
		viewer.dispose();
		if (openFiles.isEmpty())
			this.showOpening();
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
		storeRecentFiles();
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

	public void showOpening() {
		this.opening.setVisible(true);
	}

	public void fileOpenDialog(Component parent, IOPlugin flavor) {
		openDialog.setDialogTitle("Open files using " + flavor.getName() + " scheme");
		openDialog.setFileFilter(flavor.getFileFilter());
		int r = openDialog.showOpenDialog(parent);
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			for (File f : openDialog.getSelectedFiles()) {
				open(f, flavor);
			}
			break;
		default:
			showOpening();
		}
	}

	public static String getString(String key) {
		return getString(key, Locale.getDefault());
	}

	public static String getString(String key, Locale locale) {
		if (rbundle == null)
			rbundle = ResourceBundle.getBundle("locales/strings", locale);
		return rbundle.getString(key);
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	private List<File> loadRecentFiles() {
		List<File> files = new LinkedList<File>();
		String listOfFiles = p.get(Constants.PREF_RECENT, "");
		logger.debug(listOfFiles);
		String[] fileNames = listOfFiles.split(File.pathSeparator);
		for (String fileRef : fileNames) {
			File file = new File(fileRef);
			if (file.exists() && !files.contains(file)) {
				files.add(file);
			}

		}
		return files;
	}

	private void storeRecentFiles() {
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < recentFiles.size(); index++) {
			File file = recentFiles.get(index);
			if (sb.length() > 0) {
				sb.append(File.pathSeparator);
			}
			sb.append(file.getPath());
		}
		Preferences p = Preferences.userNodeForPackage(Annotator.class);
		p.put(Constants.PREF_RECENT, sb.toString());
	}

}
