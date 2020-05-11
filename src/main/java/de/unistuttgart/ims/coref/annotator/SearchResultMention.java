package de.unistuttgart.ims.coref.annotator;

import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

class SearchResultMention extends SearchResult {

	Mention mention;

	public SearchResultMention(SearchContainer c, Mention m) {
		super(c, m.getBegin(), m.getEnd());
		this.mention = m;
	}

	public Mention getMention() {
		return mention;
	}

}