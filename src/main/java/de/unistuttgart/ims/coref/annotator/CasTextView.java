package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CasTextView extends JPanel implements LoadingListener, CoreferenceModelListener, TreeModelListener {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTextPane textPane;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();

	public CasTextView(DocumentWindow dw) {
		super(new BorderLayout());
		this.hilit = new DefaultHighlighter();
		this.documentWindow = dw;
		this.textPane = new JTextPane();
		textPane.setDragEnabled(true);
		textPane.setEditable(false);
		textPane.setSize(400, 600);
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

	@Override
	public void jcasLoaded(JCas jcas) {
		textPane.setEditable(true);
		textPane.setText(jcas.getDocumentText());
		textPane.setEditable(false);
	}

	@Override
	public void modelCreated(CoreferenceModel model) {
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
	public void treeNodesChanged(TreeModelEvent e) {

		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

}
