package de.unistuttgart.ims.coref.annotator.stats;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.Util;
import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public class DocumentStatistics implements CoreferenceModelListener {
	public static enum Property {
		NUMBER_OF_TOKENS, NUMBER_OF_CHARACTERS, NUMBER_OF_ENTITIES, NUMBER_OF_MENTIONS, MEAN_OF_MENTIONS_PER_ENTITY,
		STANDARD_DEVIATION_OF_MENTIONS_PER_ENTITY
	}

	public static final String PREFIX = "stat.key";

	DocumentModel documentModel;

	transient SummaryStatistics summaryStatistics = null;

	MutableSet<DocumentStatisticsListener> listeners = Sets.mutable.empty();

	int numberOfTokens = -1;
	int numberOfCharacters = -1;

	public double getMeanOfMentionsPerEntity() {
		return getSummaryStatistics().getMean();
	}

	public int getNumberOfEntities() {
		return JCasUtil.select(documentModel.getJcas(), Entity.class).size();
	}

	public int getNumberOfMentions() {
		return JCasUtil.select(documentModel.getJcas(), Mention.class).size();
	}

	public double getStandardDeviationOfMentionsPerEntity() {
		return getSummaryStatistics().getStandardDeviation();
	}

	protected SummaryStatistics getSummaryStatistics() {
		if (summaryStatistics == null) {
			JCas jcas = documentModel.getJcas();
			summaryStatistics = new SummaryStatistics();
			for (Entity e : JCasUtil.select(jcas, Entity.class)) {
				summaryStatistics.addValue(documentModel.getCoreferenceModel().getMentions(e).size());
			}
		}
		return summaryStatistics;
	}

	public Object getValue(Property s) {
		switch (s) {
		case MEAN_OF_MENTIONS_PER_ENTITY:
			return getMeanOfMentionsPerEntity();
		case STANDARD_DEVIATION_OF_MENTIONS_PER_ENTITY:
			return getStandardDeviationOfMentionsPerEntity();
		case NUMBER_OF_MENTIONS:
			return getNumberOfMentions();
		case NUMBER_OF_ENTITIES:
			return getNumberOfEntities();
		case NUMBER_OF_TOKENS:
			return getNumberOfTokens();
		case NUMBER_OF_CHARACTERS:
			return getNumberOfCharacters();
		default:
			return null;
		}
	}

	public String getFormatString(Property p) {
		switch (p) {
		case NUMBER_OF_CHARACTERS:
		case NUMBER_OF_TOKENS:
		case NUMBER_OF_ENTITIES:
		case NUMBER_OF_MENTIONS:
			return "$,7d";
		case STANDARD_DEVIATION_OF_MENTIONS_PER_ENTITY:
		case MEAN_OF_MENTIONS_PER_ENTITY:
			return "$3.3f";
		default:
			return "$s";
		}
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		summaryStatistics = null;
		listeners.forEach(l -> l.refreshStatistics());
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
		this.summaryStatistics = null;
		this.numberOfTokens = Util.count(documentModel.getJcas(), Token.class);
		this.numberOfCharacters = documentModel.getJcas().getDocumentText().length();

	}

	public boolean addDocumentStatisticsListener(DocumentStatisticsListener e) {
		return listeners.add(e);
	}

	public boolean removeDocumentStatisticsListener(Object o) {
		return listeners.remove(o);
	}

	public int getNumberOfTokens() {
		return numberOfTokens;
	}

	public int getNumberOfCharacters() {
		return numberOfCharacters;
	}

}
