package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.MutableSet;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.api.v1.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public abstract class AbstractTextWindow extends AbstractWindow implements HasTextView, CoreferenceModelListener {

	public class TOCSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			Object src = e.getSource();

			if (src != AbstractTextWindow.this) {
				TreePath tp = e.getNewLeadSelectionPath();
				@SuppressWarnings("unchecked")
				AnnotationTreeNode<Segment> tn = (AnnotationTreeNode<Segment>) tp.getLastPathComponent();
				try {
					int position = UimaUtil.nextCharacter(getJCas(), tn.get().getBegin(),
							ch -> !ArrayUtils.contains(new char[] { ' ', '\n', '\t', '\f', '\r' }, ch));

					Rectangle rect = textPane.modelToView2D(position).getBounds();

					// this is a bit experimental
					rect.height = rect.height * 2;// (textPane.getParent().getHeight() / 2);

					textScrollPane.getViewport().setViewPosition(rect.getLocation());

				} catch (BadLocationException e1) {
					Annotator.logger.catching(e1);
				}
			}
		}

	}

	public class TOCRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (value instanceof AnnotationTreeNode) {
				AnnotationTreeNode<Segment> atn = (AnnotationTreeNode<Segment>) value;
				setText(atn.get().getLabel());
			}
			if (leaf)
				setIcon(FontIcon.of(MaterialDesign.MDI_MINUS));
			else if (expanded)
				setIcon(FontIcon.of(FontAwesome.FOLDER_OPEN_O));
			else
				setIcon(FontIcon.of(FontAwesome.FOLDER_O));
			return this;
		}

	}

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	HighlightManager highlightManager;
	JTextPane textPane;
	JTree tableOfContents;
	JScrollPane textScrollPane;
	JPanel textPanel;

	LineNumberStyle lineNumberStyle;

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addStyleChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removeStyleChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public enum LineNumberStyle {
		NONE, FIXED, DYNAMIC
	}

	@Override
	public String getText() {
		return getJCas().getDocumentText();
	}

	@Override
	public JCas getJCas() {
		return documentModel.getJcas();
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		switch (eventType) {
		case Add:
			entityEventAdd(event);
			break;
		case Remove:
			entityEventRemove(event);
			break;
		case Update:
			entityEventUpdate(event);
			break;
		case Move:
			entityEventMove(event);
			break;
		case Merge:
			entityEventMerge(event);
			break;
		case Op:
			entityEventOp(event);
			break;
		case Init:
			entityEventInit(event);
		default:
		}
	}

	protected void entityEventAdd(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention || fs instanceof DetachedMentionPart) {
				highlightManager.underline((Annotation) fs);
			} else if (fs instanceof CommentAnchor) {
				highlightManager.highlight((Annotation) fs);
			}
		}
	}

	protected void entityEventRemove(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention) {
				if (((Mention) fs).getDiscontinuous() != null)
					highlightManager.unUnderline(((Mention) fs).getDiscontinuous());
				highlightManager.unUnderline((Annotation) fs);
			} else if (fs instanceof Annotation)
				highlightManager.unUnderline((Annotation) fs);

		}
	}

	protected void entityEventUpdate(FeatureStructureEvent event) {
		for (FeatureStructure fs : event) {
			if (fs instanceof Mention) {
				if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
					highlightManager.unUnderline((Annotation) fs);
				else
					highlightManager.underline((Annotation) fs);
			}
		}
	}

	protected void entityEventMove(FeatureStructureEvent event) {
		for (FeatureStructure fs : event) {
			if (fs instanceof Mention) {
				if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
					highlightManager.unUnderline((Annotation) fs);
				else
					highlightManager.underline((Annotation) fs);
			}
		}
	}

	protected void entityEventMerge(FeatureStructureEvent event) {

	}

	protected void entityEventOp(FeatureStructureEvent event) {

	}

	protected void entityEventInit(FeatureStructureEvent event) {
		CoreferenceModel cm = (CoreferenceModel) event.getSource();
		for (Mention m : cm.getMentions()) {
			highlightManager.underline(m);
			if (m.getDiscontinuous() != null)
				highlightManager.underline(m.getDiscontinuous());
		}
	}

	public <T extends Annotation> MutableSet<T> getSelectedAnnotations(Class<T> clazz) {
		MutableSet<Annotation> annotations = getDocumentModel().getCoreferenceModel()
				.getMentions(getTextPane().getSelectionStart())
				.select(a -> a.getBegin() == getTextPane().getSelectionStart()
						&& a.getEnd() == getTextPane().getSelectionEnd());
		return annotations.selectInstancesOf(clazz);
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public LineNumberStyle getLineNumberStyle() {
		return lineNumberStyle;
	}

	public void setLineNumberStyle(LineNumberStyle lineNumberStyle) {
		this.lineNumberStyle = lineNumberStyle;
	}

	protected void highlightSegmentInTOC(int textPosition) {
		if (documentModel != null && textPosition >= 0) {
			Segment segment = documentModel.getSegmentModel().getSegmentAt(textPosition);
			if (segment != null) {
				TreePath tp = documentModel.getSegmentModel().getPathTo(segment);
				TreeSelectionListener[] tsls = tableOfContents.getTreeSelectionListeners();
				for (TreeSelectionListener tsl : tsls) {
					tableOfContents.removeTreeSelectionListener(tsl);
				}
				tableOfContents.scrollPathToVisible(tp);
				tableOfContents.setSelectionPath(tp);
				for (TreeSelectionListener tsl : tsls) {
					tableOfContents.addTreeSelectionListener(tsl);
				}
			}
		}

	}

	@Override
	protected void initializeWindow() {
		super.initializeWindow();

		textPane = new JTextPane();

		textScrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JViewport vp = (JViewport) e.getSource();
				Point point = vp.getViewPosition();

				int textPosition = textPane.viewToModel2D(point);
				highlightSegmentInTOC(textPosition);
			}

		});
		tableOfContents = new JTree(new Object[] {});
		tableOfContents.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tableOfContents.setCellRenderer(new TOCRenderer());
		tableOfContents.setRootVisible(false);
		tableOfContents.addTreeSelectionListener(new TOCSelectionListener());
		tableOfContents.setToggleClickCount(2);
		tableOfContents.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 6));

		textPanel = new JPanel(new BorderLayout());
		textPanel.add(textScrollPane, BorderLayout.CENTER);
		textPanel.add(new JScrollPane(tableOfContents), BorderLayout.WEST);

	}

}
