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
