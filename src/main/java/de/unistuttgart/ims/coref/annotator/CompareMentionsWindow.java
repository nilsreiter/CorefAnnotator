package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.StyleContext;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCopier;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.worker.CoreferenceModelLoader;

public class CompareMentionsWindow extends JFrame implements TextWindow, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	JCas[] jcas = new JCas[2];
	JCas targetJCas;
	CoreferenceModel[] models = new CoreferenceModel[2];
	CoreferenceModel targetModel;
	Annotator mainApplication;
	JTextPane textPane;
	StyleContext styleContext = new StyleContext();

	boolean textIsSet = false;
	int loadedJCas = 0;

	AbstractAction copyAction;

	HighlightManager highlightManager;

	public CompareMentionsWindow(Annotator mainApplication) throws UIMAException {
		this.mainApplication = mainApplication;
		this.initialiseWindow();
		this.targetJCas = JCasFactory.createJCas();
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
		textPane.addMouseListener(new TextMouseListener());
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
		CasCopier.copyCas(jcas.getCas(), targetJCas.getCas(), true, true);
		targetModel = new CoreferenceModel(targetJCas, mainApplication.getPreferences());
		targetModel.addCoreferenceModelListener(this);
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

	class TextMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				JPopupMenu pMenu = new JPopupMenu();
				int offset = textPane.viewToModel(e.getPoint());

				for (int i = 0; i < models.length; i++) {
					MutableList<Annotation> localAnnotations = Lists.mutable.withAll(models[i].getMentions(offset));
					MutableList<Annotation> mentions = localAnnotations
							.select(m -> m instanceof Mention || m instanceof DetachedMentionPart);
					for (Annotation m : mentions) {
						Mention mention = null;
						if (m instanceof Mention) {
							mention = (Mention) m;
						} else if (m instanceof DetachedMentionPart) {
							mention = ((DetachedMentionPart) m).getMention();
						}
						if (mention != null)
							pMenu.add(getMentionMenu(mention, String.valueOf(i)));
					}

				}
				pMenu.show(e.getComponent(), e.getX(), e.getY());

			}
		}

		protected JMenu getMentionMenu(Mention mention, String source) {
			JMenu menu = new JMenu(mention.getCoveredText() + "(" + source + ")");
			menu.add(new CopyMentionAction(new Span(mention)));
			return menu;
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	@Override
	public void annotationAdded(Annotation m) {
		highlightManager.underline(m, Color.green);
	}

	@Override
	public void annotationChanged(Annotation m) {
		highlightManager.underline(m, Color.green);

	}

	@Override
	public void annotationRemoved(Annotation m) {
		highlightManager.undraw(m);

	}

	@Override
	public void entityAdded(Entity entity) {
	}

	@Override
	public void entityRemoved(Entity entity) {
	}

	class CopyMentionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		Span span;

		public CopyMentionAction(Span span) {
			this.span = span;
			putValue(Action.NAME, "add");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			targetModel.add(span.begin, span.end);
		}

	}
}
