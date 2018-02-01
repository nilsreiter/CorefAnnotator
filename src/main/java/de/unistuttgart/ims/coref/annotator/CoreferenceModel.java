package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.tools.cvd.ColorIcon;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModel extends DefaultTreeModel implements KeyListener {
	JCas jcas;
	CasTextView textView;
	private static final long serialVersionUID = 1L;
	Map<Entity, EntityTreeNode> entityMap = new HashMap<Entity, EntityTreeNode>();
	Map<Mention, TreeNode<Mention>> mentionMap = new HashMap<Mention, TreeNode<Mention>>();
	Map<Character, Entity> keyMap = new HashMap<Character, Entity>();
	HashSetValuedHashMap<Entity, Mention> entityMentionMap = new HashSetValuedHashMap<Entity, Mention>();
	ColorMap colorMap = new ColorMap();

	int key = 0;

	char[] keyCodes = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public CoreferenceModel(JCas jcas, CasTextView ctw) {
		super(new TreeNode<TOP>(null, "Add new entity"));
		this.jcas = jcas;
		this.textView = ctw;
	}

	public void updateMention(Mention m, Entity newEntity) {
		Entity oldEntity = m.getEntity();
		entityMentionMap.get(oldEntity).remove(m);
		this.removeNodeFromParent(mentionMap.get(m));
		mentionMap.remove(m);
		connect(newEntity, m);
		if (entityMentionMap.get(oldEntity).isEmpty()) {
			removeEntity(oldEntity);
		}

	}

	public void removeEntity(Entity e) {
		removeNodeFromParent(entityMap.get(e));
		e.removeFromIndexes();
		int k = entityMap.remove(e).getKeyCode();
		keyMap.remove(k);
		entityMentionMap.remove(e);
	}

	public void addEntityMention(int begin, int end) {
		Entity e = new Entity(jcas);
		e.addToIndexes();
		EntityTreeNode tn = new EntityTreeNode(e, jcas.getDocumentText().substring(begin, end));
		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), 0);
		entityMap.put(e, tn);
		if (key < keyCodes.length) {
			tn.setKeyCode(keyCodes[key]);
			keyMap.put(keyCodes[key++], e);
		}

		addLink(e, begin, end);
	}

	public void addEntity(Entity e) {
		EntityTreeNode tn = new EntityTreeNode(e, "");
		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), 0);
		entityMap.put(e, tn);
		if (key < keyCodes.length) {
			tn.setKeyCode(keyCodes[key]);
			keyMap.put(keyCodes[key++], e);
		}
	}

	public void connect(Entity e, Mention m) {
		if (!entityMap.containsKey(e))
			addEntity(e);

		m.setEntity(e);
		entityMentionMap.put(e, m);
		TreeNode<Mention> tn = new TreeNode<Mention>(m, m.getCoveredText());
		mentionMap.put(m, tn);
		this.insertNodeInto(tn, entityMap.get(e), 0);
		textView.drawAnnotation(m);

	}

	public TreePath addLink(Entity e, int begin, int end) {
		Mention m = AnnotationFactory.createAnnotation(jcas, begin, end, Mention.class);
		connect(e, m);
		return new TreePath(this.getPathToRoot(mentionMap.get(m)));
	}

	public JCas getJcas() {
		return jcas;
	}

	public CasTextView getTextView() {
		return textView;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.err.println("keychar: " + e.getKeyChar());
		if (keyMap.containsKey(e.getKeyChar())) {
			e.consume();
			addLink(keyMap.get(e.getKeyChar()), textView.getTextPane().getSelectionStart(),
					textView.getTextPane().getSelectionEnd());
		} else if (e.getKeyChar() == 'n') {
			addEntityMention(textView.getTextPane().getSelectionStart(), textView.getTextPane().getSelectionEnd());
		}
	}

	public DefaultHighlighter.DefaultHighlightPainter getPainter(Entity e) {
		return new UnderlinePainter(colorMap.get(e));
	}

	public Icon getIcon(Entity e) {
		return new ColorIcon(colorMap.get(e));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public ColorMap getColorMap() {
		return colorMap;
	}

	public boolean isKeyUsed(int i) {
		return keyMap.containsKey(i);
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
		colorMap.put(entity, newColor);
		this.nodeChanged(entityMap.get(entity));
		for (Mention m : entityMentionMap.get(entity))
			textView.drawAnnotation(m);
	}

}
