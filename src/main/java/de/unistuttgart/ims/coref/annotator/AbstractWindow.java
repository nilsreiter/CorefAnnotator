package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;
import de.unistuttgart.ims.coref.annotator.action.SetAnnotatorNameAction;
import de.unistuttgart.ims.coref.annotator.action.TogglePreferenceAction;

public abstract class AbstractWindow extends JFrame implements PreferenceChangeListener {

	private static final long serialVersionUID = 1L;
	JPanel statusBar = new JPanel();
	protected JProgressBar progressBar = new JProgressBar();
	JLabel messageLabel = new JLabel();
	JLabel miscLabel = new JLabel();
	JLabel miscLabel2 = new JLabel();
	JPanel entityPanel = new JPanel();
	Thread messageVoider;
	JMenuBar menuBar = new JMenuBar();

	JMenu menu_settings = null;

	public AbstractWindow() {
		Annotator.app.getPreferences().addPreferenceChangeListener(this);
	}

	protected void initializeWindow() {
		SpringLayout springs = new SpringLayout();
		statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(800, 20));
		statusBar.setLayout(springs);
		// statusBar.setBorder(BorderFactory.createLineBorder(Color.CYAN));

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setPreferredSize(new Dimension(300, 20));

		statusBar.add(progressBar);
		statusBar.add(miscLabel);
		statusBar.add(miscLabel2);

		messageLabel = new JLabel();
		messageLabel.setSize(new Dimension(1, 20));
		messageLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		statusBar.add(messageLabel);

		entityPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		// entityPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		entityPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		entityPanel.setSize(new Dimension(0, 20));
		statusBar.add(entityPanel);

		JLabel versionLabel = new JLabel(Annotator.getAppName() + " " + Version.get().toString());
		versionLabel.setPreferredSize(new Dimension(220, 20));
		statusBar.add(versionLabel);

		// from east
		springs.putConstraint(SpringLayout.EAST, versionLabel, 10, SpringLayout.EAST, statusBar);
		springs.putConstraint(SpringLayout.EAST, miscLabel, 10, SpringLayout.WEST, versionLabel);
		springs.putConstraint(SpringLayout.EAST, miscLabel2, -10, SpringLayout.WEST, miscLabel);

		// from west
		springs.putConstraint(SpringLayout.WEST, messageLabel, 10, SpringLayout.WEST, statusBar);
		springs.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.EAST, messageLabel);
		springs.putConstraint(SpringLayout.WEST, entityPanel, 10, SpringLayout.EAST, progressBar);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.revalidate();
	}

	public JPanel getStatusBar() {
		return statusBar;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setCollectionPanel(Iterable<? extends Component> entities) {
		Lists.immutable.of(entityPanel.getComponents()).selectInstancesOf(PreferenceChangeListener.class)
				.forEach(ep -> Annotator.app.getPreferences().removePreferenceChangeListener(ep));
		entityPanel.removeAll();
		int maxObjects = 2;
		Iterator<? extends Component> iterator = entities.iterator();

		int length = 0;
		while (iterator.hasNext()) {
			Component e = iterator.next();
			if (length++ < maxObjects)
				entityPanel.add(e);
		}
		if (length > maxObjects)
			entityPanel.add(new JLabel(" and " + (length - maxObjects) + " more."));
		entityPanel.updateUI();
	};

	public void setIndeterminateProgress() {
		progressBar.setVisible(true);
		progressBar.setPreferredSize(new Dimension(300, 20));
		progressBar.setIndeterminate(true);
	}

	public void stopIndeterminateProgress() {
		progressBar.setIndeterminate(false);
		progressBar.setPreferredSize(new Dimension(0, 20));
		progressBar.setVisible(false);
	}

	/**
	 * Runs on the EDT
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		setMessage(message, 5000);
	}

	protected synchronized void setMessage(String message, int disappearingAfter) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				messageLabel.setText(message);
				messageLabel.repaint();
				statusBar.revalidate();
			}
		});

		if (messageVoider != null && messageVoider.isAlive())
			messageVoider.interrupt();

		if (disappearingAfter > 0) {
			messageVoider = new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(disappearingAfter);
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								setMessage("");
							}
						});
					} catch (InterruptedException | InvocationTargetException e) {
						// ignore
					}
				}

			};
			messageVoider.start();
		}
	}

	public JLabel getMiscLabel() {
		return miscLabel;
	}

	public JMenu initialiseMenuSettings() {
		menu_settings = new JMenu(Annotator.getString(Strings.MENU_SETTINGS));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_TRIM_WHITESPACE)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_SHOW_TEXT_LABELS)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_SHOW_LINE_NUMBER_IN_TREE)));
		menu_settings.add(
				new JCheckBoxMenuItem(TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_FULL_TOKENS)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_KEEP_TREE_SORTED)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_DELETE_EMPTY_ENTITIES)));
		menu_settings.add(new SetAnnotatorNameAction(Annotator.app));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_ASK_BEFORE_FILE_OVERWRITE)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_IGNORE_SINGLETONS_WHEN_COMPARING)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_REPLACE_MENTION)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_UNDERLINE_SINGLETONS_IN_GRAY)));
		menu_settings.add(
				new JCheckBoxMenuItem(TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_SHOW_TOC)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_CREATE_DEFAULT_FLAGS)));
		menu_settings.add(new JCheckBoxMenuItem(
				TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_COMPARE_BY_ENTITY_NAME)));
		menu_settings.add(
				new JCheckBoxMenuItem(TogglePreferenceAction.getAction(Annotator.app, Constants.SETTING_STICKY_FLAGS)));

		return menu_settings;

	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
	};

}
