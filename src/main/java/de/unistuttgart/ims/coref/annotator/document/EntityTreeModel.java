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
import de.unistuttgart.ims.coref.annotator.Constants;
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
		this.resort();

	}

	public void initialise() {
		Lists.immutable.withAll(JCasUtil.select(coreferenceModel.getJCas(), Entity.class)).forEach(e -> {
			entityEvent(Event.Add, e);
		});
		Annotator.logger.debug("Added all entities");

		for (Mention m : JCasUtil.select(coreferenceModel.getJCas(), Mention.class)) {
			annotationEvent(Event.Add, m);
		}
		Annotator.logger.debug("Added all mentions");
	}

	@Override
	public void annotationEvent(Event event, Annotation a) {
		switch (event) {
		case Add:
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
				optResort();
			}
			break;
		case Remove:
			removeNodeFromParent(get(a));
			break;
		case Update:
			nodeChanged(get(a));
			break;
		default:
			break;
		}
	}

	@Override
	public void entityEvent(Event event, Entity entity) {
		switch (event) {
		case Add:
			CATreeNode tn = new CATreeNode(entity, entity.getLabel());
			fsMap.put(entity, tn);
			insertNodeInto(tn, getRoot(), 0);
			if (entity instanceof EntityGroup) {
				EntityGroup eg = (EntityGroup) entity;
				for (int i = 0; i < eg.getMembers().size(); i++)
					insertNodeInto(new CATreeNode(eg.getMembers(i)), get(eg), 0);
			}
			optResort();
			break;
		case Remove:
			CATreeNode etn = fsMap.get(entity);
			for (int i = 0; i < etn.getChildCount(); i++) {
				etn.removeAllChildren();
			}
			removeNodeFromParent(etn);
			fsMap.remove(entity);
			optResort();
			break;
		case Update_Color:
		case Update:
			nodeChanged(get(entity));
			break;
		default:
			break;
		}
	}

	protected CATreeNode get(Object m) {
		return fsMap.get(m);
	}

	@Override
	public CATreeNode getRoot() {
		return (CATreeNode) root;
	}

	public void optResort() {
		if (coreferenceModel.getPreferences().getBoolean(Constants.CFG_KEEP_TREE_SORTED, Defaults.CFG_KEEP_TREE_SORTED))
			resort();
	}

	public void resort() {
		resort(entitySortOrder.getComparator());
	}

	public void resort(Comparator<CATreeNode> comparator) {
		Annotator.logger.trace("Sorting entity tree with {}", comparator.toString());
		if (!getRoot().isLeaf()) {
			getRoot().getChildren().sort(comparator);
			nodeStructureChanged(getRoot());
		}
	}

	public void setEntitySortOrder(EntitySortOrder entitySortOrder) {
		this.entitySortOrder = entitySortOrder;
	}

	public EntitySortOrder getEntitySortOrder() {
		return entitySortOrder;
	}

	@Override
	public void entityGroupEvent(Event event, EntityGroup eg) {
		switch (event) {
		case Add:
			CATreeNode tn = new CATreeNode(eg, eg.getLabel());
			fsMap.put(eg, tn);
			insertNodeInto(tn, getRoot(), 0);
			for (int i = 0; i < eg.getMembers().size(); i++)
				insertNodeInto(new CATreeNode(eg.getMembers(i)), get(eg), 0);
			optResort();
			break;
		case Remove:
			break;
		case Update:
			break;
		default:
			break;

		}

	}

	@Override
	public void annotationMovedEvent(Annotation annotation, Object from, Object to) {
		CATreeNode node = get(annotation);
		CATreeNode newParent = get(to);
		removeNodeFromParent(node);

		int ind = 0;
		while (ind < newParent.getChildCount()) {
			CATreeNode cnode = newParent.getChildAt(ind);
			if (cnode.getFeatureStructure() instanceof Entity
					|| ((Annotation) cnode.getFeatureStructure()).getBegin() > annotation.getBegin())
				break;
			ind++;
		}
		insertNodeInto(node, newParent, ind);
		resort();

	}

	public Object[] getPathToRoot(FeatureStructure fs) {
		return getPathToRoot(get(fs));
	}

}
