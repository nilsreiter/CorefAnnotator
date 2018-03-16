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
import de.unistuttgart.ims.coref.annotator.worker.CoreferenceModelLoader;

public class CompareMentionsWindow extends JFrame implements TextWindow {

	private static final long serialVersionUID = 1L;

	JCas[] jcas = new JCas[2];
	CoreferenceModel[] models = new CoreferenceModel[2];
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

	protected synchronized void initialiseText(JCas jcas2) {
		if (textIsSet)
			return;
		textPane.setText(jcas2.getDocumentText());
		textPane.setCaretPosition(0);
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
		new CoreferenceModelLoader(cm -> setCoreferenceModelRight(cm), jcas).execute();
	}

	public void setJCasRight(JCas jcas) {
		this.jcas[1] = jcas;
		if (!textIsSet)
			initialiseText(jcas);
		new CoreferenceModelLoader(cm -> setCoreferenceModelLeft(cm), jcas).execute();
	}

	public void setCoreferenceModelLeft(CoreferenceModel cm) {
		models[0] = cm;
	}

	public void setCoreferenceModelRight(CoreferenceModel cm) {
		models[1] = cm;
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
