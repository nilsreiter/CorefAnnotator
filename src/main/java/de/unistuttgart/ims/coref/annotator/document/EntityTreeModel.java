package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.EntitySortOrder;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class EntityTreeModel extends DefaultTreeModel implements CoreferenceModelListener {
	private static final long serialVersionUID = 1L;

	CoreferenceModel coreferenceModel;

	/**
	 * A map of feature structures to the tree nodes that represent them
	 */
	Map<FeatureStructure, CATreeNode> fsMap = Maps.mutable.empty();

	/**
	 * The sort order for the tree nodes
	 */
	EntitySortOrder entitySortOrder = Defaults.CFG_ENTITY_SORT_ORDER;

	public EntityTreeModel(CoreferenceModel docMod) {
		super(new CATreeNode(null, Annotator.getString("tree.root")));
		this.coreferenceModel = docMod;
		this.coreferenceModel.addCoreferenceModelListener(this);

		this.initialise();

		nodeStructureChanged(root);
	}

	public void initialise() {
		Lists.immutable.withAll(JCasUtil.select(coreferenceModel.getJcas(), Entity.class)).forEach(e -> {
			entityAdded(e);
		});
		Annotator.logger.debug("Added all entities");

		for (Mention m : JCasUtil.select(coreferenceModel.getJcas(), Mention.class)) {
			annotationAdded(m);
		}
		Annotator.logger.debug("Added all mentions");
	}

	@Override
	public void annotationAdded(Annotation a) {
		if (a instanceof Mention) {
			Mention mention = (Mention) a;
			CATreeNode node = new CATreeNode(mention, mention.getCoveredText());
			fsMap.put(mention, node);
			CATreeNode entityNode = get(mention.getEntity());

			int ind = 0;
			while (ind < entityNode.getChildCount()) {
				CATreeNode cnode = entityNode.getChildAt(ind);
				if (cnode.getFeatureStructure() instanceof Entity
						|| ((Annotation) cnode.getFeatureStructure()).getBegin() > mention.getBegin())
					break;
				ind++;
			}

			insertNodeInto(node, entityNode, ind);
		}
	}

	@Override
	public void annotationChanged(Annotation m) {
		nodeChanged(get(m));
	}

	@Override
	public void annotationRemoved(Annotation m) {
		removeNodeFromParent(get(m));
	}

	@Override
	public void entityAdded(Entity entity) {
		// if (fsMap.containsKey(entity))
		// return;
		CATreeNode tn = new CATreeNode(entity, entity.getLabel());
		fsMap.put(entity, tn);
		insertNodeInto(tn, getRoot(), 0);
		if (entity instanceof EntityGroup) {
			EntityGroup eg = (EntityGroup) entity;
			for (int i = 0; i < eg.getMembers().size(); i++)
				insertNodeInto(new CATreeNode(eg.getMembers(i)), get(eg), 0);
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		CATreeNode etn = fsMap.get(entity);
		for (int i = 0; i < etn.getChildCount(); i++) {
			etn.removeAllChildren();
		}
		removeNodeFromParent(etn);
		fsMap.remove(entity);
	}

	protected CATreeNode get(Annotation m) {
		return fsMap.get(m);
	}

	protected CATreeNode get(Entity e) {
		// if (!fsMap.containsKey(e))
		// entityAdded(e);
		return fsMap.get(e);
	}

	@Override
	public CATreeNode getRoot() {
		return (CATreeNode) root;
	}

	public void resort() {
		resort(entitySortOrder.getComparator());
	}

	public void resort(Comparator<CATreeNode> comparator) {
		Annotator.logger.trace("Sorting entity tree with {}", comparator.toString());
		getRoot().getChildren().sort(comparator);
		nodeStructureChanged(getRoot());
	}

	public void setEntitySortOrder(EntitySortOrder entitySortOrder) {
		this.entitySortOrder = entitySortOrder;
	}

}
