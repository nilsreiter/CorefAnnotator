package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

public class LinguisticTreeModel<T extends Annotation> extends SubModel implements TreeModel {

	DefaultMutableTreeNode rootNode;
	Class<T> rootClass;
	Type uimaRootType;

	public LinguisticTreeModel(DocumentModel documentModel, Class<T> rootClass) {
		super(documentModel);

		this.rootNode = new DefaultMutableTreeNode();
		this.rootNode.setUserObject(rootClass.getSimpleName());
		this.rootClass = rootClass;
		this.uimaRootType = documentModel.getJcas().getTypeSystem().getType(rootClass.getName());
	}

	@Override
	public Object getRoot() {
		return rootNode;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == rootNode) {
			return getDocumentModel().getJcas().getTypeSystem().getDirectSubtypes(uimaRootType).get(index);
		} else if (parent instanceof Type) {
			// TODO: implmeent
			// JCasUtil.getType(jCas, type)
			// getDocumentModel().getJcas().getAnnotationIndex((Type) parent)
			// return JCasUtil.select(getDocumentModel().getJcas(), );
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		DefaultMutableTreeNode tnode = (DefaultMutableTreeNode) parent;
		if (parent == rootNode) {
			return getDocumentModel().getJcas().getTypeSystem().getDirectSubtypes(uimaRootType).size();
		} else if (tnode.getUserObject() instanceof Type) {
			Type t = (Type) tnode.getUserObject();
			return getDocumentModel().getJcas().getAnnotationIndex(t).size();
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		DefaultMutableTreeNode tnode = (DefaultMutableTreeNode) node;
		return tnode.getUserObject() instanceof Annotation;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

}
