package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.text.JTextComponent;

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CasTextView extends JPanel implements LoadingListener, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTextArea textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();

	Object selectionHighlight = null;

	public CasTextView(DocumentWindow dw) {
		super(new BorderLayout());
		this.hilit = new DefaultHighlighter();
		this.documentWindow = dw;
		this.textPane = new JTextArea();
		this.textPane.setWrapStyleWord(true);
		this.textPane.setLineWrap(true);
		this.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setTransferHandler(new TextViewTransferHandler(this));

		textPane.setFont(textPane.getFont().deriveFont(0, 13));
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
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(),
					new UnderlinePainter(new Color(a.getEntity().getColor())));
			highlightMap.put(a, hi);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JTextComponent getTextPane() {
		return textPane;
	}

	public void drawAnnotations() {
		hilit.removeAllHighlights();
		highlightMap.clear();
		for (Mention m : JCasUtil.select(getJCas(), Mention.class)) {
			drawAnnotation(m);
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
			JTextComponent t = (JTextComponent) comp;
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

	@Override
	public void jcasLoaded(JCas jcas) {
		textPane.setEditable(true);
		textPane.setText(jcas.getDocumentText());
		textPane.setEditable(false);
	}

	@Override
	public void modelCreated(CoreferenceModel model, DocumentWindow dw) {
		textPane.addKeyListener(model);

	}

	@Override
	public void mentionAdded(Mention m) {
		drawAnnotation(m);
	}

	@Override
	public void mentionChanged(Mention m) {
		drawAnnotation(m);
	}

	@Override
	public void mentionSelected(Mention m) {
		if (m != null)
			textPane.setCaretPosition(m.getEnd());
		try {
			if (selectionHighlight != null)
				hilit.removeHighlight(selectionHighlight);
			if (m != null)
				selectionHighlight = hilit.addHighlight(m.getBegin(), m.getEnd(),
						new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
