package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;

public abstract class AbstractWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	JPanel statusBar = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel messageLabel = new JLabel();
	JLabel miscLabel = new JLabel();
	Thread messageVoider;

	protected void initialize() {
		SpringLayout springs = new SpringLayout();
		statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(800, 20));
		statusBar.setLayout(springs);

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setPreferredSize(new Dimension(300, 20));

		statusBar.add(progressBar);
		statusBar.add(miscLabel);

		messageLabel = new JLabel();
		messageLabel.setSize(new Dimension(1, 20));
		statusBar.add(messageLabel);

		JLabel versionLabel = new JLabel(
				Annotator.class.getPackage().getImplementationTitle() + " " + Version.get().toString());
		versionLabel.setPreferredSize(new Dimension(220, 20));
		statusBar.add(versionLabel);

		springs.putConstraint(SpringLayout.EAST, versionLabel, 10, SpringLayout.EAST, statusBar);
		springs.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.EAST, messageLabel);
		springs.putConstraint(SpringLayout.WEST, messageLabel, 10, SpringLayout.WEST, statusBar);
		springs.putConstraint(SpringLayout.EAST, miscLabel, 10, SpringLayout.WEST, versionLabel);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.revalidate();
	}

	public JPanel getStatusBar() {
		return statusBar;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setIndeterminateProgress() {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void stopIndeterminateProgress() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	protected void setMessage(String message) {
		setMessage(message, false);
	}

	protected synchronized void setMessage(String message, boolean disappearing) {
		messageLabel.setText(message);
		messageLabel.repaint();
		statusBar.revalidate();

		if (messageVoider != null && messageVoider.isAlive())
			messageVoider.interrupt();

		if (disappearing) {
			messageVoider = new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						setMessage("");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			};
			SwingUtilities.invokeLater(messageVoider);
		}
	}

	public JLabel getMiscLabel() {
		return miscLabel;
	}
}
