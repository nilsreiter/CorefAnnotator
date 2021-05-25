package de.unistuttgart.ims.coref.annotator.plugins;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;

public class PluginConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	JPanel optionPanel;
	GridBagConstraints optionPanelConstraints;

	public PluginConfigurationDialog(Window parent, ConfigurableExportPlugin plugin,
			Consumer<ConfigurableExportPlugin> callback, Iterable<PluginOption> options) {
		super(parent, Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE_, plugin.getName()));

		optionPanel = new JPanel(new GridBagLayout());
		optionPanelConstraints = new GridBagConstraints();
		optionPanelConstraints.anchor = GridBagConstraints.WEST;

		optionPanelConstraints.gridy = 0;
		for (PluginOption option : options) {
			optionPanelConstraints.gridx = 0;
			optionPanelConstraints.weightx = 2;
			optionPanel.add(option.getLabel(), optionPanelConstraints);
			optionPanelConstraints.gridx = 1;
			optionPanelConstraints.weightx = 1;
			optionPanel.add(option.getComponent(), optionPanelConstraints);
			optionPanelConstraints.gridy++;
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

	public void setDescription(String string) {
		optionPanelConstraints.ipady = 10;
		optionPanelConstraints.gridx = 0;
		optionPanelConstraints.gridwidth = 2;
		optionPanel.add(new JLabel(string), optionPanelConstraints);

		pack();
		validate();
	}
}
