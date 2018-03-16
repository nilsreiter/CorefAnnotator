package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.apache.uima.jcas.JCas;

public class CompareMentionsWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	JCas[] jcas = new JCas[2];
	Annotator mainApplication;
	JTextPane textPane;

	AbstractAction copyAction;

	HighlightManager highlightManager;

	public CompareMentionsWindow(Annotator mainApplication) {
		this.mainApplication = mainApplication;
		this.initialiseWindow();
	}

	protected void initialiseActions() {
		// copyAction = new CopyAction();
	}

	protected void initialiseWindow() {
		Caret caret = new Caret();

		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setCaret(caret);
		textPane.getCaret().setVisible(true);
		textPane.addFocusListener(caret);
		textPane.setCaretPosition(0);
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				copyAction);
		add(textPane, BorderLayout.CENTER);

		highlightManager = new HighlightManager(textPane);
		setPreferredSize(new Dimension(800, 800));
		pack();

	}

	public void setJCasLeft(JCas jcas) {
		this.jcas[0] = jcas;
	}

	public void setJCasRight(JCas jcas) {
		this.jcas[1] = jcas;
	}

}
