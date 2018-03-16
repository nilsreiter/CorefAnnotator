package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.action.CopyAction;

public class CompareMentionsWindow extends JFrame implements TextWindow {

	private static final long serialVersionUID = 1L;

	JCas[] jcas = new JCas[2];
	Annotator mainApplication;
	JTextPane textPane;
	boolean textIsSet = false;

	AbstractAction copyAction;

	HighlightManager highlightManager;

	public CompareMentionsWindow(Annotator mainApplication) {
		this.mainApplication = mainApplication;
		this.initialiseWindow();
	}

	protected void initialiseActions() {
		copyAction = new CopyAction(this, mainApplication);
	}

	protected void initialiseText(JCas jcas2) {
		textPane.setText(jcas2.getDocumentText());
		textIsSet = true;
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
		textPane.setText(null);
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				copyAction);
		add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		highlightManager = new HighlightManager(textPane);
		setPreferredSize(new Dimension(800, 800));
		pack();
	}

	public void setJCasLeft(JCas jcas) {
		this.jcas[0] = jcas;
		if (!textIsSet)
			initialiseText(jcas);
	}

	public void setJCasRight(JCas jcas) {
		this.jcas[1] = jcas;
		if (!textIsSet)
			initialiseText(jcas);
	}

	@Override
	public String getText() {
		return textPane.getText();
	}

	@Override
	public Span getSelection() {
		return new Span(textPane.getSelectionStart(), textPane.getSelectionEnd());
	}

}
