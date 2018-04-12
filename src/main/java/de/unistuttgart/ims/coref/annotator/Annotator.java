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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;
import de.unistuttgart.ims.coref.annotator.action.ExitAction;
import de.unistuttgart.ims.coref.annotator.action.FileCompareOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileMergeOpenAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.HelpAction;
import de.unistuttgart.ims.coref.annotator.action.SelectedFileOpenAction;
import de.unistuttgart.ims.coref.annotator.action.ShowLogWindowAction;
import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class Annotator implements AboutHandler, PreferencesHandler, OpenFilesHandler, QuitHandler {

	public static final Logger logger = LogManager.getLogger(Annotator.class);

	static ResourceBundle rbundle;

	Set<DocumentWindow> openFiles = Sets.mutable.empty();

	public MutableList<File> recentFiles;

	TypeSystemDescription typeSystemDescription;

	PluginManager pluginManager = new PluginManager();

	JFileChooser openDialog;

	JFrame opening;
	JPanel statusBar;
	JPanel recentFilesPanel;

	LogWindow logWindow = null;
	UpdateCheck updateCheck = new UpdateCheck();

	AbstractAction openAction, quitAction = new ExitAction(), helpAction = new HelpAction();
	AbstractAction openCompareAction;

	Preferences preferences = Preferences.userNodeForPackage(Annotator.class);

	public static Annotator app;

	public static void main(String[] args) {
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

	public Annotator() throws ResourceInitializationException {
		logger.trace("Application startup");
		this.pluginManager.init();
		this.recentFiles = loadRecentFiles();

		try {
			if (!preferences.nodeExists(Constants.CFG_ANNOTATOR_ID))
				if (System.getProperty("user.name") != null)
					preferences.put(Constants.CFG_ANNOTATOR_ID, System.getProperty("user.name"));
				else
					preferences.put(Constants.CFG_ANNOTATOR_ID, Defaults.CFG_ANNOTATOR_ID);

		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		this.initialiseActions();
		this.initialiseTypeSystem();
		this.initialiseDialogs();

	}

	protected void initialiseDialogs() {
		openDialog = new JFileChooser();
		openDialog.setMultiSelectionEnabled(true);
		openDialog.setFileFilter(FileFilters.xmi);

		opening = getOpeningDialog();
	}

	protected void initialiseActions() {
		openAction = new FileSelectOpenAction(this);
		openCompareAction = new FileCompareOpenAction();
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
				handleQuitRequestWith(null, null);
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Annotator.getString("dialog.splash.default")));
		panel.setPreferredSize(new Dimension(width, 130));
		panel.add(new JButton(openAction));
		panel.add(new JButton(quitAction));
		panel.add(new JButton(helpAction));
		panel.add(new JButton(new ShowLogWindowAction(this)));
		panel.add(new JButton(openCompareAction));
		panel.add(new JButton(new FileMergeOpenAction()));
		mainPanel.add(panel);

		mainPanel.add(Box.createVerticalStrut(10));
		recentFilesPanel = new JPanel();
		recentFilesPanel.setBorder(BorderFactory.createTitledBorder(Annotator.getString("dialog.splash.recent")));
		recentFilesPanel.setPreferredSize(new Dimension(width, 200));
		refreshRecents();
		mainPanel.add(recentFilesPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Annotator.getString("dialog.splash.import")));
		panel.setPreferredSize(new Dimension(width, 200));
		pluginManager.getIOPlugins().forEachWith((plugin, pan) -> {
			IOPlugin p = getPluginManager().getIOPlugin(plugin);
			try {
				if (p.getImporter() != null) {
					AbstractAction importAction = new FileImportAction(this, p);
					pan.add(new JButton(importAction));
				}
			} catch (ResourceInitializationException e1) {
				logger.catching(e1);
			}
		}, panel);

		mainPanel.add(panel);

		for (Component c : mainPanel.getComponents())
			((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel versionLabel = new JLabel(Version.get().toString());

		statusBar = new JPanel();

		try {
			if (updateCheck.checkForUpdate()) {
				JButton button = new JButton();
				button.setText(Annotator.getString(Constants.Strings.STATUS_NOW_AVAILABLE) + ": "
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

	public synchronized DocumentWindow open(final File file, IOPlugin flavor, String language) {
		logger.trace("Creating new DocumentWindow");

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				DocumentWindow v = new DocumentWindow(Annotator.this);
				v.loadFile(file, flavor, language);
				openFiles.add(v);
				if (flavor instanceof DefaultIOPlugin)
					recentFiles.add(0, file);

			}
		};

		SwingUtilities.invokeLater(runnable);
		return null;
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
				open((File) file, new DefaultIOPlugin(), null);
			}
		}
	}

	@Override
	public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
		for (DocumentWindow v : openFiles)
			this.close(v);
		storeRecentFiles();
		try {
			preferences.sync();
		} catch (BackingStoreException e1) {
			logger.catching(e1);
		}
		System.exit(0);
	}

	@Override
	public void handleAbout(AboutEvent e) {
	}

	@Override
	public void handlePreferences(PreferencesEvent e) {
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
		openDialog.setCurrentDirectory(getCurrentDirectory());
		int r = openDialog.showOpenDialog(parent);
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			for (File f : openDialog.getSelectedFiles()) {
				setCurrentDirectory(f.getParentFile());
				open(f, flavor, Constants.X_UNSPECIFIED);
			}
			break;
		default:
			showOpening();
		}
	}

	public static String getString(String key) {
		try {
			return getString(key, Locale.getDefault());
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

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	private MutableList<File> loadRecentFiles() {
		MutableList<File> files = Lists.mutable.empty();
		String listOfFiles = preferences.get(Constants.PREF_RECENT, "");
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
		preferences.put(Constants.PREF_RECENT, sb.toString());
	}

	public JMenu getRecentFilesMenu() {
		JMenu m = new JMenu(Annotator.getString("menu.file.recent"));
		for (int i = 0; i < Math.min(20, recentFiles.size()); i++)
			m.add(new SelectedFileOpenAction(this, recentFiles.get(i)));
		return m;

	}

	public void refreshRecents() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				recentFilesPanel.removeAll();
				for (int i = 0; i < Math.min(recentFiles.size(), 10); i++) {
					File f = recentFiles.get(i);
					recentFilesPanel.add(new JButton(new SelectedFileOpenAction(Annotator.this, f)));
				}
			}
		});
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
		return new File(preferences.get(Constants.CFG_CURRENT_DIRECTORY, System.getProperty("user.home")));
	}

	public void setCurrentDirectory(File f) {
		preferences.put(Constants.CFG_CURRENT_DIRECTORY, f.getAbsolutePath());
		try {
			preferences.sync();
		} catch (BackingStoreException e1) {
			logger.catching(e1);
		}
	}

}
