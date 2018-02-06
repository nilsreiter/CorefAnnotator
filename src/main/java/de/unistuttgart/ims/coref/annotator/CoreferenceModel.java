package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModel extends DefaultTreeModel implements KeyListener, TreeSelectionListener {
	JCas jcas;
	private static final long serialVersionUID = 1L;
	Map<FeatureStructure, EntityTreeNode> entityMap = new HashMap<FeatureStructure, EntityTreeNode>();
	Map<FeatureStructure, CATreeNode> mentionMap = new HashMap<FeatureStructure, CATreeNode>();
	Map<Character, Entity> keyMap = new HashMap<Character, Entity>();
	HashSetValuedHashMap<Entity, Mention> entityMentionMap = new HashSetValuedHashMap<Entity, Mention>();
	ColorMap colorMap = new ColorMap();

	List<CoreferenceModelListener> crModelListeners = new LinkedList<CoreferenceModelListener>();

	int key = 0;

	char[] keyCodes = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	CATreeNode rootNode;
	CATreeNode groupRootNode;

	EntitySortOrder entitySortOrder = EntitySortOrder.Alphabet;

	boolean keepEmptyEntities = true;

	public CoreferenceModel(JCas jcas) {
		super(new CATreeNode(null, "Add new entity"));
		this.rootNode = (CATreeNode) getRoot();
		this.groupRootNode = new CATreeNode(null, "Groups");
		this.insertNodeInto(groupRootNode, rootNode, 0);
		this.jcas = jcas;

	}

	public void importExistingData() {
		for (Entity e : JCasUtil.select(jcas, Entity.class)) {
			addExistingEntity(e);
		}
		for (EntityGroup eg : JCasUtil.select(jcas, EntityGroup.class)) {
			for (int i = 0; i < eg.getMembers().size(); i++) {
				this.insertNodeInto(new EntityTreeNode(eg.getMembers(i)), entityMap.get(eg), 0);
			}

		}
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			connect(m.getEntity(), m);

			fireMentionAddedEvent(m);
		}
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

	public void updateMention(Mention m, Entity newEntity) {
		// remove mention from old entity
		Entity oldEntity = m.getEntity();
		entityMentionMap.get(oldEntity).remove(m);
		removeNodeFromParent(mentionMap.get(m));
		mentionMap.remove(m);

		// attach it to the new entity
		connect(newEntity, m);
		if (entityMentionMap.get(oldEntity).isEmpty() && !keepEmptyEntities) {
			removeEntity(oldEntity);
		}

		// fire event
		this.fireMentionChangedEvent(m);
	}

	public void removeEntity(Entity e) {
		removeNodeFromParent(entityMap.get(e));
		e.removeFromIndexes();
		int k = entityMap.remove(e).getKeyCode();
		keyMap.remove(k);
		entityMentionMap.remove(e);
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

	public EntityTreeNode addExistingEntity(Entity e) {
		EntityTreeNode tn = new EntityTreeNode(e, "");
		if (e instanceof EntityGroup) {
			insertNodeInto(tn, groupRootNode, 0);
		} else
			insertNodeInto(tn, rootNode, 0);
		entityMap.put(e, tn);
		if (e.getKey() != null) {
			tn.setKeyCode(e.getKey().charAt(0));
			keyMap.put(e.getKey().charAt(0), e);
		}
		return tn;
	}

	protected void connect(Entity e, Mention m) {
		if (!entityMap.containsKey(e))
			addExistingEntity(e);

		m.setEntity(e);
		entityMentionMap.put(e, m);
		CATreeNode tn = new CATreeNode(m, m.getCoveredText());
		mentionMap.put(m, tn);
		int ind = 0;
		while (ind < entityMap.get(e).getChildCount()) {
			CATreeNode node = (CATreeNode) entityMap.get(e).getChildAt(ind);
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
	}

	public void addNewMention(Entity e, int begin, int end) {
		Mention m = AnnotationFactory.createAnnotation(jcas, begin, end, Mention.class);
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

	public void formGroup(Entity e1, Entity e2) {
		FSArray arr = new FSArray(jcas, 2);
		arr.set(0, e1);
		arr.set(1, e2);
		EntityGroup eg = new EntityGroup(jcas);
		eg.setColor(colorMap.nextColor);
		if (e1.getLabel() != null && e2.getLabel() != null)
			eg.setLabel(e1.getLabel() + " and " + e2.getLabel());
		else
			eg.setLabel("A group");
		eg.setMembers(arr);
		eg.addToIndexes();

		CATreeNode gtn = addExistingEntity(eg);
		this.insertNodeInto(new EntityTreeNode(e1), gtn, 0);
		this.insertNodeInto(new EntityTreeNode(e2), gtn, 1);

	}

	public JCas getJcas() {
		return jcas;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.err.println("keychar: " + e.getKeyChar());
		JTextComponent ta = (JTextComponent) e.getSource();
		if (keyMap.containsKey(e.getKeyChar())) {
			e.consume();
			addNewMention(keyMap.get(e.getKeyChar()), ta.getSelectionStart(), ta.getSelectionEnd());
		} /*
			 * else if (e.getKeyChar() == 'n') {
			 * addNewEntityMention(ta.getSelectionStart(),
			 * ta.getSelectionEnd()); }
			 */
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public boolean isKeyUsed(int i) {
		return keyMap.containsKey(i);
	}

	public DetachedMentionPart removeDiscontinuousMentionPart(Mention m) {
		DetachedMentionPart dmp = m.getDiscontinuous();
		m.setDiscontinuous(null);

		removeNodeFromParent(mentionMap.get(dmp));
		return dmp;
	}

	public void addDiscontinuousToMention(Mention m, DetachedMentionPart dmp) {
		m.setDiscontinuous(dmp);
		insertNodeInto(mentionMap.get(dmp), mentionMap.get(m), 0);
	}

	public void addDiscontinuousToMention(Mention m, int begin, int end) {
		DetachedMentionPart d = AnnotationFactory.createAnnotation(jcas, begin, end, DetachedMentionPart.class);
		m.setDiscontinuous(d);
		CATreeNode node = new CATreeNode(d, d.getCoveredText());
		mentionMap.put(d, node);
		insertNodeInto(node, mentionMap.get(m), 0);
		fireMentionChangedEvent(m);
	}

	public void resort() {
		int n = rootNode.getChildCount();
		List<EntityTreeNode> children = new ArrayList<EntityTreeNode>(n);
		List<CATreeNode> dontsort = new ArrayList<CATreeNode>(n);

		for (int i = 0; i < n; i++) {
			if (rootNode.getChildAt(i) instanceof EntityTreeNode)
				children.add((EntityTreeNode) rootNode.getChildAt(i));
			else
				dontsort.add((CATreeNode) rootNode.getChildAt(i));
		}
		children.sort(entitySortOrder.getComparator());
		rootNode.removeAllChildren();
		for (MutableTreeNode node : children) {
			rootNode.add(node);
		}
		for (MutableTreeNode node : dontsort)
			rootNode.add(node);
		nodeChanged(rootNode);
		nodeStructureChanged(rootNode);
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

	public void updateColor(Entity entity, Color newColor) {
		// colorMap.put(entity, newColor);
		entity.setColor(newColor.getRGB());
		this.nodeChanged(entityMap.get(entity));
		for (Mention m : entityMentionMap.get(entity))
			fireMentionChangedEvent(m);
	}

	public void fireMentionChangedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionChanged(m);
	}

	public void fireMentionAddedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionAdded(m);
	}

	public void fireMentionRemovedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionRemoved(m);
	}

	public void fireMentionSelectedEvent(Mention m) {
		for (CoreferenceModelListener l : crModelListeners)
			l.mentionSelected(m);
	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return crModelListeners.add(e);
	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

	public void removeMention(Mention m) {
		CATreeNode parent = (CATreeNode) mentionMap.get(m).getParent();
		int index = parent.getIndex(mentionMap.get(m));
		parent.remove(mentionMap.get(m));
		// removeNodeFromParent(mentionMap.get(m));
		nodesWereRemoved(parent, new int[] { index }, new Object[] { mentionMap.get(m) });
		fireMentionRemovedEvent(m);
		entityMentionMap.get(m.getEntity()).remove(m);
		mentionMap.remove(m);
		m.removeFromIndexes();
	}

}
