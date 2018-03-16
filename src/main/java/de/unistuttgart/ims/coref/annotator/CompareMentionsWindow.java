package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.StyleContext;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.worker.CoreferenceModelLoader;

public class CompareMentionsWindow extends JFrame implements TextWindow {

	private static final long serialVersionUID = 1L;

	JCas[] jcas = new JCas[2];
	CoreferenceModel[] models = new CoreferenceModel[2];
	Annotator mainApplication;
	JTextPane textPane;
	StyleContext styleContext = new StyleContext();

	boolean textIsSet = false;
	int loadedJCas = 0;

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

		StyleManager.styleCharacter(textPane.getStyledDocument(), StyleManager.getDefaultCharacterStyle());
		StyleManager.styleParagraph(textPane.getStyledDocument(), StyleManager.getDefaultParagraphStyle());

		drawAllAnnotations();
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
		loadedJCas++;
		if (!textIsSet)
			initialiseText(jcas);
		drawAllAnnotations();
		new CoreferenceModelLoader(cm -> setCoreferenceModelRight(cm), jcas).execute();
	}

	public void setJCasRight(JCas jcas) {
		this.jcas[1] = jcas;
		loadedJCas++;
		if (!textIsSet)
			initialiseText(jcas);
		drawAllAnnotations();
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

	protected void drawAllAnnotations() {
		if (loadedJCas < 2)
			return;
		MutableMap<Span, Mention> map1 = Maps.mutable.empty();
		for (Mention m : JCasUtil.select(jcas[0], Mention.class)) {
			map1.put(new Span(m), m);
		}
		MutableMap<Span, Mention> map2 = Maps.mutable.empty();
		for (Mention m : JCasUtil.select(jcas[1], Mention.class)) {
			map2.put(new Span(m), m);
		}

		System.err.println(map1.keySet());
		System.err.println(map2.keySet());

		for (Span s : map1.keySet()) {
			if (map2.containsKey(s)) {
				highlightManager.underline(map1.get(s), Color.gray.brighter());
			} else {
				highlightManager.underline(map1.get(s), Color.red);
			}
		}

		for (Span s : map2.keySet()) {
			if (!map1.containsKey(s)) {
				highlightManager.underline(map2.get(s), Color.blue);
			}
		}
	}

	protected void drawAll(JCas jcas, Color color) {
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			highlightManager.underline(m, color);

		}

	};

}
