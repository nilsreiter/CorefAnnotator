package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.apache.uima.util.FileUtils;

public class LogWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public LogWindow() {
		JTextArea textArea = new JTextArea();
		Dimension d = new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		d.height -= 100;
		textArea.setMaximumSize(d);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		try {
			File logFile = new File(System.getProperty("user.home"), "..CorefAnnotator.log");
			if (logFile.exists() && logFile.canRead()) {
				String log = FileUtils.file2String(logFile);
				textArea.setText(log);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		textArea.setEditable(false);

		this.add(scroll, BorderLayout.CENTER);
		this.pack();
	}
}
