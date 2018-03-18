package de.unistuttgart.ims.coref.annotator.plugin.rankings;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.plugins.EntityRankingPlugin;

public class MatchingRanker implements EntityRankingPlugin {
	Pattern pattern;

	@Override
	public String getDescription() {
		return getName();
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public MutableSortedSet<Entity> rank(Span potAnnotation, CoreferenceModel cModel, JCas jcas) {
		String s = jcas.getDocumentText().substring(potAnnotation.begin, potAnnotation.end);
		pattern = Pattern.compile(s);
		Comparator<Entity> comparator = new Comparator<Entity>() {

			@Override
			public int compare(Entity o1, Entity o2) {
				boolean b1 = matches(cModel, o1);
				boolean b2 = matches(cModel, o2);
				if (b1 == b2) {
					int comp = -1 * Integer.compare(cModel.get(o1).size(), cModel.get(o2).size());
					return (comp == 0 ? -1 * Util.toString(o1.getLabel()).compareTo(Util.toString(o2.getLabel()))
							: comp);
				}
				return (b1 ? -1 : 1);
			}
		};

		return SortedSets.mutable.ofAll(comparator, JCasUtil.select(jcas, Entity.class));

	}

	protected boolean wasRecent(JCas jcas, Entity o1, int begin) {
		for (Mention m : JCasUtil.selectPreceding(Mention.class, new Annotation(jcas, begin, begin), 20)) {
			if (m.getEntity() == o1)
				return true;
		}
		return false;
	}

	protected boolean matches(CoreferenceModel model, Entity e) {
		Matcher m;

		if (e.getLabel() != null) {
			m = pattern.matcher(e.getLabel());
			if (m.find())
				return true;
		}
		for (Mention child : model.get(e)) {
			String mc = ((Annotation) child).getCoveredText();
			m = pattern.matcher(mc);
			if (m.find())
				return true;

		}
		return false;

	}
}
