package de.unistuttgart.ims.coref.annotator.document;

import java.util.NoSuchElementException;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.uima.cas.text.AnnotationTree;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.RangedHashSetValuedHashMap;
import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationLengthComparator;

public class SegmentModel extends SubModel implements ListModel<Segment>, TreeModel {
	DocumentModel documentModel;
	@Deprecated
	MutableList<ListDataListener> listeners = Lists.mutable.empty();

	RangedHashSetValuedHashMap<Segment> position2Segment = new RangedHashSetValuedHashMap<Segment>();

	AnnotationTree<Segment> tree;
	Segment rootSegment;

	MutableList<TreeModelListener> treeModelListeners = Lists.mutable.empty();

	public SegmentModel(DocumentModel documentModel) {
		super(documentModel);
	}

	@Override
	protected void initializeOnce() {
		JCas jcas = getDocumentModel().getJcas();

		rootSegment = AnnotationFactory.createAnnotation(jcas, 0, jcas.getDocumentText().length(), Segment.class);
		tree = jcas.getAnnotationIndex(Segment.class).tree(rootSegment);
		for (Segment s : JCasUtil.select(jcas, Segment.class))
			position2Segment.add(s);

	}

	public ImmutableList<Segment> getTopLevelSegments() {
		return Lists.mutable.ofAll(tree.getRoot().getChildren()).collect(tn -> tn.get())
				.selectInstancesOf(Segment.class).toImmutable();
	}

	@Override
	public int getSize() {
		return tree.getRoot().getChildCount();
	}

	@Override
	public Segment getElementAt(int index) {
		return tree.getRoot().getChild(index).get();
	}

	public ImmutableList<Segment> getElementsAt(int index0, int index1) {
		return Lists.mutable.ofAll(tree.getRoot().getChildren()).subList(index0, index1).collect(n -> n.get())
				.selectInstancesOf(Segment.class).toImmutable();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
		if (!getTopLevelSegments().isEmpty())
			l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, getSize()));
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	@Override
	public Object getRoot() {
		return tree.getRoot();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof AnnotationTreeNode)
			return ((AnnotationTreeNode<Segment>) parent).getChild(index);
		throw new NoSuchElementException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof AnnotationTreeNode)
			return ((AnnotationTreeNode<Segment>) parent).getChildCount();
		throw new NoSuchElementException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isLeaf(Object node) {
		return ((AnnotationTreeNode<? extends Segment>) node).getChildCount() == 0;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		AnnotationTreeNode<Segment> par = ((AnnotationTreeNode<Segment>) parent);
		for (int i = 0; i < par.getChildCount(); i++) {
			if (par.getChild(i) == child)
				return i;
		}
		return -1;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	@SuppressWarnings("unchecked")
	public AnnotationTreeNode<Segment> getAnnotationTreeNode(Segment segment) {
		if (tree == null)
			return null;
		return (AnnotationTreeNode<Segment>) getAnnotationTreeNode(tree.getRoot(), segment);
	}

	public AnnotationTreeNode<? extends Segment> getAnnotationTreeNode(AnnotationTreeNode<? extends Segment> current,
			Segment segment) {
		AnnotationTreeNode<? extends Segment> r = null;
		if (current.get() == segment)
			return r = current;
		else {
			for (int i = 0; i < current.getChildCount(); i++) {
				AnnotationTreeNode<? extends Segment> rec = getAnnotationTreeNode(current.getChild(i), segment);
				if (rec != null)
					r = rec;

			}
		}
		return r;
	}

	public Segment getSegmentAt(int textPosition) {
		MutableSet<Segment> segments = position2Segment.get(textPosition);
		if (segments.isEmpty())
			return null;
		return Lists.mutable.ofAll(segments).min(new AnnotationLengthComparator<Segment>());
	}

	public Segment getRootSegment() {
		return rootSegment;
	}

	public TreePath getPathTo(Segment segment) {

		AnnotationTreeNode<Segment> node = getAnnotationTreeNode(segment);

		TreePath tp = new TreePath(getPathToRoot(node));
		return tp;
	}

	protected Object[] getPathToRoot(AnnotationTreeNode<Segment> s) {
		if (s == null)
			return new Object[] { tree.getRoot() };
		MutableList<Object> l = Lists.mutable.of(s);
		AnnotationTreeNode<Segment> cur = s;
		while (cur != tree.getRoot()) {
			cur = cur.getParent();
			l.add(0, cur);
		}
		return l.toArray(new Object[l.size()]);
	}

}
