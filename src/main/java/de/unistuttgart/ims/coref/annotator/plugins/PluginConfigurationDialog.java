package de.unistuttgart.ims.coref.annotator.plugins;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;

public class PluginConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public PluginConfigurationDialog(Window parent, ConfigurableExportPlugin plugin,
			Consumer<ConfigurableExportPlugin> callback, Iterable<PluginOption> options) {
		super(parent, Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE_, plugin.getName()));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));

		for (PluginOption option : options) {
			optionPanel.add(option.getLabel());
			optionPanel.add(option.getComponent());
		}

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				for (PluginOption option : options)
					option.ok();

				PluginConfigurationDialog.this.dispose();
				callback.accept(plugin);
			}
		};

		Action cancelAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.DIALOG_CANCEL)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				PluginConfigurationDialog.this.dispose();
			}
		};

		Action helpAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.MENU_HELP)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpWindow.show("Input/Output");
			}
		};

		JButton okButton = new JButton(okAction);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(new JButton(cancelAction));
		buttonPanel.add(new JButton(helpAction));

		getContentPane().add(optionPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(parent);
		SwingUtilities.getRootPane(okButton).setDefaultButton(okButton);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
