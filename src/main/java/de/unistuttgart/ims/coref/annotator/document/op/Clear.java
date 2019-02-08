package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.MutableList;

/**
 * TODO: implement operation
 * 
 * @author reiterns
 *
 */
public class Clear implements CoreferenceModelOperation, FlagModelOperation {

	MutableList<FeatureStructure> clearFeatureStructures;

}
