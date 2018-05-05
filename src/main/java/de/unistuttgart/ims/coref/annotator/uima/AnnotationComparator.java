package de.unistuttgart.ims.coref.annotator.uima;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationComparator implements Comparator<Annotation> {

	boolean useEnd = false;
	boolean descending = false;

	public AnnotationComparator() {
	}

	public AnnotationComparator(boolean useEnd) {
		this.useEnd = useEnd;
	}

	@Override
	public int compare(Annotation o1, Annotation o2) {
		if (useEnd)
			return (descending ? -1 : 1) * Integer.compare(o1.getEnd(), o2.getEnd());
		return (descending ? -1 : 1) * Integer.compare(o1.getBegin(), o2.getBegin());
	}

	public boolean isUseEnd() {
		return useEnd;
	}

	public void setUseEnd(boolean useEnd) {
		this.useEnd = useEnd;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}

}
