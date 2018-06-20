package de.unistuttgart.ims.coref.annotator.document;

import java.util.Collection;
import java.util.Map;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class SegmentModel implements ListModel<Segment> {
	DocumentModel documentModel;
	ImmutableList<Segment> topLevelSegments = null;
	MutableList<ListDataListener> listeners = Lists.mutable.empty();

	public SegmentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
		this.loadJCas(documentModel.getJcas());
	}

	protected void loadJCas(JCas jcas) {
		Map<Segment, Collection<Segment>> index = JCasUtil.indexCovering(jcas, Segment.class, Segment.class);

		topLevelSegments = Lists.mutable.withAll(JCasUtil.select(jcas, Segment.class)).reject(s -> index.containsKey(s))
				.toSortedList(new AnnotationComparator()).toImmutable();
	}

	public ImmutableList<Segment> getTopLevelSegments() {
		return topLevelSegments;
	}

	@Override
	public int getSize() {
		return topLevelSegments.size();
	}

	@Override
	public Segment getElementAt(int index) {
		return topLevelSegments.get(index);
	}

	public ImmutableList<Segment> getElementsAt(int index0, int index1) {
		return topLevelSegments.subList(index0, index1);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
		if (!topLevelSegments.isEmpty())
			l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, getSize() - 1));
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
}
