package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;

import javax.swing.AbstractListModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CommentSortOrder;
import de.unistuttgart.ims.coref.annotator.api.AnnotationComment;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.CommentAnchor;

public class CommentsModel extends AbstractListModel<Comment> {

	MutableList<Comment> comments = Lists.mutable.empty();
	MutableMap<FeatureStructure, Comment> commentMap = Maps.mutable.empty();
	DocumentModel documentModel;
	private static final long serialVersionUID = 1L;

	public CommentsModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public void load() {

		for (Comment comment : JCasUtil.select(this.documentModel.getJcas(), Comment.class)) {
			register(comment);
		}
		Annotator.logger.debug("Comments list contains {} elements.", comments.size());
	}

	public Comment add(String text, String author, int begin, int end) {

		CommentAnchor annotation = new CommentAnchor(this.documentModel.getJcas());
		annotation.setBegin(begin);
		annotation.setEnd(end);
		annotation.addToIndexes();

		AnnotationComment comment = new AnnotationComment(this.documentModel.getJcas());
		comment.setAuthor(author);
		comment.setValue(text);
		comment.setAnnotation(annotation);
		comment.addToIndexes();

		register(comment);
		documentModel.setUnsavedChanges(true);
		documentModel.fireDocumentChangedEvent();
		return comment;
	}

	public Comment get(CommentAnchor ca) {
		return commentMap.get(ca);
	}

	@Override
	public int getSize() {
		return comments.size();
	}

	@Override
	public Comment getElementAt(int index) {
		return comments.get(index);
	}

	protected void register(Comment comment) {
		if (comment instanceof AnnotationComment) {
			commentMap.put(((AnnotationComment) comment).getAnnotation(), comment);
			documentModel.getCoreferenceModel().characterPosition2AnnotationMap
					.add(((AnnotationComment) comment).getAnnotation());
		}
		int ind = 0;
		Comparator<Comment> comp = CommentSortOrder.POSITION.getComparator();
		while (ind < comments.size()) {
			if (comp.compare(comment, comments.get(ind)) < 0) {
				break;
			}
			ind++;
		}
		comments.add(ind, comment);
		fireIntervalAdded(this, ind, ind);
		if (comment instanceof AnnotationComment)
			documentModel.getCoreferenceModel()
					.fireEvent(Event.get(Event.Type.Add, null, ((AnnotationComment) comment).getAnnotation()));

	}

	public void remove(Comment c) {
		int index = comments.indexOf(c);
		comments.remove(index);
		if (c instanceof AnnotationComment) {
			commentMap.remove(((AnnotationComment) c).getAnnotation());
			documentModel.getCoreferenceModel().characterPosition2AnnotationMap
					.remove(((AnnotationComment) c).getAnnotation());
			documentModel.getCoreferenceModel()
					.fireEvent(Event.get(Event.Type.Remove, null, ((AnnotationComment) c).getAnnotation()));
		}
		documentModel.setUnsavedChanges(true);
		fireIntervalRemoved(this, index, index);
	}

	public void update(Comment c) {
		int index = comments.indexOf(c);
		documentModel.setUnsavedChanges(true);
		fireContentsChanged(this, index, index);
	}
}