package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.commons.io.IOUtils;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class HelpWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private static HelpWindow window = null;

	JTabbedPane tabbedPane;

	protected HelpWindow() {

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Index", new JScrollPane(load("docs/index")));
		tabbedPane.addTab("How to annotate", new JScrollPane(load("docs/howto")));
		tabbedPane.addTab("Compare annotations", new JScrollPane(load("docs/compare")));
		tabbedPane.addTab("Automatic processing", new JScrollPane(load("docs/processing")));
		tabbedPane.addTab("Input/Output", new JScrollPane(loadIOPlugins()));

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

	private JEditorPane loadIOPlugins() {
		JEditorPane textArea = new JEditorPane();
		textArea.setContentType("text/html");

		StringBuilder b = new StringBuilder();

		try {
			b.append(IOUtils.toString(getClass().getResourceAsStream("/docs/io-plugins.txt"), "UTF-8"));
		} catch (IOException e) {
			b.append("<html><head><title>Input/Output</title></head>");
			b.append("<body style=\"font-family:sans-serif;font-size:12pt;\"><h1>Input/Output</h1>");
		}

		for (Class<? extends IOPlugin> pluginClass : Annotator.app.getPluginManager().getIOPlugins()) {
			IOPlugin instance = Annotator.app.getPluginManager().getIOPlugin(pluginClass);

			b.append("<h2>").append(instance.getName()).append("</h2>");
			b.append("<p>Class name: <code>").append(pluginClass.getName()).append("</code></p>");
			if (instance.getDescription() == null)
				b.append("<p>No description.</p>");
			else
				b.append("<p>").append(instance.getDescription()).append("</p>");
		}
		b.append("</body></html>");

		textArea.setText(b.toString());
		textArea.setEditable(false);
		textArea.setPreferredSize(new Dimension(500, 500));

		return textArea;
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
