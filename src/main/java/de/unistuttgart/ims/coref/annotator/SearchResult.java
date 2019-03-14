package de.unistuttgart.ims.coref.annotator;

class SearchResult {
	Span span;
	SearchContainer container;

	public SearchResult(SearchContainer container, int begin, int end) {
		super();
		this.container = container;
		this.span = new Span(begin, end);
	}

	public int getBegin() {
		return span.begin;
	}

	public int getEnd() {
		return span.end;
	}

	public Span getSpan() {
		return span;
	}

	@Override
	public String toString() {
		return this.container.getText().substring(Integer.max(span.begin - this.container.getContexts(), 0),
				Integer.min(span.end + this.container.getContexts(), this.container.getText().length() - 1));
	}
}