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

	public CATreeNode add(DetachedMentionPart dmp) {
		CATreeNode node = new CATreeNode(dmp, dmp.getCoveredText());
		mentionMap.put(dmp, node);
		return node;
	}

	public CATreeNode add(Mention m) {
		CATreeNode node = new CATreeNode(m, m.getCoveredText());
		mentionMap.put(m, node);
		return node;
	}

	public EntityTreeNode add(Entity e) {
		EntityTreeNode tn = new EntityTreeNode(e, "");
		insertNodeInto(tn, rootNode, 0);
		entityMap.put(e, tn);
		if (e.getKey() != null) {
			tn.setKeyCode(e.getKey().charAt(0));
			keyMap.put(e.getKey().charAt(0), e);
		}
		return tn;
	}

	public void add(int begin, int end) {
		// document model
		Mention m = createMention(begin, end);
		Entity e = createEntity(m.getCoveredText());

		// tree model
		addTo(add(e), add(m));
	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return crModelListeners.add(e);
	}

	public void addTo(Mention m, DetachedMentionPart dmp) {
		// tree model
		CATreeNode node = add(dmp);
		insertNodeInto(node, get(m), 0);

		// notify text view
		fireAnnotationChangedEvent(m);
	}

	public void addTo(Mention m, int begin, int end) {
		// document model
		DetachedMentionPart d = createDetachedMentionPart(begin, end);
		d.setMention(m);
		m.setDiscontinuous(d);

		addTo(m, d);
	}

	public void addTo(Entity e, int begin, int end) {
		Mention m = createMention(begin, end);
		addTo(get(e), add(m));
		fireMentionAddedEvent(m);
	}

	public void addTo(EntityTreeNode entityNode, CATreeNode mentionNode) {

		Mention m = (Mention) mentionNode.getFeatureStructure();
		Entity e = entityNode.getFeatureStructure();
		m.setEntity(e);
		CATreeNode tn = add(m);
		int ind = 0;
		while (ind < entityNode.getChildCount()) {
			CATreeNode node = entityNode.getChildAt(ind);
			if (node.getFeatureStructure() instanceof Entity
					|| ((Annotation) node.getFeatureStructure()).getBegin() > m.getBegin())
				break;
			ind++;
		}

		insertNodeInto(tn, entityNode, ind);

		if (m.getDiscontinuous() != null) {
			CATreeNode discNode = get(m.getDiscontinuous());
			insertNodeInto(discNode, tn, 0);
		}
		fireAnnotationChangedEvent(m);
		resort();
	}

	public void addTo(EntityGroup eg, Entity e) {
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

	protected DetachedMentionPart createDetachedMentionPart(int b, int e) {
		DetachedMentionPart dmp = AnnotationFactory.createAnnotation(jcas, b, e, DetachedMentionPart.class);
		if (preferences.getBoolean(Constants.CFG_TRIM_WHITESPACE, true))
			dmp = AnnotationUtil.trim(dmp);

		registerAnnotation(dmp);
		return dmp;
	}

	protected EntityGroup createEntityGroup(String l, int initialSize) {
		EntityGroup e = new EntityGroup(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(l);
		e.addToIndexes();
		e.setMembers(new FSArray(jcas, initialSize));
		return e;
	}

	protected Entity createEntity(String l) {
		Entity e = new Entity(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(l);
		e.addToIndexes();
		return e;
	}

	protected Mention createMention(int b, int e) {
		Mention m = AnnotationFactory.createAnnotation(jcas, b, e, Mention.class);
		if (preferences.getBoolean(Constants.CFG_TRIM_WHITESPACE, true))
			m = AnnotationUtil.trim(m);
		if (preferences.getBoolean(Constants.CFG_FULL_TOKENS, Defaults.CFG_FULL_TOKENS))
			m = Util.extend(m);
		registerAnnotation(m);
		return m;
	}

	public void fireMentionAddedEvent(Mention m) {
		this.characterPosition2AnnotationMap.add(m);
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionAdded(m);
	}

	public void fireAnnotationChangedEvent(Annotation m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.annotationChanged(m);
	}

	public void fireAnnotationRemovedEvent(Annotation m) {
		this.characterPosition2AnnotationMap.remove(m);
		for (CoreferenceModelListener l : crModelListeners)
			l.annotationRemoved(m);
	}

	public void fireMentionSelectedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.annotationSelected(m);
	}

	public void formGroup(Entity e1, Entity e2) {
		EntityGroup eg = createEntityGroup(e1.getLabel() + " and " + e2.getLabel(), 2);
		eg.setMembers(0, e1);
		eg.setMembers(1, e2);

		EntityTreeNode gtn = add(eg);

		insertNodeInto(new EntityTreeNode(e1), gtn, 0);
		insertNodeInto(new EntityTreeNode(e2), gtn, 1);

	}

	public JCas getJcas() {
		return jcas;
	}

	public CATreeNode get(DetachedMentionPart m) {
		return mentionMap.get(m);
	}

	public CATreeNode get(Mention m) {
		return mentionMap.get(m);
	}

	public EntityTreeNode get(Entity e) {
		return entityMap.get(e);
	}

	public Collection<Annotation> getMentions(int position) {
		return this.characterPosition2AnnotationMap.get(position);
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

	public void registerAnnotation(Annotation a) {
		characterPosition2AnnotationMap.add(a);
	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	}

	protected void remove(Mention m, CATreeNode node) {
		removeNodeFromParent(node);
		// nodesWereRemoved(node.getParent(), new int[] { index }, new Object[]
		// { mentionMap.get(m) });
		fireAnnotationRemovedEvent(m);

		// document
		mentionMap.remove(m);
		m.removeFromIndexes();
	}

	protected void remove(DetachedMentionPart m, CATreeNode node) {
		m.getMention().setDiscontinuous(null);
		m.setMention(null);

		// tree
		removeNodeFromParent(node);
		fireAnnotationRemovedEvent(m);

		// document
		mentionMap.remove(m);
		m.removeFromIndexes();
	}

	protected void remove(Entity e, EntityTreeNode etn) {
		for (int i = 0; i < etn.getChildCount(); i++) {
			etn.removeAllChildren();
			CATreeNode n = etn.getChildAt(i);
			if (n.getFeatureStructure() instanceof Mention)
				remove((Mention) n.getFeatureStructure(), n);
		}
		removeNodeFromParent(etn);
		entityMap.remove(e);
		String k = e.getKey();
		if (k != null)
			keyMap.remove(k.charAt(0));
		e.removeFromIndexes();
	}

	public void remove(DetachedMentionPart dmp) {
		remove(dmp, get(dmp));
	}

	public DetachedMentionPart removeFrom(Mention m) {
		DetachedMentionPart dmp = m.getDiscontinuous();
		m.setDiscontinuous(null);
		dmp.setMention(null);

		removeNodeFromParent(mentionMap.get(dmp));
		return dmp;
	}

	public void remove(Entity e) {
		remove(e, get(e));
	}

	public void removeFrom(EntityGroup eg, EntityTreeNode e) {
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

	public void remove(EntityGroup eg) {
		EntityTreeNode etn = entityMap.get(eg);
		for (int i = 0; i < etn.getChildCount(); i++) {
			FeatureStructure fs = etn.getChildAt(i).getFeatureStructure();
			if (fs instanceof Mention)
				remove((Mention) fs);
		}
		removeNodeFromParent(etn);
		eg.removeFromIndexes();

	}

	public void remove(Mention m) {
		remove(m, get(m));
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
		fireAnnotationChangedEvent(m);
		fireMentionSelectedEvent(m);
	}

	public void updateColor(Entity entity, Color newColor) {
		entity.setColor(newColor.getRGB());
		EntityTreeNode entityNode = entityMap.get(entity);
		this.nodeChanged(entityNode);
		for (int i = 0; i < entityNode.getChildCount(); i++) {
			FeatureStructure child = entityNode.getChildAt(i).getFeatureStructure();
			if (child instanceof Annotation)
				fireAnnotationChangedEvent((Mention) child);
		}
	}

	public void moveTo(Mention m, Entity newEntity) {
		CATreeNode mentionNode = get(m);
		// remove mention from old entity
		removeNodeFromParent(mentionNode);
		mentionMap.remove(m);

		// attach it to the new entity
		addTo(get(newEntity), mentionNode);

		// fire event
		fireAnnotationChangedEvent(m);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

}
