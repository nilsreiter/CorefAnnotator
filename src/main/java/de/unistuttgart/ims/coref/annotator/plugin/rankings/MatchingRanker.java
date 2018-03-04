package de.unistuttgart.ims.coref.annotator.plugin.rankings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.PotentialAnnotation;
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
	public void rank(PotentialAnnotation potAnnotation, CoreferenceModel cModel, JCas jcas) {
		String s = jcas.getDocumentText().substring(potAnnotation.getBegin(), potAnnotation.getEnd());
		pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < ((CATreeNode) cModel.getRoot()).getChildCount(); i++) {
			CATreeNode tn = ((CATreeNode) cModel.getRoot()).getChildAt(i);
			if (tn.isEntity()) {
				tn.setRank(matches(s, tn) ? 60 : 40);
			}
		}
	}

	protected boolean matches(String s, CATreeNode e) {
		if (!e.isEntity())
			return false;
		Matcher m;

		if (e.getEntity().getLabel() != null) {
			m = pattern.matcher(e.getEntity().getLabel());
			if (m.find())
				return true;
		}
		StringArray flags = e.getEntity().getFlags();
		if (flags != null)
			for (int i = 0; i < e.getEntity().getFlags().size(); i++) {
				m = pattern.matcher(e.getEntity().getFlags(i));
				if (m.find())
					return true;
			}
		for (int i = 0; i < e.getChildCount(); i++) {
			FeatureStructure child = e.getChildAt(i).getFeatureStructure();
			if (child instanceof Annotation) {
				String mc = ((Annotation) child).getCoveredText();
				m = pattern.matcher(mc);
				if (m.find())
					return true;
			}
		}
		return false;

	}
}
