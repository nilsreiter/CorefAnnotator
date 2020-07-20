package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

class SearchResultMention extends SearchResult {

	Mention mention;

	public SearchResultMention(SearchContainer c, Mention m) {
		super(c, UimaUtil.getBegin(m), UimaUtil.getEnd(m));
		this.mention = m;
	}

	public Mention getMention() {
		return mention;
	}

}