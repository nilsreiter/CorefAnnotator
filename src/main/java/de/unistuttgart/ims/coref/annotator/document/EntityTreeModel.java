package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.EntitySortOrder;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.comp.SortingTreeModelListener;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel.EntitySorter;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityName;
import de.unistuttgart.ims.coref.annotator.uima.MentionComparator;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class EntityTreeModel extends DefaultTreeModel implements CoreferenceModelListener, Model, ModelAdapter {
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

	String currentSearchString = null;

	MutableList<EntitySortOrderListener> entitySortOrderListeners = Lists.mutable.empty();

	public EntityTreeModel(CoreferenceModel docMod) {
		super(new CATreeNode(null, Annotator.getString("tree.root")));
		this.coreferenceModel = docMod;
		this.coreferenceModel.addCoreferenceModelListener(this);

		// this.initialise();
		this.resort();

	}

	public void addTreeModelListener(SortingTreeModelListener l) {
		listenerList.add(SortingTreeModelListener.class, l);
	}

	private CATreeNode createNode(FeatureStructure fs) {
		CATreeNode node = null;
		if (fs instanceof Entity) {
			node = new CATreeNode(fs, ((Entity) fs).getLabel());
		} else if (fs instanceof Annotation) {
			node = new CATreeNode(fs, ((Annotation) fs).getCoveredText());
		} else if (fs instanceof Mention) {
			node = new CATreeNode(fs, UimaUtil.getCoveredText((Mention) fs));
		}
		if (node != null)
			fsMap.put(fs, node);
		return node;
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Annotator.logger.traceEntry();
		Event.Type eventType = event.getType();
		switch (eventType) {
		case Add:
			CATreeNode arg0 = get(event.getArgument(0));
			for (FeatureStructure fs : event.iterable(1)) {
				if (fs instanceof MentionSurface) {
					nodeChanged(arg0);
				} else if (fs instanceof Mention || fs instanceof Entity) {
					CATreeNode tn = createNode(fs);
					insertNodeInto(tn, arg0, getInsertPosition(arg0, fs));
					if (fs instanceof Entity && UimaUtil.isGroup(fs)) {
						Entity eg = (Entity) fs;
						for (int j = 0; j < eg.getMembers().size(); j++)
							try {
								insertNodeInto(new CATreeNode(eg.getMembers(j)), tn, 0);
							} catch (NullPointerException e) {
								Annotator.logger.catching(e);
							}
					}
				}
			}
			arg0.modify();
			optResort();
			break;
		case Remove:
			if (UimaUtil.isGroup(event.getArgument1())) {
				CATreeNode gn = fsMap.get(event.getArgument1());
				MutableList<FeatureStructure> members = Lists.mutable.withAll(gn.getChildren())
						.collect(n -> n.getFeatureStructure());
				for (int i = members.size() - 1; i >= 0; i--) {
					if (event.arguments.contains(members.get(i)))
						removeNodeFromParent(gn.getChildAt(i));
				}
				gn.modify();
			} else
				for (int i = 1; i < event.getArity(); i++) {
					CATreeNode etn = fsMap.get(event.getArgument(i));
					if (etn != null) {
						etn.removeAllChildren();
						removeNodeFromParent(etn);
					}
					fsMap.remove(event.getArgument(i));
					etn.modify();
				}
			optResort();
			break;
		case Update:
			for (int i = 0; i < event.getArity(); i++) {
				CATreeNode node = get(event.getArgument(i));
				nodeChanged(node);
				node.modify();
			}
			break;
		case Move:
			for (int i = 2; i < event.getArity(); i++) {
				FeatureStructure fs = event.getArgument(i);
				CATreeNode node = get(fs);
				CATreeNode oldParent = node.getParent();
				CATreeNode newParent = get(event.getArgument(1));
				removeNodeFromParent(node);

				int ind = getInsertPosition(newParent, fs);
				insertNodeInto(node, newParent, ind);
				oldParent.modify();
				newParent.modify();
			}
			optResort();
			break;
		case Init:
			initialise();
			break;
		default:
			break;
		}
	}

	protected void fireTreeNodesPreResort(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SortingTreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((SortingTreeModelListener) listeners[i + 1]).treeNodesPreResort(e);
			}
		}
	}

	protected void fireTreeNodesPostResort(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SortingTreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((SortingTreeModelListener) listeners[i + 1]).treeNodesPostResort(e);
			}
		}
	}

	public CATreeNode get(FeatureStructure m) {
		if (m == null)
			return getRoot();
		if (!fsMap.containsKey(m)) {
			entityEvent(Event.get(this, Event.Type.Add, null, m));
		}
		return fsMap.get(m);
	}

	public EntitySortOrder getEntitySortOrder() {
		return entitySortOrder;
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

	public Object[] getPathToRoot(FeatureStructure fs) {
		return getPathToRoot(get(fs));
	}

	@Override
	public CATreeNode getRoot() {
		return (CATreeNode) root;
	}

	private void initialise() {
		for (Entity e : coreferenceModel.getEntities(EntitySorter.ADDRESS)) {
			entityEvent(Event.get(this, Event.Type.Add, null, e));
		}
		MentionComparator mc = new MentionComparator();
		// this is needed for ascending sorting
		mc.setDescending(true);
		for (Mention m : SortedSets.immutable.withAll(mc, JCasUtil.select(coreferenceModel.getJCas(), Mention.class))) {
			entityEvent(Event.get(this, Event.Type.Add, m.getEntity(), m));
		}

		Annotator.logger.debug("Added all entities and mentions.");
	}

	protected boolean matches(Pattern pattern, CATreeNode e) {
		if (!e.isEntity())
			return false;
		Matcher m;

		if (e.getEntity().getLabel() != null) {
			m = pattern.matcher(e.getEntity().getLabel());
			if (m.find())
				return true;
		}
		FSList<Flag> flags = e.getEntity().getFlags();
		if (flags != null)
			for (int i = 0; i < e.getEntity().getFlags().getLength(); i++) {
				m = pattern.matcher(flags.getNthElement(i).getLabel());
				if (m.find())
					return true;
			}
		for (int i = 0; i < e.getChildCount(); i++) {
			FeatureStructure child = e.getChildAt(i).getFeatureStructure();
			if (child instanceof Annotation) {
				String mc = ((Annotation) child).getCoveredText();
				m = pattern.matcher(mc);
				if (m.find())
					return true;
			}
		}
		return false;

	}

	public void optResort() {
		if (coreferenceModel.getPreferences().getBoolean(Constants.CFG_KEEP_TREE_SORTED, Defaults.CFG_KEEP_TREE_SORTED))
			resort();
	}

	public void rankBySearchString(String s) {
		currentSearchString = s;
		if (s == null)
			return;
		Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
		if (s.length() >= 1) {
			for (int i = 0; i < getRoot().getChildCount(); i++) {
				CATreeNode tn = getRoot().getChildAt(i);
				if (tn.isEntity()) {
					tn.setRank(matches(pattern, tn) ? 60 : 40);
				}
			}
			resort(EntitySortOrder.getVisibilitySortOrder(getEntitySortOrder().getComparator()));
		} else {
			unRankBySearch();
		}
	}

	public void unRankBySearch() {
		currentSearchString = null;
		for (int i = 0; i < getRoot().getChildCount(); i++) {
			CATreeNode tn = getRoot().getChildAt(i);
			if (tn.isEntity()) {
				tn.setRank(50);

			}
		}
		resort();
	}

	public void resort() {
		if (currentSearchString != null)
			rankBySearchString(currentSearchString);
		else
			resort(entitySortOrder.getComparator());
	}

	public void resort(Comparator<CATreeNode> comparator) {
		if (!getRoot().isLeaf()) {
			getRoot().getChildren().sort(comparator);
			fireTreeNodesPreResort(this, new CATreeNode[] { getRoot() }, null, null);
			nodeStructureChanged(getRoot());
			fireTreeNodesPostResort(this, new CATreeNode[] { getRoot() }, null, null);
		}
	}

	public void setEntitySortOrder(EntitySortOrder entitySortOrder) {
		this.entitySortOrder = entitySortOrder;
		entitySortOrderListeners.forEach(l -> l.entitySortEvent(entitySortOrder, entitySortOrder.descending));
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		coreferenceModel.getDocumentModel()
				.edit(new UpdateEntityName(((CATreeNode) path.getLastPathComponent()).getEntity(), (String) newValue));
	}

	public boolean addEntitySortOrderListener(EntitySortOrderListener e) {
		return entitySortOrderListeners.add(e);
	}
}
