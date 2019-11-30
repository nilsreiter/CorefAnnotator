package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.Span.ExtendedSpan;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class MultiDocumentModel implements Model, ICoreferenceModel {
	MutableList<DocumentModel> documentModels = Lists.mutable.empty();
	MutableList<CoreferenceModelListener> crModelListeners = Lists.mutable.empty();
	MutableList<Mention> intersection;

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		e.entityEvent(Event.get(this, Event.Type.Init));
		return crModelListeners.add(e);
	}

	public boolean addDocumentModel(DocumentModel e) {
		return documentModels.add(e);
	}

	public MutableList<DocumentModel> getDocumentModels() {
		return documentModels;
	}

	public DocumentModel removeDocumentModel(int index) {
		return documentModels.remove(index);
	}

	public boolean removeDocumentModel(Object o) {
		return documentModels.remove(o);
	}

	public void initialize() {
		MutableMap<DocumentModel, MutableMap<Span, Mention>> spanMentionMap = Maps.mutable.empty();
		MutableSet<Span> spanIntersection = null;
		for (DocumentModel dm : documentModels) {
			spanMentionMap.put(dm, Maps.mutable.empty());
			JCas jcas = dm.getJcas();
			MutableSet<Span> spans = Sets.mutable.empty();
			Span annotatedRange = new Span(Integer.MAX_VALUE, Integer.MIN_VALUE);

			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				if (Annotator.app.getPreferences().getBoolean(Constants.CFG_IGNORE_SINGLETONS_WHEN_COMPARING,
						Defaults.CFG_IGNORE_SINGLETONS_WHEN_COMPARING)
						&& dm.getCoreferenceModel().getSingletons().contains(m.getEntity()))
					continue;
				Span span;
				if (Annotator.app.getPreferences().getBoolean(Constants.CFG_COMPARE_BY_ENTITY_NAME,
						Defaults.CFG_COMPARE_BY_ENTITY_NAME))
					span = new ExtendedSpan(m);
				else
					span = new Span(m);

				spanMentionMap.get(dm).put(span, m);
				spans.add(span);

				if (m.getEnd() > annotatedRange.end)
					annotatedRange.end = m.getEnd();
				if (m.getBegin() < annotatedRange.begin)
					annotatedRange.begin = m.getBegin();

			}
			if (spanIntersection == null)
				spanIntersection = Sets.mutable.withAll(spans);
			else
				spanIntersection.intersect(spans);
		}
		try {
			intersection = spanIntersection.flatCollect(span -> {
				return spanMentionMap.collect(map -> map.get(span));
			}).toList();
		} catch (NullPointerException e) {
			Annotator.logger.catching(e);
		}

	}

	public ImmutableList<? extends Annotation> getIntersection() {
		return intersection.toImmutable();

	};

	public ImmutableMap<String, Iterable<Annotation>> getAnnotations(int position) {
		// TODO: implement
		return null;
	}

	@Override
	public ImmutableSortedSet<Mention> getMentions() {
		return documentModels.flatCollect(dm -> dm.getCoreferenceModel().getMentions())
				.toSortedSet(new AnnotationComparator()).toImmutable();
	}

}
