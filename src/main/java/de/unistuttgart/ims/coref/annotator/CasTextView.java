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
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.commons.Counter;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CasTextView extends JPanel implements LoadingListener, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTextPane textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	StyleContext styleContext = new StyleContext();
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	Counter<Span> spanCounter = new Counter<Span>();

	Object selectionHighlight = null;

	public CasTextView(DocumentWindow dw) {
		super(new BorderLayout());
		this.hilit = new DefaultHighlighter();
		this.documentWindow = dw;
		this.textPane = new JTextPane();
		// this.textPane.setWrapStyleWord(true);
		// this.textPane.setLineWrap(true);
		this.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setTransferHandler(new TextViewTransferHandler(this));
		// textPane.setFont(textPane.getFont().deriveFont(0, 13));
		textPane.setHighlighter(hilit);

		add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);

	}

	protected void initStyles() {
		Style speakerTagStyle = styleContext.addStyle("Speaker", null);
		speakerTagStyle.addAttribute(StyleConstants.Bold, true);
	}

	public CAS getCAS() {
		return documentWindow.getJcas().getCas();
	}

	public JCas getJCas() {
		return documentWindow.getJcas();
	}

	public void drawAnnotation(Mention a) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (hi != null) {
			hilit.removeHighlight(hi);
			spanCounter.subtract(span);
		}
		try {
			int n = spanCounter.get(span);
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(),
					new UnderlinePainter(new Color(a.getEntity().getColor()), n * 3));
			spanCounter.add(span);
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
			// drawAnnotations();
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {

			if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {

			}
			return false;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}
			JTextComponent.DropLocation dl = (javax.swing.text.JTextComponent.DropLocation) info.getDropLocation();
			dl.getDropPoint();

			int index = dl.getIndex();
			System.err.println(index);
			return false;
		}
	}

	public DocumentWindow getDocumentWindow() {
		return documentWindow;
	}

	@Override
	public void jcasLoaded(JCas jcas) {
		/*
		 * StringContent c = new StringContent(jcas.getDocumentText().length());
		 * try { c.insertString(0, jcas.getDocumentText()); } catch
		 * (BadLocationException e) { e.printStackTrace(); } JCasDocument
		 * document = new JCasDocument(jcas);
		 * textPane.setStyledDocument(document);
		 */
		textPane.setStyledDocument(new DefaultStyledDocument(styleContext));
		textPane.setText(jcas.getDocumentText().replaceAll("\r", " "));

	}

	@Override
	public void modelCreated(CoreferenceModel model, DocumentWindow dw) {
		textPane.addKeyListener(model);
		textPane.setCaretPosition(0);
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
	public void mentionRemoved(Mention m) {
		hilit.removeHighlight(highlightMap.get(m));
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

	public void switchStyle(JCas jcas, StyleVariant sv) {
		sv.style(jcas, textPane.getStyledDocument(), styleContext);

	}

}
