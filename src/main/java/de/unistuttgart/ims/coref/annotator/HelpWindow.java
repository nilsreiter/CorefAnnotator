package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class HelpWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private static HelpWindow window = null;

	JTabbedPane tabbedPane;

	protected HelpWindow() {

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Index", new JScrollPane(load("docs/index")));
		tabbedPane.addTab("How to annotate", new JScrollPane(load("docs/howto")));
		tabbedPane.addTab("Compare annotations", new JScrollPane(load("docs/compare")));

		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		this.pack();
		this.setLocationRelativeTo(null);

	}

	private JEditorPane load(String path) {
		JEditorPane textArea;
		try {
			if (!path.endsWith(".html"))
				path += ".html";

			URL url = getClass().getClassLoader().getResource(path);
			textArea = new JEditorPane(url);
			textArea.setContentType("text/html");
			textArea.setEditable(false);
			textArea.setPreferredSize(new Dimension(500, 500));
			return textArea;
		} catch (IOException e) {
			Annotator.logger.catching(e);
		}
		return null;
	}

	public static HelpWindow getHelpWindow() {
		if (window == null) {
			window = new HelpWindow();
		}
		return window;
	}

	public static HelpWindow show(String key) {
		HelpWindow hw = getHelpWindow();
		hw.setVisible(true);
		if (key.equalsIgnoreCase("index"))
			hw.tabbedPane.setSelectedIndex(0);
		return hw;
	}
}
