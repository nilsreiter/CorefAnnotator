package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

import de.unistuttgart.ims.coref.annotator.api.AnnotationComment;
import de.unistuttgart.ims.coref.annotator.api.Comment;

public enum CommentSortOrder {
	POSITION;

	public Comparator<Comment> getComparator() {
		return new Comparator<Comment>() {

			@Override
			public int compare(Comment o1, Comment o2) {
				int b1 = 0, b2 = 0;

				if (o1 instanceof AnnotationComment)
					b1 = ((AnnotationComment) o1).getAnnotation().getBegin();
				if (o2 instanceof AnnotationComment)
					b2 = ((AnnotationComment) o2).getAnnotation().getBegin();

				return Integer.compare(b1, b2);
			}
		};
	}
}
