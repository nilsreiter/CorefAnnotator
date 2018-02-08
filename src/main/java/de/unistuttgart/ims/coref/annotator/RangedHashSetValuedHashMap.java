package de.unistuttgart.ims.coref.annotator;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.jcas.tcas.Annotation;

public class RangedHashSetValuedHashMap<V extends Annotation> extends HashSetValuedHashMap<Integer, V> {

	private static final long serialVersionUID = 1L;

	public void add(V value) {
		this.put(new Span(value), value);
	}

	public void put(Span span, V value) {
		for (int i = span.begin; i < span.end; i++)
			put(i, value);
	}

	public void remove(V value) {
		for (int i = value.getBegin(); i < value.getEnd(); i++)
			this.get(i).remove(value);
	}
}
