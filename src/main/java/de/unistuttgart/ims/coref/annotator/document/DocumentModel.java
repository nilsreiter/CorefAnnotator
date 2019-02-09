package de.unistuttgart.ims.coref.annotator.document;

import java.util.Deque;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.document.adapter.EntityTreeModel;
import de.unistuttgart.ims.coref.annotator.document.op.CoreferenceModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.FlagModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;
import de.unistuttgart.ims.coref.annotator.document.op.RelationModelOperation;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

/**
 * This class represents an opened document. Individual aspects are stored in
 * sub models (e.g., for comments or the coreference part). All interaction with
 * the document should take place through this class (and not accessing the JCas
 * directly).
 *
 */
public class DocumentModel implements Model {

	CoreferenceModel coreferenceModel;

	MutableList<DocumentStateListener> documentStateListeners = Lists.mutable.empty();

	Deque<Operation> history = new LinkedList<Operation>();

	JCas jcas;

	RelationModel relationModel;
	SegmentModel segmentModel;

	EntityTreeModel treeModel;

	FlagModel flagModel;

	TypeSystemVersion typeSystemVersion;

	boolean unsavedChanges = false;

	Preferences preferences;

	public DocumentModel(JCas jcas, Preferences preferences) {
		this.jcas = jcas;
		this.preferences = preferences;
	}

	public boolean addDocumentStateListener(DocumentStateListener e) {
		return documentStateListeners.add(e);
	}

	public void edit(Operation operation) {
		if (operation instanceof CoreferenceModelOperation)
			coreferenceModel.edit(operation);
		if (operation instanceof RelationModelOperation)
			relationModel.edit((RelationModelOperation) operation);
		if (operation instanceof FlagModelOperation)
			flagModel.edit((FlagModelOperation) operation);
		history.push(operation);
		fireDocumentChangedEvent();
	}

	protected void fireDocumentChangedEvent() {
		documentStateListeners.forEach(l -> l.documentStateEvent(new DocumentState(this)));
	}

	public CoreferenceModel getCoreferenceModel() {
		return coreferenceModel;
	}

	public String getDocumentTitle() {
		String documentTitle = "Untitled document";
		try {
			if (JCasUtil.exists(getJcas(), DocumentMetaData.class)
					&& DocumentMetaData.get(getJcas()).getDocumentTitle() != null)
				documentTitle = DocumentMetaData.get(getJcas()).getDocumentTitle();
		} catch (Exception e) {
			Annotator.logger.catching(e);
		}
		return documentTitle;
	}

	public TypeSystemVersion getFileFormat() {
		return typeSystemVersion;
	}

	public FlagModel getFlagModel() {
		return flagModel;
	}

	public Deque<Operation> getHistory() {
		return history;
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

	public Preferences getPreferences() {
		return preferences;
	}

	public RelationModel getRelationModel() {
		return relationModel;
	}

	public SegmentModel getSegmentModel() {
		return segmentModel;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends StylePlugin> getStylePlugin() throws ClassNotFoundException {
		return (Class<? extends StylePlugin>) Class.forName(Util.getMeta(jcas).getStylePlugin());
	}

	public EntityTreeModel getTreeModel() {
		return treeModel;
	}

	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	public void initialize() {
		coreferenceModel = new CoreferenceModel(this);
		treeModel = new EntityTreeModel(coreferenceModel);
		flagModel = new FlagModel(this, preferences);
		segmentModel = new SegmentModel(this);
		relationModel = new RelationModel(this);
	}

	public boolean isSavable() {
		return hasUnsavedChanges() || getHistory().size() > 0;
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

	public void setCoreferenceModel(CoreferenceModel coreferenceModel) {
		this.coreferenceModel = coreferenceModel;
	}

	protected void setFileFormat(TypeSystemVersion typeSystemVersion) {
		this.typeSystemVersion = typeSystemVersion;
	}

	public void setFlagModel(FlagModel flagModel) {
		this.flagModel = flagModel;
	}

	public void setJcas(JCas jcas) {
		this.jcas = jcas;
	}

	public void setLanguage(String l) {
		jcas.setDocumentLanguage(l);
		fireDocumentChangedEvent();
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public void setRelationModel(RelationModel relationModel) {
		this.relationModel = relationModel;
	}

	public void setSegmentModel(SegmentModel segmentModel) {
		this.segmentModel = segmentModel;
	}

	public void setTreeModel(EntityTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void setUnsavedChanges(boolean unsavedChanges) {
		this.unsavedChanges = unsavedChanges;
		fireDocumentChangedEvent();
	}

	public void signal() {
		fireDocumentChangedEvent();
	}

	public void undo() {
		if (!history.isEmpty()) {
			undo(history.pop());
			fireDocumentChangedEvent();
		}
	}

	protected void undo(Operation operation) {
		if (operation instanceof CoreferenceModelOperation)
			coreferenceModel.undo(operation);
		if (operation instanceof RelationModelOperation)
			relationModel.undo((RelationModelOperation) operation);
		if (operation instanceof FlagModelOperation)
			flagModel.undo((FlagModelOperation) operation);

	}

}
