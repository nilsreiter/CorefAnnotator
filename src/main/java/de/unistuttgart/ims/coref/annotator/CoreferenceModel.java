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

import javax.swing.Icon;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.tools.cvd.ColorIcon;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModel extends DefaultTreeModel implements KeyListener {
	JCas jcas;
	private static final long serialVersionUID = 1L;
	Map<Entity, EntityTreeNode> entityMap = new HashMap<Entity, EntityTreeNode>();
	Map<Mention, TreeNode<Mention>> mentionMap = new HashMap<Mention, TreeNode<Mention>>();
	Map<Character, Entity> keyMap = new HashMap<Character, Entity>();
	HashSetValuedHashMap<Entity, Mention> entityMentionMap = new HashSetValuedHashMap<Entity, Mention>();
	ColorMap colorMap = new ColorMap();

	List<CoreferenceModelListener> listeners = new LinkedList<CoreferenceModelListener>();

	int key = 0;

	char[] keyCodes = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	TreeNode<TOP> rootNode;

	EntitySortOrder entitySortOrder = EntitySortOrder.Alphabet;

	boolean keepEmptyEntities = true;

	@SuppressWarnings("unchecked")
	public CoreferenceModel(JCas jcas) {
		super(new TreeNode<TOP>(null, "Add new entity"));
		this.rootNode = (TreeNode<TOP>) getRoot();
		this.jcas = jcas;

	}

	public void importExistingData() {
		for (Entity e : JCasUtil.select(jcas, Entity.class)) {
			addExistingEntity(e);
		}
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			connect(m.getEntity(), m);
			fireMentionAddedEvent(m);
		}
	}

	public void updateMention(Mention m, Entity newEntity) {
		Entity oldEntity = m.getEntity();
		entityMentionMap.get(oldEntity).remove(m);
		this.removeNodeFromParent(mentionMap.get(m));
		mentionMap.remove(m);
		connect(newEntity, m);
		if (entityMentionMap.get(oldEntity).isEmpty() && !keepEmptyEntities) {
			removeEntity(oldEntity);
		}
		this.fireMentionChangedEvent(m);
	}

	public void removeEntity(Entity e) {
		removeNodeFromParent(entityMap.get(e));
		e.removeFromIndexes();
		int k = entityMap.remove(e).getKeyCode();
		keyMap.remove(k);
		entityMentionMap.remove(e);
	}

	public void addNewEntityMention(int begin, int end) {
		String covered = jcas.getDocumentText().substring(begin, end);
		Entity e = new Entity(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(covered);
		e.addToIndexes();
		EntityTreeNode tn = new EntityTreeNode(e, covered);

		int ind = 0;
		Comparator<TreeNode<Entity>> comparator = entitySortOrder.getComparator();
		while (ind < this.rootNode.getChildCount()) {
			@SuppressWarnings("unchecked")
			TreeNode<Entity> node = (TreeNode<Entity>) rootNode.getChildAt(ind);
			if (comparator.compare(tn, node) <= 0)
				break;
			ind++;
		}

		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), ind);
		entityMap.put(e, tn);
		if (key < keyCodes.length) {
			tn.setKeyCode(keyCodes[key]);
			keyMap.put(keyCodes[key++], e);
		}

		addNewMention(e, begin, end);
	}

	public void addExistingEntity(Entity e) {
		EntityTreeNode tn = new EntityTreeNode(e, "");
		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), 0);
		entityMap.put(e, tn);
		if (key < keyCodes.length) {
			tn.setKeyCode(keyCodes[key]);
			keyMap.put(keyCodes[key++], e);
		}
	}

	protected void connect(Entity e, Mention m) {
		if (!entityMap.containsKey(e))
			addExistingEntity(e);

		m.setEntity(e);
		entityMentionMap.put(e, m);
		TreeNode<Mention> tn = new TreeNode<Mention>(m, m.getCoveredText());
		mentionMap.put(m, tn);
		this.insertNodeInto(tn, entityMap.get(e), 0);
	}

	public void addNewMention(Entity e, int begin, int end) {
		Mention m = AnnotationFactory.createAnnotation(jcas, begin, end, Mention.class);
		connect(e, m);
		fireMentionAddedEvent(m);
	}

	public JCas getJcas() {
		return jcas;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.err.println("keychar: " + e.getKeyChar());
		JTextArea ta = (JTextArea) e.getSource();
		if (keyMap.containsKey(e.getKeyChar())) {
			e.consume();
			addNewMention(keyMap.get(e.getKeyChar()), ta.getSelectionStart(), ta.getSelectionEnd());
		} else if (e.getKeyChar() == 'n') {
			addNewEntityMention(ta.getSelectionStart(), ta.getSelectionEnd());
		}
	}

	@Deprecated
	public DefaultHighlighter.DefaultHighlightPainter getPainter(Entity e) {
		return new UnderlinePainter(new Color(e.getColor()));
	}

	@Deprecated
	public Icon getIcon(Entity e) {
		return new ColorIcon(new Color(e.getColor()));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Deprecated
	public ColorMap getColorMap() {
		return colorMap;
	}

	public boolean isKeyUsed(int i) {
		return keyMap.containsKey(i);
	}

	@SuppressWarnings("unchecked")
	public void resort() {
		int n = rootNode.getChildCount();
		List<TreeNode<Entity>> children = new ArrayList<TreeNode<Entity>>(n);
		for (int i = 0; i < n; i++) {
			children.add((TreeNode<Entity>) rootNode.getChildAt(i));
		}
		children.sort(entitySortOrder.getComparator());
		rootNode.removeAllChildren();
		for (MutableTreeNode node : children) {
			rootNode.add(node);
		}
		nodeChanged(rootNode);
		nodeStructureChanged(rootNode);
	}

	public void reassignKey(char keyCode, Entity e) {
		Entity old = keyMap.get(keyCode);
		if (old != null) {
			entityMap.get(old).setKeyCode(Character.MIN_VALUE);
			this.nodeChanged(entityMap.get(old));
		}
		keyMap.put(keyCode, e);
		entityMap.get(e).setKeyCode(keyCode);
		this.nodeChanged(entityMap.get(e));
	}

	public void updateColor(Entity entity, Color newColor) {
		// colorMap.put(entity, newColor);
		entity.setColor(newColor.getRGB());
		this.nodeChanged(entityMap.get(entity));
		for (Mention m : entityMentionMap.get(entity))
			fireMentionChangedEvent(m);
	}

	public void fireMentionChangedEvent(Mention m) {
		for (CoreferenceModelListener l : listeners)
			l.mentionChanged(m);
	}

	public void fireMentionAddedEvent(Mention m) {
		for (CoreferenceModelListener l : listeners)
			l.mentionAdded(m);

	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return listeners.add(e);
	}

	public boolean removeCoreferenceModelListener(Object o) {
		return listeners.remove(o);
	}

}
