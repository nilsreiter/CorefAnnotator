package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class CoreferenceModel extends DefaultTreeModel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	RangedHashSetValuedHashMap<Annotation> characterPosition2AnnotationMap = new RangedHashSetValuedHashMap<Annotation>();
	ColorProvider colorMap = new ColorProvider();
	HashSetValuedHashMap<FeatureStructure, Comment> comments = new HashSetValuedHashMap<FeatureStructure, Comment>();
	List<CoreferenceModelListener> crModelListeners = new LinkedList<CoreferenceModelListener>();
	Map<FeatureStructure, EntityTreeNode> entityMap = new HashMap<FeatureStructure, EntityTreeNode>();
	EntitySortOrder entitySortOrder = EntitySortOrder.Alphabet;
	CATreeNode groupRootNode;

	JCas jcas;
	boolean keepEmptyEntities = true;

	int key = 0;

	char[] keyCodes = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	Map<Character, Entity> keyMap = new HashMap<Character, Entity>();

	Map<FeatureStructure, CATreeNode> mentionMap = new HashMap<FeatureStructure, CATreeNode>();

	Preferences preferences;

	CATreeNode rootNode;

	public CoreferenceModel(JCas jcas, Preferences preferences) {
		super(new CATreeNode(null, Annotator.getString("tree.root")));
		this.rootNode = (CATreeNode) getRoot();
		// this.groupRootNode = new CATreeNode(null,
		// Annotator.getString("tree.groups"));
		// this.insertNodeInto(groupRootNode, rootNode, 0);
		this.jcas = jcas;
		this.preferences = preferences;

	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return crModelListeners.add(e);
	}

	public void addDiscontinuousToMention(Mention m, DetachedMentionPart dmp) {
		m.setDiscontinuous(dmp);
		insertNodeInto(mentionMap.get(dmp), mentionMap.get(m), 0);
	}

	public void addDiscontinuousToMention(Mention m, int begin, int end) {
		DetachedMentionPart d = AnnotationFactory.createAnnotation(jcas, begin, end, DetachedMentionPart.class);
		d.setMention(m);
		m.setDiscontinuous(d);
		characterPosition2AnnotationMap.add(d);
		CATreeNode node = new CATreeNode(d, d.getCoveredText());
		mentionMap.put(d, node);
		insertNodeInto(node, mentionMap.get(m), 0);
		fireMentionChangedEvent(m);
	}

	public EntityTreeNode addExistingEntity(Entity e) {
		EntityTreeNode tn = new EntityTreeNode(e, "");
		// if (e instanceof EntityGroup) {
		// insertNodeInto(tn, groupRootNode, 0);
		// } else
		insertNodeInto(tn, rootNode, 0);
		entityMap.put(e, tn);
		if (e.getKey() != null) {
			tn.setKeyCode(e.getKey().charAt(0));
			keyMap.put(e.getKey().charAt(0), e);
		}
		return tn;
	}

	public void addNewEntityMention(int begin, int end) {
		String covered = jcas.getDocumentText().substring(begin, end);
		Entity e = new Entity(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(covered);
		e.addToIndexes();
		EntityTreeNode tn = new EntityTreeNode(e, covered);

		int ind = 0;
		Comparator<EntityTreeNode> comparator = entitySortOrder.getComparator();
		while (ind < this.rootNode.getChildCount()) {
			CATreeNode n = (CATreeNode) rootNode.getChildAt(ind);
			if (n.getFeatureStructure() == null)
				break;
			EntityTreeNode node = (EntityTreeNode) rootNode.getChildAt(ind);
			if (comparator.compare(tn, node) <= 0)
				break;

			ind++;
		}

		insertNodeInto(tn, (CATreeNode) this.getRoot(), ind);
		entityMap.put(e, tn);
		if (key < keyCodes.length) {
			tn.setKeyCode(keyCodes[key]);
			keyMap.put(keyCodes[key++], e);
		}

		addNewMention(e, begin, end);
	}

	public void addNewMention(Entity e, int begin, int end) {
		Mention m = new Mention(jcas);
		m.setBegin(begin);
		m.setEnd(end);
		if (preferences.getBoolean(Constants.CFG_TRIM_WHITESPACE, true))
			m = AnnotationUtil.trim(m);
		m.addToIndexes();
		connect(e, m);
		fireMentionAddedEvent(m);
	}

	public void addToGroup(EntityGroup eg, Entity e) {
		// UIMA stuff
		FSArray oldArr = eg.getMembers();
		FSArray arr = new FSArray(jcas, eg.getMembers().size() + 1);
		int i = 0;
		for (; i < eg.getMembers().size(); i++) {
			arr.set(i, eg.getMembers(i));
		}
		arr.set(i, e);
		eg.setMembers(arr);
		oldArr.removeFromIndexes();

		// tree stuff
		insertNodeInto(new EntityTreeNode(e), entityMap.get(eg), 0);
	}

	protected void connect(Entity e, Mention m) {
		if (!entityMap.containsKey(e))
			addExistingEntity(e);

		m.setEntity(e);
		CATreeNode tn = new CATreeNode(m, m.getCoveredText());
		mentionMap.put(m, tn);
		int ind = 0;
		while (ind < entityMap.get(e).getChildCount()) {
			CATreeNode node = entityMap.get(e).getChildAt(ind);
			if (node.getFeatureStructure() instanceof Entity
					|| ((Annotation) node.getFeatureStructure()).getBegin() > m.getBegin())
				break;
			ind++;
		}

		insertNodeInto(tn, entityMap.get(e), ind);

		if (m.getDiscontinuous() != null) {
			CATreeNode discNode = new CATreeNode(m.getDiscontinuous(), m.getDiscontinuous().getCoveredText());
			mentionMap.put(m.getDiscontinuous(), discNode);
			insertNodeInto(discNode, tn, 0);
		}
		resort();
	}

	public void fireMentionAddedEvent(Mention m) {
		this.characterPosition2AnnotationMap.add(m);
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionAdded(m);
	}

	public void fireMentionChangedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionChanged(m);
	}

	public void fireMentionRemovedEvent(Mention m) {
		this.characterPosition2AnnotationMap.remove(m);
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionRemoved(m);
	}

	public void fireMentionSelectedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.annotationSelected(m);
	}

	public void formGroup(Entity e1, Entity e2) {
		FSArray arr = new FSArray(jcas, 2);
		arr.set(0, e1);
		arr.set(1, e2);
		EntityGroup eg = new EntityGroup(jcas);
		eg.setColor(colorMap.getNextColor().getRGB());
		if (e1.getLabel() != null && e2.getLabel() != null)
			eg.setLabel(e1.getLabel() + " and " + e2.getLabel());
		else
			eg.setLabel("A group");
		eg.setMembers(arr);
		eg.addToIndexes();

		EntityTreeNode gtn = addExistingEntity(eg);

		insertNodeInto(new EntityTreeNode(e1), gtn, 0);
		insertNodeInto(new EntityTreeNode(e2), gtn, 1);

	}

	public JCas getJcas() {
		return jcas;
	}

	public Collection<Annotation> getMentions(int position) {
		return this.characterPosition2AnnotationMap.get(position);
	}

	public CATreeNode getNode(Mention m) {
		return mentionMap.get(m);
	}

	public boolean isKeyUsed(int i) {
		return keyMap.containsKey(i);
	}

	public void reassignKey(char keyCode, Entity e) {
		Entity old = keyMap.get(keyCode);
		if (old != null) {
			entityMap.get(old).setKeyCode(Character.MIN_VALUE);
			old.setKey(null);
			nodeChanged(entityMap.get(old));
		}
		keyMap.put(keyCode, e);
		entityMap.get(e).setKeyCode(keyCode);
		e.setKey(String.valueOf(keyCode));
		nodeChanged(entityMap.get(e));
	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	}

	public void removeDetachedMentionPart(Mention m, DetachedMentionPart dmp) {
		m.setDiscontinuous(null);
		removeNodeFromParent(mentionMap.get(dmp));
		mentionMap.remove(dmp);
		dmp.removeFromIndexes();
		fireMentionChangedEvent(m);
	}

	public DetachedMentionPart removeDiscontinuousMentionPart(Mention m) {
		DetachedMentionPart dmp = m.getDiscontinuous();
		m.setDiscontinuous(null);

		removeNodeFromParent(mentionMap.get(dmp));
		return dmp;
	}

	public void removeEntity(Entity e) {
		removeNodeFromParent(entityMap.get(e));
		e.removeFromIndexes();
		entityMap.remove(e);
		String k = e.getKey();
		if (k != null)
			keyMap.remove(k.charAt(0));
	}

	public void removeEntityFromGroup(EntityGroup eg, EntityTreeNode e) {
		removeNodeFromParent(e);
		FSArray oldArray = eg.getMembers();
		FSArray arr = new FSArray(jcas, eg.getMembers().size() - 1);
		for (int i = 0; i < oldArray.size() - 1; i++) {
			if (eg.getMembers(i) == e.getFeatureStructure()) {
				i--;
			} else {
				arr.set(i, eg.getMembers(i));
			}
		}
		eg.setMembers(arr);
	}

	public void removeEntityGroup(EntityGroup eg) {
		EntityTreeNode etn = entityMap.get(eg);
		for (int i = 0; i < etn.getChildCount(); i++) {
			FeatureStructure fs = etn.getChildAt(i).getFeatureStructure();
			if (fs instanceof Mention)
				removeMention((Mention) fs);
		}
		removeNodeFromParent(etn);
		eg.removeFromIndexes();

	}

	public void removeMention(Mention m) {
		CATreeNode parent = (CATreeNode) mentionMap.get(m).getParent();
		int index = parent.getIndex(mentionMap.get(m));
		parent.remove(mentionMap.get(m));
		nodesWereRemoved(parent, new int[] { index }, new Object[] { mentionMap.get(m) });
		fireMentionRemovedEvent(m);
		mentionMap.remove(m);
		m.removeFromIndexes();
	}

	public void resort() {
		resort(entitySortOrder.getComparator());
	}

	public void resort(Comparator<EntityTreeNode> comparator) {
		int n = rootNode.getChildCount();
		List<EntityTreeNode> children = new ArrayList<EntityTreeNode>(n);
		List<CATreeNode> dontsort = new ArrayList<CATreeNode>(n);

		for (int i = 0; i < n; i++) {
			if (rootNode.getChildAt(i) instanceof EntityTreeNode)
				children.add((EntityTreeNode) rootNode.getChildAt(i));
			else
				dontsort.add((CATreeNode) rootNode.getChildAt(i));
		}
		children.sort(comparator);
		rootNode.removeAllChildren();
		for (MutableTreeNode node : children) {
			rootNode.add(node);
		}
		for (MutableTreeNode node : dontsort)
			rootNode.add(node);
		nodeChanged(rootNode);
		nodeStructureChanged(rootNode);
	}

	public void toggleFlagEntity(Entity m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		nodeChanged(mentionMap.get(m));
		// fireMentionChangedEvent(m);
		// fireMentionSelectedEvent(m);
	}

	public void toggleFlagMention(Mention m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		nodeChanged(mentionMap.get(m));
		fireMentionChangedEvent(m);
		fireMentionSelectedEvent(m);
	}

	public void updateColor(Entity entity, Color newColor) {
		entity.setColor(newColor.getRGB());
		EntityTreeNode entityNode = entityMap.get(entity);
		this.nodeChanged(entityNode);
		for (int i = 0; i < entityNode.getChildCount(); i++) {
			FeatureStructure child = entityNode.getChildAt(i).getFeatureStructure();
			if (child instanceof Annotation)
				fireMentionChangedEvent((Mention) child);
		}
	}

	public void updateMention(Mention m, Entity newEntity) {
		// remove mention from old entity
		removeNodeFromParent(mentionMap.get(m));
		mentionMap.remove(m);

		// attach it to the new entity
		connect(newEntity, m);

		// fire event
		this.fireMentionChangedEvent(m);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

}
