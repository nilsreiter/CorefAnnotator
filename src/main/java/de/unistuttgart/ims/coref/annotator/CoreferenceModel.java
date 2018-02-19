package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class CoreferenceModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;
	RangedHashSetValuedHashMap<Annotation> characterPosition2AnnotationMap = new RangedHashSetValuedHashMap<Annotation>();
	ColorProvider colorMap = new ColorProvider();
	HashSetValuedHashMap<FeatureStructure, Comment> comments = new HashSetValuedHashMap<FeatureStructure, Comment>();
	MutableList<CoreferenceModelListener> crModelListeners = Lists.mutable.empty();
	Map<FeatureStructure, CATreeNode> fsMap = Maps.mutable.empty();
	EntitySortOrder entitySortOrder = EntitySortOrder.Mentions;

	JCas jcas;
	boolean keepEmptyEntities = true;

	int key = 0;

	Map<Character, Entity> keyMap = Maps.mutable.empty();

	Preferences preferences;

	CATreeNode rootNode;

	public CoreferenceModel(JCas jcas, Preferences preferences) {
		super(new CATreeNode(null, Annotator.getString("tree.root")));
		this.rootNode = (CATreeNode) getRoot();
		this.jcas = jcas;
		this.preferences = preferences;

	}

	public CATreeNode add(DetachedMentionPart dmp) {
		CATreeNode node = new CATreeNode(dmp, dmp.getCoveredText());
		fsMap.put(dmp, node);
		return node;
	}

	public CATreeNode add(Mention m) {
		CATreeNode node = new CATreeNode(m, m.getCoveredText());
		fsMap.put(m, node);
		characterPosition2AnnotationMap.add(m);
		return node;
	}

	public CATreeNode add(Entity e) {
		CATreeNode tn = new CATreeNode(e, "");
		insertNodeInto(tn, rootNode, 0);
		fsMap.put(e, tn);
		if (e.getKey() != null) {
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

	public void addTo(CATreeNode entityNode, CATreeNode mentionNode) {

		Mention m = mentionNode.getFeatureStructure();
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
			CATreeNode discNode = getOrCreate(m.getDiscontinuous());
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
		insertNodeInto(new CATreeNode(e, e.getLabel()), fsMap.get(eg), 0);
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
		crModelListeners.forEach(l -> l.annotationAdded(m));
	}

	public void fireAnnotationChangedEvent(Annotation m) {
		crModelListeners.forEach(l -> l.annotationChanged(m));
	}

	public void fireAnnotationRemovedEvent(Annotation m) {
		crModelListeners.forEach(l -> l.annotationRemoved(m));
	}

	public void fireMentionSelectedEvent(Mention m) {
		crModelListeners.forEach(l -> l.annotationSelected(m));
	}

	public void formGroup(Entity e1, Entity e2) {
		EntityGroup eg = createEntityGroup(e1.getLabel() + " and " + e2.getLabel(), 2);
		eg.setMembers(0, e1);
		eg.setMembers(1, e2);

		CATreeNode gtn = add(eg);

		insertNodeInto(new CATreeNode(e1, e1.getLabel()), gtn, 0);
		insertNodeInto(new CATreeNode(e2, e2.getLabel()), gtn, 1);

	}

	public JCas getJcas() {
		return jcas;
	}

	public CATreeNode get(DetachedMentionPart m) {
		return fsMap.get(m);
	}

	public CATreeNode getOrCreate(DetachedMentionPart m) {
		if (!fsMap.containsKey(m))
			return add(m);
		return fsMap.get(m);
	}

	public CATreeNode get(Mention m) {
		return fsMap.get(m);
	}

	public CATreeNode get(Entity e) {
		return fsMap.get(e);
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
			old.setKey(null);
			nodeChanged(fsMap.get(old));
		}
		keyMap.put(keyCode, e);
		e.setKey(String.valueOf(keyCode));
		nodeChanged(fsMap.get(e));
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
		fsMap.remove(m);
		m.removeFromIndexes();
	}

	protected void remove(DetachedMentionPart m, CATreeNode node) {
		m.getMention().setDiscontinuous(null);
		m.setMention(null);

		// tree
		removeNodeFromParent(node);
		fireAnnotationRemovedEvent(m);

		// document
		fsMap.remove(m);
		m.removeFromIndexes();
	}

	protected void remove(Entity e, CATreeNode etn) {
		for (int i = 0; i < etn.getChildCount(); i++) {
			etn.removeAllChildren();
			CATreeNode n = etn.getChildAt(i);
			if (n.getFeatureStructure() instanceof Mention)
				remove((Mention) n.getFeatureStructure(), n);
		}
		removeNodeFromParent(etn);
		fsMap.remove(e);
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

		removeNodeFromParent(fsMap.get(dmp));
		return dmp;
	}

	public void remove(Entity e) {
		remove(e, get(e));
	}

	public void removeFrom(EntityGroup eg, CATreeNode e) {
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
		CATreeNode etn = fsMap.get(eg);
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
		characterPosition2AnnotationMap.remove(m);
	}

	public void resort() {
		resort(entitySortOrder.getComparator());
	}

	public void resort(Comparator<CATreeNode> comparator) {
		rootNode.getChildren().sort(comparator);
		nodeStructureChanged(rootNode);
	}

	public void toggleFlagEntity(Entity m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		nodeChanged(fsMap.get(m));
		// fireMentionChangedEvent(m);
		// fireMentionSelectedEvent(m);
	}

	public void toggleFlagMention(Mention m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		nodeChanged(fsMap.get(m));
		fireAnnotationChangedEvent(m);
		fireMentionSelectedEvent(m);
	}

	public void updateColor(Entity entity, Color newColor) {
		entity.setColor(newColor.getRGB());
		CATreeNode entityNode = fsMap.get(entity);
		this.nodeChanged(entityNode);
		for (int i = 0; i < entityNode.getChildCount(); i++) {
			FeatureStructure child = entityNode.getChildAt(i).getFeatureStructure();
			if (child instanceof Annotation)
				fireAnnotationChangedEvent((Mention) child);
		}
	}

	public void update(Entity entity, boolean displayed) {
		CATreeNode node = get(entity);
		for (int i = 0; i < node.getChildCount(); i++) {
			CATreeNode child = node.getChildAt(i);
			if (child.isMention())
				if (displayed)
					fireMentionAddedEvent(child.getFeatureStructure());
				else
					fireAnnotationRemovedEvent(child.getFeatureStructure());

		}
	};

	public void merge(CATreeNode e1, CATreeNode e2) {
		CATreeNode bigger, smaller;
		if (e1.getChildCount() >= e2.getChildCount()) {
			bigger = e1;
			smaller = e2;
		} else {
			smaller = e1;
			bigger = e2;
		}
		for (int i = 0; i < smaller.getChildCount();) {
			CATreeNode node = smaller.getChildAt(i);
			if (node.getFeatureStructure() instanceof Mention) {
				Mention m = (Mention) node.getFeatureStructure();
				moveTo(m, bigger.getFeatureStructure());
			} else {
				i++;
			}
		}
		remove(smaller.getEntity());
	}

	public void moveTo(Mention m, Entity newEntity) {
		CATreeNode mentionNode = get(m);
		// remove mention from old entity
		removeNodeFromParent(mentionNode);
		fsMap.remove(m);

		// attach it to the new entity
		addTo(get(newEntity), mentionNode);

		// fire event
		fireAnnotationChangedEvent(m);
	}

}
