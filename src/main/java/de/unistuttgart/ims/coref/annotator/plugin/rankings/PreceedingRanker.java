package de.unistuttgart.ims.coref.annotator.plugin.rankings;

import java.util.Comparator;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.plugins.EntityRankingPlugin;

public class PreceedingRanker implements EntityRankingPlugin {

	@Override
	public String getDescription() {
		return getName();
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public MutableSortedSet<Entity> rank(Span span, CoreferenceModel cModel, JCas jcas) {
		return Sets.mutable
				.ofAll(JCasUtil.selectPreceding(Mention.class, new Annotation(jcas, span.begin, span.end), 5))
				.collect(m -> m.getEntity()).toSortedSet(new Comparator<Entity>() {
					@Override
					public int compare(Entity o1, Entity o2) {
						return cModel.getLabel(o1).compareTo(cModel.getLabel(o2));
					}
				});
	}

}
