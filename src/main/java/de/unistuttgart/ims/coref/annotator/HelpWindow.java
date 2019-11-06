package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

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

	private static final long serialVersionUID = 1L;

	private static HelpWindow window = null;
	HelpTopic[] topics;

	JList<String> topicList;

	protected HelpWindow() {
		topics = new HelpTopic[] { new HelpTopic("Index", new JScrollPane(load("docs/index"))),
				new HelpTopic("How to annotate", new JScrollPane(load("docs/howto"))),
				new HelpTopic("Compare annotations", new JScrollPane(load("docs/compare"))),
				new HelpTopic("Automatic processing", new JScrollPane(load("docs/processing"))),
				new HelpTopic("Flag editing", new JScrollPane(load("docs/flags"))),
				new HelpTopic("Input/Output", new JScrollPane(loadIOPlugins())) };

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
			hw.topicList.setSelectedIndex(0);
		else
			for (int i = 0; i < hw.topics.length; i++)
				if (hw.topics[i].getTitle().equalsIgnoreCase(key))
					hw.topicList.setSelectedIndex(i);
		return hw;
	}

	public static class HelpTopic {
		String title;
		Component panel;

		public HelpTopic(String title, Component panel) {
			super();
			this.title = title;
			this.panel = panel;
		}

		public String getTitle() {
			return title;
		}

		public Component getPanel() {
			return panel;
		}

	}
}
