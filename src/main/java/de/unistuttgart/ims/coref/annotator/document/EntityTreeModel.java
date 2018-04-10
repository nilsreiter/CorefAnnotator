package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
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
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		CATreeNode arg0 = get(event.getArgument(0));
		switch (eventType) {
		case Add:
			for (FeatureStructure fs : event.iterable(1)) {
				if (fs instanceof Mention || fs instanceof Entity || fs instanceof DetachedMentionPart) {
					CATreeNode tn = createNode(fs);
					insertNodeInto(tn, arg0, getInsertPosition(arg0, fs));
					if (fs instanceof EntityGroup) {
						EntityGroup eg = (EntityGroup) fs;
						for (int j = 0; j < eg.getMembers().size(); j++)
							try {
								insertNodeInto(new CATreeNode(eg.getMembers(j)), tn, 0);
							} catch (NullPointerException e) {
								Annotator.logger.catching(e);
							}
					}
				}
			}
			optResort();
			break;
		case Remove:
			if (event.getArgument1() instanceof EntityGroup) {
				CATreeNode gn = fsMap.get(event.getArgument1());
				MutableList<FeatureStructure> members = Lists.mutable.withAll(gn.getChildren())
						.collect(n -> n.getFeatureStructure());
				for (int i = members.size() - 1; i >= 0; i--) {
					if (event.arguments.contains(members.get(i)))
						removeNodeFromParent(gn.getChildAt(i));
				}
			} else
				for (int i = 1; i < event.getArity(); i++) {
					CATreeNode etn = fsMap.get(event.getArgument(i));
					if (etn != null) {
						etn.removeAllChildren();
						removeNodeFromParent(etn);
					}
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

				int ind = getInsertPosition(newParent, fs);
				insertNodeInto(node, newParent, ind);
			}
			optResort();
			break;
		default:
			break;
		}
	}

	private int getInsertPosition(CATreeNode newParent, FeatureStructure newChildFS) {
		int ind = 0;
		if (newChildFS instanceof Annotation)
			while (ind < newParent.getChildCount()) {
				CATreeNode cnode = newParent.getChildAt(ind);
				if (cnode.getFeatureStructure() instanceof Entity
						|| ((Annotation) cnode.getFeatureStructure()).getBegin() > ((Annotation) newChildFS).getBegin())
					break;
				ind++;
			}
		return ind;
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
		if (!getRoot().isLeaf()) {
			getRoot().getChildren().sort(comparator);
			nodeStructureChanged(getRoot());
		}
	}

	public void setEntitySortOrder(EntitySortOrder entitySortOrder) {
		this.entitySortOrder = entitySortOrder;
	}

}
