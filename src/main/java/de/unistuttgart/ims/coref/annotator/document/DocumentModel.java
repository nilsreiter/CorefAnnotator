package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.coref.annotator.FileFormat;

/**
 * This class represents an opened document. Individual aspects are stored in
 * sub models (e.g., for comments or the coreference part). All interaction with
 * the document should take place through this class (and not accessing the JCas
 * directly).
 *
 */
public class DocumentModel {

	JCas jcas;

	CommentsModel commentsModel;

	CoreferenceModel coreferenceModel;

	EntityTreeModel treeModel;

	FileFormat fileFormat;

	MutableList<DocumentStateListener> documentStateListeners = Lists.mutable.empty();

	boolean unsavedChanges = false;

	public DocumentModel(JCas jcas) {
		this.jcas = jcas;
	}

	public boolean addDocumentStateListener(DocumentStateListener e) {
		return documentStateListeners.add(e);
	}

	protected void fireDocumentChangedEvent() {
		documentStateListeners.forEach(l -> l.documentStateEvent(new DocumentState(this)));
	}

	public CommentsModel getCommentsModel() {
		return commentsModel;
	}

	public CoreferenceModel getCoreferenceModel() {
		return coreferenceModel;
	}

	public FileFormat getFileFormat() {
		return fileFormat;
	}

	/**
	 * Don't use! This method will become protected at some point.
	 * 
	 * @return
	 */
	public JCas getJcas() {
		return jcas;
	}

	public String getLanguage() {
		return jcas.getDocumentLanguage();
	}

	public EntityTreeModel getTreeModel() {
		return treeModel;
	}

	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	public boolean isSavable() {
		return hasUnsavedChanges() || coreferenceModel.getHistory().size() > 0;
	}

	/**
	 * This method removes annotations from the CAS that are not used by the
	 * annotation tool. Currently, this is mostly DKpro-related annotations. TODO:
	 * Make this configurable and more robust
	 */
	public void removeForeignAnnotations() {
		TOP fs;

		// dkpro types
		fs = new CoreferenceChain(jcas);
		jcas.removeAllIncludingSubtypes(fs.getTypeIndexID());
		fs = new CoreferenceLink(jcas);
		jcas.removeAllIncludingSubtypes(fs.getTypeIndexID());
		fs = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas);
		jcas.removeAllIncludingSubtypes(fs.getTypeIndexID());
		fs = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas);
		jcas.removeAllIncludingSubtypes(fs.getTypeIndexID());

		unsavedChanges = true;
		fireDocumentChangedEvent();
	}

	public void setCommentsModel(CommentsModel commentsModel) {
		this.commentsModel = commentsModel;
	}

	public void setCoreferenceModel(CoreferenceModel coreferenceModel) {
		this.coreferenceModel = coreferenceModel;
	}

	protected void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public void setJcas(JCas jcas) {
		this.jcas = jcas;
	}

	public void setLanguage(String l) {
		jcas.setDocumentLanguage(l);
		fireDocumentChangedEvent();
	}

	public void setTreeModel(EntityTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	protected void setUnsavedChanges(boolean unsavedChanges) {
		this.unsavedChanges = unsavedChanges;
	}

	public void signal() {
		fireDocumentChangedEvent();
	}

}
