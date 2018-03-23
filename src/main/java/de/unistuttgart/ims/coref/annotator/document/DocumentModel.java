package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class DocumentModel {

	JCas jcas;

	CommentsModel commentsModel;

	CoreferenceModel coreferenceModel;

	EntityTreeModel treeModel;

	MutableList<DocumentStateListener> documentStateListeners = Lists.mutable.empty();

	public DocumentModel(JCas jcas) {
		this.jcas = jcas;
	}

	public JCas getJcas() {
		return jcas;
	}

	public void setJcas(JCas jcas) {
		this.jcas = jcas;
	}

	public CommentsModel getCommentsModel() {
		return commentsModel;
	}

	public void setCommentsModel(CommentsModel commentsModel) {
		this.commentsModel = commentsModel;
	}

	public CoreferenceModel getCoreferenceModel() {
		return coreferenceModel;
	}

	public void setCoreferenceModel(CoreferenceModel coreferenceModel) {
		this.coreferenceModel = coreferenceModel;
	}

	public EntityTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(EntityTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public boolean addDocumentStateListener(DocumentStateListener e) {
		return documentStateListeners.add(e);
	}

	protected void fireDocumentChangedEvent() {
		documentStateListeners.forEach(l -> l.documentStateEvent(new DocumentState(this)));
	}

}
