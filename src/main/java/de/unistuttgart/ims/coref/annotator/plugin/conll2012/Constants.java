package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

public class Constants {
	public static final String TYPE_CHAIN = "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain";
	public static final String TYPE_LINK = "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink";

	public static final String FEATURE_FIRST = "first";
	public static final String FEATURE_NEXT = "next";

	public static final String FLOW_KEY_CONVERTER = "Converter";
	public static final String FLOW_KEY_TOKENIZER = "Tokenizer";
	public static final String FLOW_KEY_SENTENCE_SPLITTER = "Sentencer";
	public static final String FLOW_KEY_CLEANER = "Cleaner";

	public static final String DESCRIPTION = "Import from and Export into CoNLL 2012 format. All columns except surface, sentence and coreference are empty.";
	public static final String NAME = "CoNLL 2012";
}
