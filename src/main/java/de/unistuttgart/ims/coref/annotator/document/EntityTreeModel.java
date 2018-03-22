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
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;

public class EntityTreeModel extends DefaultTreeModel implements CoreferenceModelListener {
	private static final long serialVersionUID = 1L;

	CoreferenceModel coreferenceModel;

	/**
	 * The sort order for the tree nodes
	 */
	EntitySortOrder entitySortOrder = Defaults.CFG_ENTITY_SORT_ORDER;

	/**
	 * A map of feature structures to the tree nodes that represent them
	 */
	Map<FeatureStructure, CATreeNode> fsMap = Maps.mutable.empty();

	public EntityTreeModel(CoreferenceModel docMod) {
		super(new CATreeNode(null, Annotator.getString("tree.root")));
		this.coreferenceModel = docMod;
		this.coreferenceModel.addCoreferenceModelListener(this);

		this.initialise();
		this.resort();

	}

	@Override
	@Deprecated
	public void annotationEvent(AnnotationEvent evt) {
		Type event = evt.getType();
		Annotation a = (Annotation) evt.getArgument1();

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
			} else if (a instanceof DetachedMentionPart) {
				DetachedMentionPart dmp = (DetachedMentionPart) a;
				CATreeNode node = new CATreeNode(dmp, dmp.getCoveredText());
				fsMap.put(dmp, node);
				CATreeNode mentionNode = get(dmp.getMention());
				int ind = 0;
				while (ind < mentionNode.getChildCount()) {
					CATreeNode cnode = mentionNode.getChildAt(ind);
					if (cnode.getFeatureStructure() instanceof Entity
							|| ((Annotation) cnode.getFeatureStructure()).getBegin() > dmp.getBegin())
						break;
					ind++;
				}
				insertNodeInto(node, mentionNode, ind);
			}
			break;
		case Remove:
			removeNodeFromParent(get(a));
			break;
		case Update:
			nodeChanged(get(a));
			break;
		case Move:
			AnnotationMoveEvent<?> moveEvent = (AnnotationMoveEvent<?>) evt;
			CATreeNode node = get(a);
			CATreeNode newParent = get(moveEvent.getTo());
			removeNodeFromParent(node);

			int ind = 0;
			while (ind < newParent.getChildCount()) {
				CATreeNode cnode = newParent.getChildAt(ind);
				if (cnode.getFeatureStructure() instanceof Entity
						|| ((Annotation) cnode.getFeatureStructure()).getBegin() > a.getBegin())
					break;
				ind++;
			}
			insertNodeInto(node, newParent, ind);
			resort();
			break;
		default:
			break;
		}
	}

	@Deprecated
	@Override
	public void annotationEvent(EventType eventType, Annotation a) {

	}

	@Override
	@Deprecated
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

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		switch (eventType) {
		case Add:
			for (int i = 1; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity) {
					CATreeNode tn = createNode(event.getArgument(i));
					insertNodeInto(tn, get(event.getArgument(0)), 0);
				}
				if (event.getArgument(i) instanceof EntityGroup) {
					EntityGroup eg = (EntityGroup) event.getArgument(i);
					for (int j = 0; i < eg.getMembers().size(); j++)
						insertNodeInto(new CATreeNode(eg.getMembers(j)), get(eg), 0);
				}
			}
			optResort();
			break;
		case Remove:
			for (int i = 1; i < event.getArity(); i++) {
				CATreeNode etn = fsMap.get(event.getArgument(i));
				etn.removeAllChildren();
				removeNodeFromParent(etn);
				fsMap.remove(event.getArgument(i));
			}
			optResort();
			break;
		case Update:
			for (int i = 0; i < event.getArity(); i++)
				nodeChanged(get(event.getArgument(i)));
			break;
		case Move:
			for (int i = 2; i < event.getArity(); i++) {
				FeatureStructure fs = event.getArgument(i);
				CATreeNode node = get(fs);
				CATreeNode newParent = get(event.getArgument(1));
				removeNodeFromParent(node);

				int ind = 0;
				if (fs instanceof Annotation) {
					while (ind < newParent.getChildCount()) {
						CATreeNode cnode = newParent.getChildAt(ind);
						if (cnode.getFeatureStructure() instanceof Entity
								|| ((Annotation) cnode.getFeatureStructure()).getBegin() > ((Annotation) fs).getBegin())
							break;
						ind++;
					}
				}
				insertNodeInto(node, newParent, ind);
			}
			optResort();
			break;
		default:
			break;
		}
	}

	private CATreeNode createNode(FeatureStructure fs) {
		CATreeNode node = null;
		if (fs instanceof Entity) {
			node = new CATreeNode(fs, ((Entity) fs).getLabel());
		} else if (fs instanceof Annotation) {
			node = new CATreeNode(fs, ((Annotation) fs).getCoveredText());
		}
		if (node != null)
			fsMap.put(fs, node);
		return node;
	}

	@Override
	@Deprecated
	public void entityEvent(EventType eventType, Entity entity) {
		switch (eventType) {
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
		case Update:
			nodeChanged(get(entity));
			break;
		default:
			break;
		}
	}

	@Override
	@Deprecated
	public void entityGroupEvent(EventType eventType, EntityGroup eg) {
		Annotator.logger.entry();
		switch (eventType) {
		case Add:
			CATreeNode tn = new CATreeNode(eg, eg.getLabel());
			fsMap.put(eg, tn);
			insertNodeInto(tn, getRoot(), 0);
			for (int i = 0; i < eg.getMembers().size(); i++)
				insertNodeInto(new CATreeNode(eg.getMembers(i)), get(eg), 0);
			optResort();
			break;
		case Remove:
			removeNodeFromParent(get(eg));
			break;
		case Update:
			CATreeNode egNode = get(eg);
			for (int i = egNode.getChildCount() - 1; i >= 0; i--) {
				CATreeNode child = egNode.getChildAt(i);
				if (child.isEntity())
					removeNodeFromParent(child);
			}
			for (int i = 0; i < eg.getMembers().size(); i++)
				insertNodeInto(new CATreeNode(eg.getMembers(i)), get(eg), egNode.getChildCount());
			nodeStructureChanged(egNode);
			break;
		default:
			break;

		}

	}

	protected CATreeNode get(Object m) {
		if (m == null)
			return getRoot();
		return fsMap.get(m);
	}

	public EntitySortOrder getEntitySortOrder() {
		return entitySortOrder;
	}

	public Object[] getPathToRoot(FeatureStructure fs) {
		return getPathToRoot(get(fs));
	}

	@Override
	public CATreeNode getRoot() {
		return (CATreeNode) root;
	}

	public void initialise() {
		Lists.immutable.withAll(JCasUtil.select(coreferenceModel.getJCas(), Entity.class)).forEach(e -> {
			entityEvent(Event.get(Event.Type.Add, null, e));
		});
		Annotator.logger.debug("Added all entities");

		for (Mention m : JCasUtil.select(coreferenceModel.getJCas(), Mention.class)) {
			entityEvent(Event.get(Event.Type.Add, m.getEntity(), m));
		}
		Annotator.logger.debug("Added all mentions");
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

}
