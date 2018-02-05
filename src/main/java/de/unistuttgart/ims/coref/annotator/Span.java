package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Span {

	public int end;

	public int begin;

	public Span(Annotation annotation) {
		this.begin = annotation.getBegin();
		this.end = annotation.getEnd();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.begin, this.end);
	}

	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		Span that = (Span) obj;
		return this.begin == that.begin && this.end == that.end;
	}

	@Override
	public String toString() {
		MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("begin", this.begin);
		helper.add("end", this.end);
		return helper.toString();
	}

}
