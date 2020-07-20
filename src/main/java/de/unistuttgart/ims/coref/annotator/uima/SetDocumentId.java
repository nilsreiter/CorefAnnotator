package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class SetDocumentId extends JCasAnnotator_ImplBase {

	public static final String PARAM_DOCUMENT_ID = "Document Id";
	public static final String PARAM_DOCUMENT_TITLE = "Document Title";

	@ConfigurationParameter(name = PARAM_DOCUMENT_ID, mandatory = false)
	String documentId = null;

	@ConfigurationParameter(name = PARAM_DOCUMENT_TITLE, mandatory = false)
	String documentTitle = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		if (!JCasUtil.exists(jcas, DocumentMetaData.class))
			DocumentMetaData.create(jcas);
		if (documentId != null)
			DocumentMetaData.get(jcas).setDocumentId(documentId);
		if (documentTitle != null)
			DocumentMetaData.get(jcas).setDocumentTitle(documentTitle);
	}

}
