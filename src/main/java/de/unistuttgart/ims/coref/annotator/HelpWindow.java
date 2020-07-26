package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.IOUtils;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class HelpWindow extends JFrame {

	public static class Topic {
		public static final HelpTopic INDEX = topics[0];
		public static final HelpTopic HOWTO = topics[1];
		public static final HelpTopic COMPARE = topics[2];
		public static final HelpTopic PROCESSING = topics[3];
		public static final HelpTopic FLAGS = topics[4];
		public static final HelpTopic IO = topics[5];
		public static final HelpTopic SEARCH = topics[6];
		public static final HelpTopic PREFERENCES = topics[7];
	}

	private static final long serialVersionUID = 1L;

	private static HelpWindow window = null;
	static HelpTopic[] topics = new HelpTopic[] { new HelpTopic("Index", "index"),
			new HelpTopic("How to annotate", "howto"), new HelpTopic("Compare annotations", "compare"),
			new HelpTopic("Automatic processing", "processing"), new HelpTopic("Flag editing", "flags"),
			new HelpTopic("Input/Output", "io", s -> new JScrollPane(loadIOPlugins())),
			new HelpTopic("Search", "search"), new HelpTopic("Preferences", "preferences") };

	JList<String> topicList;

	protected HelpWindow() {

		JPanel topicArea = new JPanel();

		DefaultListModel<String> topicListModel = new DefaultListModel<String>();
		for (HelpTopic ht : topics)
			topicListModel.addElement(ht.getTitle());

		topicList = new JList<String>(topicListModel);
		topicList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Topics"));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topicList, topicArea);
		topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topicList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					splitPane.setRightComponent(topics[topicList.getSelectedIndex()].getPanel());
				}
			}

		});

		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		topicList.setSelectedIndex(0);
		this.pack();
		this.setLocationRelativeTo(null);

	}

	protected static Component load(String path) {
		JEditorPane textArea;
		try {
			if (!path.endsWith(".html"))
				path += ".html";

			URL url = HelpWindow.class.getClassLoader().getResource(path);
			textArea = new JEditorPane(url);
			textArea.setContentType("text/html");
			textArea.setEditable(false);
			textArea.setPreferredSize(new Dimension(500, 500));
			textArea.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().browse(e.getURL().toURI());
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			});
			return new JScrollPane(textArea);
		} catch (IOException e) {
			Annotator.logger.catching(e);
		}
		return null;
	}

	protected static JEditorPane loadIOPlugins() {
		JEditorPane textArea = new JEditorPane();
		textArea.setContentType("text/html");

		StringBuilder b = new StringBuilder();

		try {
			b.append(IOUtils.toString(HelpWindow.class.getResourceAsStream("/docs/io-plugins.txt"), "UTF-8"));
		} catch (IOException e) {
			b.append("<html><head><title>Input/Output</title></head>");
			b.append("<body style=\"font-family:sans-serif;font-size:12pt;\"><h1>Input/Output</h1>");
		}

		for (IOPlugin instance : Annotator.app.getPluginManager().getIOPluginObjects()) {

			b.append("<h2>").append(instance.getName()).append("</h2>");
			b.append("<p>Class name: <code>").append(instance.getClass().getName()).append("</code></p>");
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

	public static HelpWindow show(HelpTopic topic) {
		HelpWindow hw = getHelpWindow();
		hw.setVisible(true);
		if (topic.getKey().equalsIgnoreCase("index"))
			hw.topicList.setSelectedIndex(0);
		else
			for (int i = 0; i < topics.length; i++)
				if (topics[i].getTitle().equalsIgnoreCase(topic.getKey()))
					hw.topicList.setSelectedIndex(i);
		return hw;
	}

	public static HelpWindow show(String key) {
		HelpWindow hw = getHelpWindow();
		hw.setVisible(true);
		if (key.equalsIgnoreCase("index"))
			hw.topicList.setSelectedIndex(0);
		else
			for (int i = 0; i < topics.length; i++)
				if (topics[i].getKey().equalsIgnoreCase(key))
					hw.topicList.setSelectedIndex(i);
		return hw;
	}

	public static class HelpTopic {
		String key;
		String title;
		Component panel = null;
		Function<String, Component> componentLoader;

		@Deprecated
		public HelpTopic(String title, Component panel) {
			super();
			this.title = title;
			this.panel = panel;
		}

		public HelpTopic(String title, String key) {
			this(title, key, k -> load("docs/" + k));
		}

		public HelpTopic(String title, String key, Function<String, Component> func) {
			this.title = title;
			this.key = key;
			this.componentLoader = func;
		}

		public String getTitle() {
			return title;
		}

		public Component getPanel() {
			if (panel == null) {
				panel = componentLoader.apply(key);
			}
			return panel;
		}

		public String getKey() {
			return key;
		}

	}
}
