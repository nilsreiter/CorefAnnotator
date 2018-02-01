package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModel extends DefaultTreeModel {
	JCas jcas;
	CasTextView textView;
	private static final long serialVersionUID = 1L;
	Map<Entity, TreeNode<Entity>> entityMap = new HashMap<Entity, TreeNode<Entity>>();
	Map<Mention, TreeNode<Mention>> mentionMap = new HashMap<Mention, TreeNode<Mention>>();

	public CoreferenceModel(TreeNode<TOP> root, JCas jcas, CasTextView ctw) {
		super(root);
		this.jcas = jcas;
		this.textView = ctw;
	}

	public void updateMention(Mention m, Entity oldEntity, Entity newEntity) {
		this.removeNodeFromParent(mentionMap.get(m));
		mentionMap.remove(m);
		addLink(newEntity, m);
	}

	public void addEntityMention(int begin, int end) {
		Entity e = new Entity(jcas);
		e.addToIndexes();
		TreeNode<Entity> tn = new TreeNode<Entity>(e, jcas.getDocumentText().substring(begin, end));
		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), 0);
		entityMap.put(e, tn);

		addLink(e, begin, end);
	}

	public void addEntity(Entity e) {
		TreeNode<Entity> tn = new TreeNode<Entity>(e, "");
		insertNodeInto(tn, (TreeNode<?>) this.getRoot(), 0);
		entityMap.put(e, tn);

	}

	public void addLink(Entity e, Mention m) {
		if (!entityMap.containsKey(e))
			addEntity(e);

		m.setEntity(e);
		TreeNode<Mention> tn = new TreeNode<Mention>(m, m.getCoveredText());
		mentionMap.put(m, tn);
		this.insertNodeInto(tn, entityMap.get(e), 0);
		textView.drawAnnotation(m);

	}

	public void addLink(Entity e, int begin, int end) {
		Mention m = AnnotationFactory.createAnnotation(jcas, begin, end, Mention.class);
		addLink(e, m);
	}

}
