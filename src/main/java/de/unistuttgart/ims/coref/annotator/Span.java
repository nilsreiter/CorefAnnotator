package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.Objects;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class Span {

	public int end;

	public int begin;

	public Span(Annotation annotation) {
		this.begin = annotation.getBegin();
		this.end = annotation.getEnd();
	}

	public Span(int begin, int end) {
		this.begin = begin;
		this.end = end;
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
		return "(" + begin + "," + end + ")";
	}

	public boolean contains(Span other) {
		return (other.begin >= begin && other.end <= end);
	}

	public static class ExtendedSpan extends Span {

		public String entityLabel;

		public ExtendedSpan(Mention annotation) {
			super(annotation);
			this.entityLabel = annotation.getEntity().getLabel();
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.begin, this.end, this.entityLabel);
		}

		@Override
		public boolean equals(Object obj) {
			if (!this.getClass().equals(obj.getClass())) {
				return false;
			}
			ExtendedSpan that = (ExtendedSpan) obj;
			return this.begin == that.begin && this.end == that.end && this.entityLabel.contentEquals(that.entityLabel);
		}
	}
}
