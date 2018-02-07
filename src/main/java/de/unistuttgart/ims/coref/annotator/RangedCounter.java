package de.unistuttgart.ims.coref.annotator;

public class RangedCounter extends Counter<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void subtract(Span span) {
		for (int i = span.begin; i < span.end; i++)
			subtract(i);

	}

	public void add(Span span) {
		for (int i = span.begin; i < span.end; i++)
			add(i);
	}

	public int getMax(Span span) {
		int max = 0;
		for (int i = span.begin; i < span.end; i++)
			if (get(i) > max)
				max = get(i);
		return max;
	}

}
