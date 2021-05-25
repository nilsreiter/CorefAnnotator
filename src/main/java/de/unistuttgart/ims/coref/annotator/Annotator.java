package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import com.ibm.icu.text.MessageFormat;

import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;
import de.unistuttgart.ims.coref.annotator.action.ExitAction;
import de.unistuttgart.ims.coref.annotator.action.FileCompareOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileMergeOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectAnalyzeAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.HelpAction;
import de.unistuttgart.ims.coref.annotator.action.SelectedFileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ImportPlugin;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class Annotator implements PreferenceChangeListener {

	public static final Logger logger = LogManager.getLogger(Annotator.class);

	static ResourceBundle rbundle;

	Set<DocumentWindow> openFiles = Sets.mutable.empty();

	TypeSystemDescription typeSystemDescription;

	PluginManager pluginManager = new PluginManager();

	JFrame opening;
	JPanel statusBar;
	JPanel recentFilesPanel;

	LogWindow logWindow = null;
	UpdateCheck updateCheck = new UpdateCheck();

	AbstractAction openAction, quitAction = new ExitAction(), helpAction = new HelpAction();
	AbstractAction openCompareAction;

	Preferences preferences = Preferences.userNodeForPackage(Annotator.class);

	public static Annotator app;

	static Boolean javafx = null;

	public static void main(String[] args) {
//		Annotator.logger.error("error");
//		Annotator.logger.warn("warn");
//		Annotator.logger.info("info");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						Annotator.logger.error("Could not set look and feel {}.", e.getMessage());
					}

					app = new Annotator();
					app.showOpening();
				} catch (ResourceInitializationException e) {
					logger.catching(e);
				}
			}

		});
	}

	@SuppressWarnings("unused")
	public Annotator() throws ResourceInitializationException {
		logger.info("Application startup. Version " + Version.get().toString());
		if (Annotator.javafx())
			new JFXPanel();
		this.pluginManager.init();
		this.preferences.addPreferenceChangeListener(this);

		try {
			if (!preferences.nodeExists(Constants.CFG_ANNOTATOR_ID))
				if (System.getProperty("user.name") != null)
					preferences.put(Constants.CFG_ANNOTATOR_ID, System.getProperty("user.name"));
				else
					preferences.put(Constants.CFG_ANNOTATOR_ID, Defaults.CFG_ANNOTATOR_ID);

			for (String key : preferences.keys()) {
				Annotator.logger.info("Preference {} set to {}", key, preferences.get(key, null));
			}
		} catch (BackingStoreException e) {
			Annotator.logger.catching(e);
		}

		this.initialiseActions();
		this.initialiseTypeSystem();
		this.initialiseDialogs();

	}

	protected void initialiseDialogs() {

		opening = getOpeningDialog();
	}

	protected void initialiseActions() {
		openAction = new FileSelectOpenAction(this);
		openCompareAction = new FileCompareOpenAction();
	}

	public static String getAppName() {
		// return "CorefAnnotator";
		return Annotator.class.getPackage().getImplementationTitle();
	}

	protected JFrame getOpeningDialog() {
		int width = 300;
		JFrame opening = new JFrame();
		opening.setLocationByPlatform(true);
		opening.setTitle(Annotator.class.getPackage().getImplementationTitle());
		opening.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				opening.dispose();
				handleQuitRequestWith();
			}
		});
		opening.setContentPane(new JPanel());
		opening.getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(width, 700));
		SpringLayout mainPanelLayout = new SpringLayout();
		mainPanel.setLayout(mainPanelLayout);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Annotator.getString(Strings.DIALOG_SPLASH_DEFAULT)));
		panel.setPreferredSize(new Dimension(width, 130));
		panel.add(new JButton(openAction));
		panel.add(new JButton(quitAction));
		panel.add(new JButton(helpAction));
		panel.add(new JButton(new ShowLogWindowAction(this)));
		panel.add(new JButton(openCompareAction));
		panel.add(new JButton(new FileMergeOpenAction()));
		panel.add(new JButton(new FileSelectAnalyzeAction()));
		mainPanel.add(panel);
		mainPanelLayout.putConstraint(SpringLayout.NORTH, panel, 5, SpringLayout.NORTH, mainPanel);
		mainPanelLayout.putConstraint(SpringLayout.WEST, panel, 5, SpringLayout.WEST, mainPanel);
		mainPanelLayout.putConstraint(SpringLayout.EAST, panel, -5, SpringLayout.EAST, mainPanel);

		recentFilesPanel = new JPanel();
		recentFilesPanel.setBorder(BorderFactory.createTitledBorder(Annotator.getString(Strings.DIALOG_SPLASH_RECENT)));
		recentFilesPanel.setPreferredSize(new Dimension(width, 200));
		preferenceChange(new PreferenceChangeEvent(preferences, Constants.PREF_RECENT, null));
		mainPanel.add(recentFilesPanel);
		mainPanelLayout.putConstraint(SpringLayout.NORTH, recentFilesPanel, 15, SpringLayout.SOUTH, panel);
		mainPanelLayout.putConstraint(SpringLayout.WEST, recentFilesPanel, 5, SpringLayout.WEST, mainPanel);
		mainPanelLayout.putConstraint(SpringLayout.EAST, recentFilesPanel, -5, SpringLayout.EAST, mainPanel);

		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Annotator.getString(Strings.DIALOG_SPLASH_IMPORT)));
		panel.setPreferredSize(new Dimension(width, 200));
		pluginManager.getIOPluginObjects().selectInstancesOf(ImportPlugin.class).forEachWith((p, pan) -> {
			AbstractAction importAction = new FileImportAction(this, p);
			pan.add(new JButton(importAction));
		}, panel);

		mainPanel.add(panel);
		mainPanelLayout.putConstraint(SpringLayout.NORTH, panel, 15, SpringLayout.SOUTH, recentFilesPanel);
		mainPanelLayout.putConstraint(SpringLayout.WEST, panel, 5, SpringLayout.WEST, mainPanel);
		mainPanelLayout.putConstraint(SpringLayout.EAST, panel, -5, SpringLayout.EAST, mainPanel);

		if (isFirstLaunch()) {
			JPanel lastPanel = panel;
			panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder(Annotator.getString(Strings.DIALOG_SPLASH_FIRSTTIME)));
			panel.setPreferredSize(new Dimension(width, 200));
			JLabel label = new JLabel();
			label.setIcon(FontIcon.of(MaterialDesign.MDI_INFORMATION_OUTLINE, 24));
			label.setText(Annotator.getString(Strings.DIALOG_SPLASH_FIRSTTIME_TEXT, Version.get()));
			panel.add(label);
			HelpAction ha = new HelpAction(HelpWindow.Topic.WHATSNEW);
			ha.putValue(Action.NAME, Annotator.getString(Strings.DIALOG_SPLASH_FIRSTTIME_BUTTON));
			ha.putValue(Action.LARGE_ICON_KEY, null);
			ha.putValue(Action.SMALL_ICON, null);
			panel.add(new JButton(ha));
			mainPanel.add(panel);

			mainPanelLayout.putConstraint(SpringLayout.NORTH, panel, 15, SpringLayout.SOUTH, lastPanel);
			mainPanelLayout.putConstraint(SpringLayout.WEST, panel, 5, SpringLayout.WEST, mainPanel);
			mainPanelLayout.putConstraint(SpringLayout.EAST, panel, -5, SpringLayout.EAST, mainPanel);
		}
		mainPanelLayout.putConstraint(SpringLayout.SOUTH, panel, -5, SpringLayout.SOUTH, mainPanel);

		mainPanel.validate();

		JLabel versionLabel = new JLabel(Version.get().toString());

		statusBar = new JPanel();

		try {
			if (updateCheck.checkForUpdate()) {
				JButton button = new JButton();
				button.setText(Annotator.getString(Strings.STATUS_NOW_AVAILABLE) + ": "
						+ updateCheck.getRemoteVersion().toString());
				button.setIcon(FontIcon.of(MaterialDesign.MDI_NEW_BOX));
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().browse(updateCheck.getReleasePage());
						} catch (IOException e1) {
							logger.catching(e1);
						}
					}

				});
				statusBar.add(button);
			} else {
				statusBar.add(versionLabel);
			}
		} catch (IOException e1) {
			logger.catching(e1);
			statusBar.add(versionLabel);
		}

		opening.getContentPane().add(mainPanel, BorderLayout.CENTER);
		opening.getContentPane().add(statusBar, BorderLayout.SOUTH);
		opening.pack();
		return opening;
	}

	protected void initialiseTypeSystem() throws ResourceInitializationException {
		typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription();
	}

	public synchronized DocumentWindow open(final File file, ImportPlugin flavor, String language) {
		logger.trace("Creating new DocumentWindow");
		DocumentWindow v = new DocumentWindow();

		if (flavor instanceof ConfigurableImportPlugin)
			((ConfigurableImportPlugin) flavor).showImportConfigurationDialog(v, fl -> {
				v.loadFile(file, flavor, language);

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						openFiles.add(v);
						if (flavor instanceof DefaultImportPlugin)
							addRecentFile(file);
						v.initialise();

					}
				});
			});
		else {
			v.loadFile(file, flavor, language);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					openFiles.add(v);
					if (flavor instanceof DefaultImportPlugin)
						addRecentFile(file);
					v.initialise();

				}

			});
		}
		return null;

	}

	public void close(DocumentWindow viewer) {
		openFiles.remove(viewer);
		viewer.dispose();
		if (openFiles.isEmpty())
			this.showOpening();
	};

	public void handleQuitRequestWith() {
		for (DocumentWindow v : openFiles)
			this.close(v);
		preferences.put(Constants.PREF_LAST_VERSION, Version.get().toString());

		try {
			preferences.sync();
		} catch (BackingStoreException e1) {
			logger.catching(e1);
		}
		Annotator.logger.info("Shutting down.");
		System.exit(0);
	}

	public void warnDialog(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public void showOpening() {
		this.opening.setVisible(true);
	}

	public void fileOpenDialog(Component parent, ImportPlugin flavor, boolean multi, Consumer<File[]> okCallback,
			Consumer<Object> cancelCallback, String title) {
		if (Annotator.javafx()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
					fileChooser.setTitle(title);
					fileChooser.setInitialDirectory(getCurrentDirectory());
					// fileChooser.getExtensionFilters().clear();
					fileChooser.getExtensionFilters().add(flavor.getExtensionFilter());
					File[] result;
					if (multi) {
						result = fileChooser.showOpenMultipleDialog(null).toArray(new File[] {});
						if (result != null) {
							setCurrentDirectory(result[0].getParentFile());
							okCallback.accept(result);
							return;
						}
					} else {
						result = new File[1];
						result[0] = fileChooser.showOpenDialog(null);
						if (result[0] != null) {
							setCurrentDirectory(result[0].getParentFile());
							okCallback.accept(result);
							return;
						}
					}
					cancelCallback.accept(null);
				}
			});
		} else {
			JFileChooser openDialog;
			openDialog = new JFileChooser();
			openDialog.setMultiSelectionEnabled(multi);
			openDialog.setFileFilter(FileFilters.xmi_gz);
			openDialog.setDialogTitle(title);
			openDialog.setFileFilter(flavor.getFileFilter());
			openDialog.setCurrentDirectory(getCurrentDirectory());
			int r = openDialog.showOpenDialog(parent);
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File[] selectedFiles;
				if (multi)
					selectedFiles = openDialog.getSelectedFiles();
				else {
					selectedFiles = new File[1];
					selectedFiles[0] = openDialog.getSelectedFile();
				}
				setCurrentDirectory(selectedFiles[0].getParentFile());
				okCallback.accept(selectedFiles);
				break;
			default:
				cancelCallback.accept(null);
			}
		}
	}

	public void fileOpenDialog(Component parent, ImportPlugin flavor) {
		fileOpenDialog(parent, flavor, false, f -> open(f[0], flavor, Constants.X_UNSPECIFIED), o -> showOpening(),
				Annotator.getString(Strings.DIALOG_OPEN_WITH_TITLE, flavor.getName()));
	}

	public static String getString(String key, Object... parameters) {
		try {
			return getString(key, Locale.getDefault(), parameters);
		} catch (java.util.MissingResourceException e) {
			logger.catching(e);
			return key;
		}
	}

	public static String getString(String key, Locale locale) {
		if (rbundle == null)
			rbundle = ResourceBundle.getBundle("locales/strings", locale);
		return rbundle.getString(key);
	}

	public static String getString(String key, Locale locale, Object... parameters) {
		if (rbundle == null)
			rbundle = ResourceBundle.getBundle("locales/strings", locale);
		if (parameters.length > 0) {
			String s = rbundle.getString(key);
			return MessageFormat.format(s, parameters);
		}
		return rbundle.getString(key);
	}

	public static String getStringWithDefault(String key, String defaultValue) {
		try {
			return getString(key, Locale.getDefault());
		} catch (java.util.MissingResourceException e) {
			return defaultValue;
		}
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	private MutableList<File> getRecentFilesFromPreferences() {
		MutableList<File> files = Lists.mutable.empty();
		String listOfFiles = preferences.get(Constants.PREF_RECENT, "");
		String[] fileNames = listOfFiles.split(File.pathSeparator);
		for (String fileRef : fileNames) {
			File file = new File(fileRef);
			if (file.exists() && !files.contains(file) && FileFilters.ca2.accept(file)) {
				files.add(file);
			}
		}
		return files;
	}

	private void recentFiles2Preferences(MutableList<File> recentFiles) {
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < recentFiles.size(); index++) {
			File file = recentFiles.get(index);
			if (sb.length() > 0) {
				sb.append(File.pathSeparator);
			}
			sb.append(file.getPath());
		}
		preferences.put(Constants.PREF_RECENT, sb.toString());
	}

	public JMenu getRecentFilesMenu() {
		ImmutableList<File> recentFiles = getRecentFilesFromPreferences().toImmutable();
		JMenu m = new JMenu(Annotator.getString(Strings.MENU_FILE_RECENT));
		for (int i = 0; i < Math.min(20, recentFiles.size()); i++)
			m.add(new SelectedFileOpenAction(this, recentFiles.get(i)));
		return m;

	}

	public Preferences getPreferences() {
		return preferences;
	}

	public LogWindow getLogWindow() {
		if (logWindow == null)
			logWindow = new LogWindow();
		return logWindow;
	}

	public File getCurrentDirectory() {
		File f = new File(preferences.get(Constants.CFG_CURRENT_DIRECTORY, System.getProperty("user.home")));
		if (!f.isDirectory())
			f = new File(System.getProperty("user.home"));
		return f;
	}

	public void setCurrentDirectory(File f) {
		preferences.put(Constants.CFG_CURRENT_DIRECTORY, f.getAbsolutePath());
		try {
			preferences.sync();
		} catch (BackingStoreException e1) {
			logger.catching(e1);
		}
	}

	@SuppressWarnings("unused")
	public static boolean javafx() {
		if (javafx == null)
			try {
				Class.forName("javafx.embed.swing.JFXPanel");
				new JFXPanel();
				javafx = true;
			} catch (Exception e) {
				javafx = false;
			}
		return javafx;
	}

	/**
	 * Adds a file to the list of recent files. If the file is already in the list,
	 * it is removed from the list and re-added at the front, effectively moving it
	 * to the front.
	 * 
	 * @param f The file to be added
	 * @return true, if the file was not already in the list, false otherwise.
	 */
	public boolean addRecentFile(File f) {
		MutableList<File> recentFiles = getRecentFilesFromPreferences();
		Annotator.logger.debug("File {} added to list of recent files", f);

		boolean contains = recentFiles.contains(f);
		if (contains) {
			recentFiles.remove(f);
		}
		recentFiles.add(0, f);
		recentFiles2Preferences(recentFiles);
		return !contains;

	}

	public boolean isFirstLaunch() {
		Version currentVersion = Version.get();
		String lastVersionString = preferences.get(Constants.PREF_LAST_VERSION, currentVersion.toString());
		Version lastVersion = Version.get(lastVersionString);
		return lastVersion.compareTo(currentVersion) < 0;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey() == Constants.PREF_RECENT) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ImmutableList<File> recentFiles = getRecentFilesFromPreferences().toImmutable();
					recentFilesPanel.removeAll();
					for (int i = 0; i < Math.min(recentFiles.size(), 10); i++) {
						File f = recentFiles.get(i);
						recentFilesPanel.add(new JButton(new SelectedFileOpenAction(Annotator.this, f)));
					}
					recentFilesPanel.repaint();
					recentFilesPanel.validate();
					opening.validate();
				}
			});

		}

	}

}
