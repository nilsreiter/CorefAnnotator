package de.unistuttgart.ims.coref.annotator.plugin.creta.webanno;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import webanno.custom.CRETALink;
import webanno.custom.CRETAMention;

public class ImportCRETA extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		Map<CRETAMention, Mention> mMap = new HashMap<CRETAMention, Mention>();
		HashMap<Mention, HashSet<Mention>> combiner = new HashMap<Mention, HashSet<Mention>>();
		HashMap<Set<Mention>, Set<Set<Mention>>> groups = new HashMap<Set<Mention>, Set<Set<Mention>>>();
		Map<Set<Mention>, Entity> entityMap = new HashMap<Set<Mention>, Entity>();

		for (CRETAMention cm : JCasUtil.select(aJCas, CRETAMention.class)) {
			Mention m = AnnotationFactory.createAnnotation(aJCas, cm.getBegin(), cm.getEnd(), Mention.class);
			mMap.put(cm, m);
			combiner.put(m, new HashSet<Mention>());
			combiner.get(m).add(m);
		}

		for (CRETALink link : JCasUtil.select(aJCas, CRETALink.class)) {
			if (link.getLinkArt() == null || link.getLinkArt().equalsIgnoreCase("Koreferenz")) {
				Mention m1 = mMap.get(link.getGovernor());
				Mention m2 = mMap.get(link.getDependent());

				Collection<Mention> old2 = combiner.get(m2);

				combiner.get(m1).addAll(old2);
				for (Mention m2c : old2) {
					combiner.put(m2c, combiner.get(m1));
				}
			}
		}

		for (CRETALink link : JCasUtil.select(aJCas, CRETALink.class)) {
			if (link.getLinkArt() != null && link.getLinkArt().equalsIgnoreCase("Gruppe")) {
				Mention ind = mMap.get(link.getGovernor());
				Mention grp = mMap.get(link.getDependent());
				if (!groups.containsKey(combiner.get(grp)))
					groups.put(combiner.get(grp), new HashSet<Set<Mention>>());
				groups.get(combiner.get(grp)).add(combiner.get(ind));
			}
		}

		Set<HashSet<Mention>> sets = new HashSet<HashSet<Mention>>(combiner.values());
		ColorProvider cm = new ColorProvider();
		for (HashSet<Mention> s : sets) {
			Entity e;
			if (groups.containsKey(s))
				e = new EntityGroup(aJCas);
			else
				e = new Entity(aJCas);

			e.setColor(cm.getNextColor().getRGB());
			e.addToIndexes();
			entityMap.put(s, e);
			for (Mention m : s) {
				if (e.getLabel() == null)
					e.setLabel(m.getCoveredText());
				m.setEntity(e);
			}
		}

		for (Set<Mention> grp : groups.keySet()) {
			EntityGroup eg = (EntityGroup) entityMap.get(grp);
			FSArray arr = new FSArray(aJCas, groups.get(grp).size());
			int i = 0;
			for (Set<Mention> memberSets : groups.get(grp)) {
				arr.set(i++, entityMap.get(memberSets));
			}
			eg.setMembers(arr);
		}

	}

}
