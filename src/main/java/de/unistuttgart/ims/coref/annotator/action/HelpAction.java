package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class HelpAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public HelpAction() {
		putValue(Action.NAME, Annotator.getString("action.help"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			URL url = getClass().getClassLoader().getResource("docs/index.html");
			JFrame helpFrame = new JFrame();

			JEditorPane textArea = new JEditorPane(url);
			textArea.setContentType("text/html");
			textArea.setEditable(false);
			textArea.setPreferredSize(new Dimension(500, 500));

			helpFrame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
			helpFrame.pack();
			helpFrame.setLocationRelativeTo(null);
			helpFrame.setVisible(true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
