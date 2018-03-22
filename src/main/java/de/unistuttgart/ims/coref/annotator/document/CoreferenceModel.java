package de.unistuttgart.ims.coref.annotator.document;

import java.awt.Color;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.RangedHashSetValuedHashMap;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.Op.AddEntityToEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.Op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.AttachPart;
import de.unistuttgart.ims.coref.annotator.document.Op.GroupEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.MergeEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.MoveMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveEntitiesFromEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveMention;
import de.unistuttgart.ims.coref.annotator.document.Op.RemovePart;
import de.unistuttgart.ims.coref.annotator.document.Op.RenameEntity;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

/**
 * Class represents the document and the tree view on the document. All
 * annotation happens through this class.
 * 
 *
 */
public class CoreferenceModel {

	/**
	 * A mapping from character positions to annotations
	 */
	RangedHashSetValuedHashMap<Annotation> characterPosition2AnnotationMap = new RangedHashSetValuedHashMap<Annotation>();

	/**
	 * Assigns colors to new entities
	 */
	ColorProvider colorMap = new ColorProvider();

	HashSetValuedHashMap<FeatureStructure, Comment> comments = new HashSetValuedHashMap<FeatureStructure, Comment>();

	/**
	 * A list of listeners to annotation events
	 */
	MutableList<CoreferenceModelListener> crModelListeners = Lists.mutable.empty();

	MutableSetMultimap<Object, Mention> entityMentionMap = Multimaps.mutable.set.empty();

	/**
	 * The document
	 */
	JCas jcas;

	Preferences preferences;

	boolean initialised = false;

	Deque<Op> history = new LinkedList<Op>();

	public CoreferenceModel(JCas jcas, Preferences preferences) {
		this.jcas = jcas;
		this.preferences = preferences;
	}

	/**
	 * Create a new entity e and a new mention m, and add m to e.
	 * 
	 * @param begin
	 *            Begin of mention
	 * @param end
	 *            End of mention
	 * @return The new mention
	 */
	private Mention add(int begin, int end) {
		Annotator.logger.entry(begin, end);
		// document model
		Mention m = createMention(begin, end);
		Entity e = createEntity(m.getCoveredText());
		m.setEntity(e);
		entityMentionMap.put(e, m);
		fireEvent(Event.get(Event.Type.Add, null, e));
		fireEvent(Event.get(Event.Type.Add, e, m));

		return m;
	}

	private Mention add(Span selection) {
		return add(selection.begin, selection.end);
	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return crModelListeners.add(e);
	}

	private Mention addTo(Entity e, Span span) {
		return addTo(e, span.begin, span.end);
	}

	private Mention addTo(Entity e, int begin, int end) {
		Mention m = createMention(begin, end);
		m.setEntity(e);
		entityMentionMap.put(e, m);

		fireEvent(Event.get(Event.Type.Add, e, m));
		return m;
	}

	private DetachedMentionPart addTo(Mention m, int begin, int end) {
		// document model
		DetachedMentionPart d = createDetachedMentionPart(begin, end);
		d.setMention(m);
		m.setDiscontinuous(d);

		fireEvent(Event.get(Event.Type.Add, m, d));
		return d;
	}

	protected DetachedMentionPart createDetachedMentionPart(int b, int e) {
		DetachedMentionPart dmp = AnnotationFactory.createAnnotation(jcas, b, e, DetachedMentionPart.class);
		if (preferences.getBoolean(Constants.CFG_TRIM_WHITESPACE, true))
			dmp = AnnotationUtil.trim(dmp);

		registerAnnotation(dmp);
		return dmp;
	}

	protected Entity createEntity(String l) {
		Entity e = new Entity(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(l);
		e.setFlags(new StringArray(jcas, 0));
		e.addToIndexes();
		return e;
	}

	protected EntityGroup createEntityGroup(String l, int initialSize) {
		EntityGroup e = new EntityGroup(jcas);
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(l);
		e.setFlags(new StringArray(jcas, 0));
		e.addToIndexes();
		e.setMembers(new FSArray(jcas, initialSize));
		return e;
	}

	/**
	 * Creates a new mention annotation in the document and adds it to the indexes
	 * 
	 * @param b
	 *            the begin character position
	 * @param e
	 *            the end character position
	 * @return the created mention
	 */
	protected Mention createMention(int b, int e) {
		Mention m = AnnotationFactory.createAnnotation(jcas, b, e, Mention.class);
		if (preferences.getBoolean(Constants.CFG_TRIM_WHITESPACE, Defaults.CFG_TRIM_WHITESPACE))
			m = AnnotationUtil.trim(m);
		if (preferences.getBoolean(Constants.CFG_FULL_TOKENS, Defaults.CFG_FULL_TOKENS))
			m = Util.extend(m);
		registerAnnotation(m);
		return m;
	}

	public void edit(Op operation) {
		Annotator.logger.entry(operation);
		if (operation instanceof RenameEntity) {
			RenameEntity op = (RenameEntity) operation;
			op.getEntity().setLabel(op.getNewLabel());
			history.push(op);
		} else if (operation instanceof Op.AddEntityToEntityGroup) {
			Op.AddEntityToEntityGroup op = (AddEntityToEntityGroup) operation;
			MutableList<Entity> oldArr = Util.toList(op.getEntityGroup().getMembers());

			MutableList<Entity> newMembers = Lists.mutable.withAll(op.getEntities());
			newMembers.removeAll(oldArr);

			op.setEntities(newMembers.toImmutable());

			FSArray arr = new FSArray(jcas, op.getEntityGroup().getMembers().size() + newMembers.size());
			int i = 0;
			for (; i < op.getEntityGroup().getMembers().size(); i++) {
				arr.set(i, op.getEntityGroup().getMembers(i));
			}
			int oldSize = i;
			for (; i < arr.size(); i++) {
				arr.set(i, newMembers.get(i - oldSize));
			}
			arr.addToIndexes();
			op.getEntityGroup().removeFromIndexes();
			op.getEntityGroup().setMembers(arr);
			fireEvent(Event.get(Event.Type.Add, op.getEntityGroup(), op.getEntities()));
			history.add(op);
		} else if (operation instanceof Op.AddMentionsToNewEntity) {
			Op.AddMentionsToNewEntity op = (Op.AddMentionsToNewEntity) operation;
			for (Span span : op.getSpans()) {
				if (op.getEntity() == null)
					op.setEntity(add(span).getEntity());
				else
					addTo(op.getEntity(), span);
			}
			history.push(op);
		} else if (operation instanceof Op.AddMentionsToEntity) {
			Op.AddMentionsToEntity op = (Op.AddMentionsToEntity) operation;
			op.setMentions(op.getSpans().collect(sp -> addTo(op.getEntity(), sp)));
			history.push(op);
		} else if (operation instanceof Op.AttachPart) {
			Op.AttachPart op = (AttachPart) operation;
			op.setPart(addTo(op.getMention(), op.getSpan().begin, op.getSpan().end));
			history.push(op);
		} else if (operation instanceof Op.MoveMentionsToEntity) {
			Op.MoveMentionsToEntity op = (Op.MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getTarget(), m));
			history.push(op);
		} else if (operation instanceof Op.RemoveMention) {
			Op.RemoveMention op = (RemoveMention) operation;
			remove(op.getMention(), false);
			op.setMentions(null);
			history.push(op);
		} else if (operation instanceof Op.RemoveEntities) {
			Op.RemoveEntities op = (RemoveEntities) operation;
			op.getEntities().forEach(e -> remove(e));
			history.push(op);
		} else if (operation instanceof Op.RemoveEntitiesFromEntityGroup) {
			Op.RemoveEntitiesFromEntityGroup op = (RemoveEntitiesFromEntityGroup) operation;
			op.getEntities().forEach(e -> removeFrom(op.getEntityGroup(), e));
			history.push(op);
		} else if (operation instanceof Op.RemovePart) {
			Op.RemovePart op = (Op.RemovePart) operation;
			remove(op.getPart());
			history.push(op);
		} else if (operation instanceof Op.GroupEntities) {
			Op.GroupEntities op = (GroupEntities) operation;
			Annotator.logger.trace("Forming entity group with {}.", op.getEntities());
			EntityGroup eg = createEntityGroup(op.getEntities().subList(0, 2).select(e -> e.getLabel() != null)
					.collect(e -> e.getLabel()).makeString(" and "), op.getEntities().size());
			for (int i = 0; i < op.getEntities().size(); i++)
				eg.setMembers(i, op.getEntities().get(i));
			fireEvent(Event.get(Event.Type.Add, null, eg));
			op.setEntityGroup(eg);
			history.push(op);
		} else if (operation instanceof Op.MergeEntities) {
			Op.MergeEntities op = (MergeEntities) operation;
			MutableSetMultimap<Entity, Mention> currentState = Multimaps.mutable.set.empty();
			op.getEntities().forEach(e -> currentState.putAll(e, entityMentionMap.get(e)));
			op.setPreviousState(currentState.toImmutable());
			op.setEntity(merge(op.getEntities()));
			history.push(op);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void undo() {
		undo(history.pop());
	}

	protected void undo(Op operation) {
		Annotator.logger.entry(operation);
		if (operation instanceof Op.RenameEntity) {
			Op.RenameEntity op = (Op.RenameEntity) operation;
			op.getEntity().setLabel(op.getOldLabel());
		} else if (operation instanceof Op.AddEntityToEntityGroup) {
			Op.AddEntityToEntityGroup op = (AddEntityToEntityGroup) operation;
			op.getEntities().forEach(e -> removeFrom(op.getEntityGroup(), e));
		} else if (operation instanceof Op.AddMentionsToNewEntity) {
			Op.AddMentionsToNewEntity op = (Op.AddMentionsToNewEntity) operation;
			remove(op.getEntity());
		} else if (operation instanceof Op.AddMentionsToEntity) {
			Op.AddMentionsToEntity op = (AddMentionsToEntity) operation;
			op.getMentions().forEach(m -> remove(m, false));
		} else if (operation instanceof Op.AttachPart) {
			Op.AttachPart op = (AttachPart) operation;
			remove(op.getPart());
		} else if (operation instanceof Op.MoveMentionsToEntity) {
			Op.MoveMentionsToEntity op = (MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getSource(), m));
		} else if (operation instanceof Op.RemovePart) {
			Op.RemovePart op = (RemovePart) operation;
			addTo(op.getMention(), op.getSpan().begin, op.getSpan().end);
		} else if (operation instanceof Op.RemoveMention) {
			Op.RemoveMention op = (RemoveMention) operation;
			addTo(op.getEntity(), op.getSpan());
		} else if (operation instanceof Op.RemoveEntities) {
			Op.RemoveEntities op = (RemoveEntities) operation;
			op.getEntities().forEach(e -> {
				e.addToIndexes();
				fireEvent(Event.get(Event.Type.Add, null, e));
			});
		} else if (operation instanceof Op.RemoveEntitiesFromEntityGroup) {
			Op.RemoveEntitiesFromEntityGroup op = (RemoveEntitiesFromEntityGroup) operation;
			FSArray oldArr = op.getEntityGroup().getMembers();
			FSArray newArr = new FSArray(jcas, oldArr.size() + op.getEntities().size());
			int i = 0;
			for (; i < oldArr.size(); i++) {
				newArr.set(i, oldArr.get(i));
			}
			for (; i < newArr.size(); i++) {
				newArr.set(i, op.getEntities().get(i - oldArr.size()));
			}
		} else if (operation instanceof Op.MergeEntities) {
			Op.MergeEntities op = (MergeEntities) operation;
			for (Entity oldEntity : op.getEntities()) {
				if (op.getEntity() != oldEntity) {
					oldEntity.addToIndexes();
					fireEvent(Event.get(Event.Type.Add, null, oldEntity));
					for (Mention m : op.getPreviousState().get(oldEntity)) {
						moveTo(oldEntity, m);
					}
				}
			}
		} else if (operation instanceof Op.GroupEntities) {
			fireEvent(Event.get(Event.Type.Remove, null, ((Op.GroupEntities) operation).getEntities()));
		}
	}

	protected void fireEvent(FeatureStructureEvent event) {
		crModelListeners.forEach(l -> l.entityEvent(event));
	}

	public JCas getJCas() {
		return jcas;
	}

	/**
	 * Retrieve all annotations that cover the current character position
	 * 
	 * @param position
	 *            The character position
	 * @return A collection of annotations
	 */
	public Collection<Annotation> getMentions(int position) {
		return this.characterPosition2AnnotationMap.get(position);
	}

	public Preferences getPreferences() {
		return preferences;
	}

	private Entity merge(Iterable<Entity> nodes) {
		Entity biggest = null;
		int size = 0;
		for (Entity n : nodes) {
			if (entityMentionMap.get(n).size() > size) {
				size = entityMentionMap.get(n).size();
				biggest = n;
			}
		}
		final Entity tgt = biggest;
		if (biggest != null)
			for (Entity n : nodes) {
				if (n != tgt) {
					entityMentionMap.get(n).toSet().forEach(m -> moveTo(tgt, m));
					remove(n);
					fireEvent(Event.get(Event.Type.Remove, n));
				}
			}
		return biggest;
	}

	public void moveTo(DetachedMentionPart dmp, Mention m) {
		Mention old = dmp.getMention();
		dmp.setMention(m);
		fireEvent(Event.get(Event.Type.Move, old, m, dmp));
	}

	private void moveTo(Entity newEntity, Mention... mentions) {
		Entity oldEntity = null;
		for (Mention m : mentions) {
			oldEntity = m.getEntity();
			m.setEntity(newEntity);
			entityMentionMap.remove(oldEntity, m);
			entityMentionMap.put(newEntity, m);
		}
		fireEvent(Event.get(Event.Type.Move, oldEntity, newEntity, Lists.immutable.of(mentions)));
	}

	public void registerAnnotation(Annotation a) {
		characterPosition2AnnotationMap.add(a);
	}

	private void remove(DetachedMentionPart dmp) {
		fireEvent(Event.get(Event.Type.Remove, dmp.getMention(), dmp));
		dmp.getMention().setDiscontinuous(null);
		dmp.removeFromIndexes();
		characterPosition2AnnotationMap.remove(dmp);
	}

	private void remove(Entity entity) {
		fireEvent(Event.get(Event.Type.Remove, entity, entityMentionMap.get(entity).toList().toImmutable()));
		for (Mention m : entityMentionMap.get(entity)) {
			characterPosition2AnnotationMap.remove(m);
			m.removeFromIndexes();
		}
		fireEvent(Event.get(Event.Type.Remove, null, entity));
		entityMentionMap.removeAll(entity);
		entity.removeFromIndexes();

	}

	private void remove(Mention m, boolean autoRemove) {
		Entity entity = m.getEntity();
		characterPosition2AnnotationMap.remove(m);
		entityMentionMap.remove(entity, m);
		fireEvent(Event.get(Event.Type.Remove, m));
		m.removeFromIndexes();
		if (autoRemove && entityMentionMap.get(entity).isEmpty()
				&& preferences.getBoolean(Constants.CFG_DELETE_EMPTY_ENTITIES, Defaults.CFG_DELETE_EMPTY_ENTITIES)) {
			remove(entity);
		}

	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	};

	private void removeFrom(EntityGroup eg, Entity entity) {
		FSArray oldArray = eg.getMembers();
		FSArray arr = new FSArray(jcas, eg.getMembers().size() - 1);
		for (int i = 0; i < oldArray.size() - 1; i++) {
			if (eg.getMembers(i) == entity) {
				i--;
			} else {
				arr.set(i, eg.getMembers(i));
			}
		}
		eg.setMembers(arr);
		fireEvent(Event.get(Event.Type.Remove, eg, entity));
	}

	public void setHidden(Entity entity, boolean hidden) {
		entity.setHidden(hidden);
		fireEvent(Event.get(Event.Type.Update, entity, entityMentionMap.get(entity).toList().toImmutable()));
	}

	public void toggleFlagEntity(Entity m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		fireEvent(Event.get(Event.Type.Update, m));
	}

	public void toggleFlagMention(Mention m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		fireEvent(Event.get(Event.Type.Update, m));
	}

	public void toggleHidden(Entity entity) {
		setHidden(entity, !entity.getHidden());
	}

	public void updateColor(Entity entity, Color newColor) {
		entity.setColor(newColor.getRGB());
		fireEvent(Event.get(Event.Type.Update, entity, entityMentionMap.get(entity).toList().toImmutable()));
	}

	public void initialPainting() {
		if (initialised)
			return;
		for (Entity entity : JCasUtil.select(jcas, Entity.class)) {
			fireEvent(Event.get(Event.Type.Add, null, entity));
		}
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			entityMentionMap.put(mention.getEntity(), mention);
			mention.getEntity().addToIndexes();
			registerAnnotation(mention);
			fireEvent(Event.get(Event.Type.Add, mention.getEntity(), mention));
		}
		initialised = true;
	}

	public Deque<Op> getHistory() {
		return history;
	}

}
