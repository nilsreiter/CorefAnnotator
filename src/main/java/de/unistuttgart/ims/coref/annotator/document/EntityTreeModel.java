package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.StringArray;
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

	protected CATreeNode get(Object m) {
		if (m == null)
			return getRoot();
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

	protected boolean matches(Pattern pattern, CATreeNode e) {
		if (!e.isEntity())
			return false;
		Matcher m;

		if (e.getEntity().getLabel() != null) {
			m = pattern.matcher(e.getEntity().getLabel());
			if (m.find())
				return true;
		}
		StringArray flags = e.getEntity().getFlags();
		if (flags != null)
			for (int i = 0; i < e.getEntity().getFlags().size(); i++) {
				m = pattern.matcher(e.getEntity().getFlags(i));
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
			for (int i = 0; i < getRoot().getChildCount(); i++) {
				CATreeNode tn = getRoot().getChildAt(i);
				if (tn.isEntity()) {
					tn.setRank(50);

				}
			}
			resort();
		}
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
