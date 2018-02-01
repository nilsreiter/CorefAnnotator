package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CasTextView extends JPanel {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTextArea textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	CoreferenceModel cModel;

	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();

	public CasTextView(DocumentWindow dw) {
		super(new BorderLayout());
		this.documentWindow = dw;
		this.cModel = new CoreferenceModel(documentWindow.getJcas(), this);
		this.textPane = new JTextArea();
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setLineWrap(true);
		textPane.setWrapStyleWord(true);
		textPane.setSize(400, 600);
		textPane.setTransferHandler(new TextViewTransferHandler(this));
		textPane.setText(dw.getJcas().getCas().getDocumentText());
		textPane.addKeyListener(cModel);
		hilit = new DefaultHighlighter();
		textPane.setHighlighter(hilit);
		add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
		setVisible(true);
	}

	public CAS getCAS() {
		return documentWindow.getJcas().getCas();
	}

	public JCas getJCas() {
		return documentWindow.getJcas();
	}

	public void drawAnnotation(Mention a) {
		Object hi = highlightMap.get(a);
		if (hi != null)
			hilit.removeHighlight(hi);
		try {
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(), cModel.getPainter(a.getEntity()));
			highlightMap.put(a, hi);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JTextArea getTextPane() {
		return textPane;
	}

	public void drawAnnotations() {
		hilit.removeAllHighlights();
		highlightMap.clear();
		for (Mention m : JCasUtil.select(getJCas(), Mention.class)) {
			try {
				Object o = hilit.addHighlight(m.getBegin(), m.getEnd(), cModel.getPainter(m.getEntity()));
				highlightMap.put(m, o);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		}
	}

	class TextViewTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		CasTextView textView;

		public TextViewTransferHandler(CasTextView tv) {
			textView = tv;
		}

		@Override
		public int getSourceActions(JComponent comp) {
			return TransferHandler.LINK;
		}

		@Override
		public Transferable createTransferable(JComponent comp) {
			JTextArea t = (JTextArea) comp;
			return new PotentialAnnotationTransfer(textView, t.getSelectionStart(), t.getSelectionEnd());
		}

		@Override
		protected void exportDone(JComponent c, Transferable t, int action) {
			// TODO: export an Annotation object
			drawAnnotations();
		}
	}

	public DocumentWindow getDocumentWindow() {
		return documentWindow;
	}
}
