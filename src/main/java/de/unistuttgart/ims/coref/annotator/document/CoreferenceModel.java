package de.unistuttgart.ims.coref.annotator.document;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
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
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.Sets;

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
import de.unistuttgart.ims.coref.annotator.document.Event.Type;
import de.unistuttgart.ims.coref.annotator.document.Op.AddEntityToEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.Op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.AttachPart;
import de.unistuttgart.ims.coref.annotator.document.Op.GroupEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.MergeEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.MoveMentionPartToMention;
import de.unistuttgart.ims.coref.annotator.document.Op.MoveMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveDuplicateMentionsInEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveEntitiesFromEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveMention;
import de.unistuttgart.ims.coref.annotator.document.Op.RemovePart;
import de.unistuttgart.ims.coref.annotator.document.Op.RenameEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.ToggleEntityFlag;
import de.unistuttgart.ims.coref.annotator.document.Op.ToggleMentionFlag;
import de.unistuttgart.ims.coref.annotator.document.Op.UpdateEntityColor;
import de.unistuttgart.ims.coref.annotator.document.Op.UpdateEntityKey;
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

	MutableSetMultimap<Entity, Mention> entityMentionMap = Multimaps.mutable.set.empty();

	MutableSetMultimap<Entity, EntityGroup> entityEntityGroupMap = Multimaps.mutable.set.empty();

	Map<Character, Entity> keyMap = Maps.mutable.empty();

	/**
	 * The document
	 */
	JCas jcas;

	Preferences preferences;

	boolean initialised = false;

	Deque<Op> history = new LinkedList<Op>();

	DocumentModel documentModel;

	public CoreferenceModel(DocumentModel documentModel, Preferences preferences) {
		this.jcas = documentModel.getJcas();
		this.preferences = preferences;
		this.documentModel = documentModel;
	}

	/**
	 * Create a new entity e and a new mention m, and add m to e. Does not fire any
	 * events.
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

		return m;
	}

	private Mention add(Span selection) {
		return add(selection.begin, selection.end);
	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		return crModelListeners.add(e);
	}

	/**
	 * does not fire events
	 * 
	 * @param e
	 * @param begin
	 * @param end
	 * @return
	 */
	private Mention addTo(Entity e, int begin, int end) {
		Mention m = createMention(begin, end);
		m.setEntity(e);
		entityMentionMap.put(e, m);

		return m;
	}

	/**
	 * does not fire events
	 * 
	 * @param e
	 * @param span
	 * @return
	 */
	private Mention addTo(Entity e, Span span) {
		return addTo(e, span.begin, span.end);
	}

	/**
	 * does not fire events
	 * 
	 * @param m
	 * @param begin
	 * @param end
	 * @return
	 */
	private DetachedMentionPart addTo(Mention m, int begin, int end) {
		// document model
		DetachedMentionPart d = createDetachedMentionPart(begin, end);
		d.setMention(m);
		m.setDiscontinuous(d);

		return d;
	}

	private DetachedMentionPart addTo(Mention m, Span sp) {
		return addTo(m, sp.begin, sp.end);
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

	public synchronized void edit(Op operation) {
		Annotator.logger.entry(operation);
		if (operation instanceof RenameEntity) {
			RenameEntity op = (RenameEntity) operation;
			op.getEntity().setLabel(op.getNewLabel());
			history.push(op);
		} else if (operation instanceof Op.RemoveDuplicateMentionsInEntities) {
			Op.RemoveDuplicateMentionsInEntities op = (RemoveDuplicateMentionsInEntities) operation;
			edit(op);
		} else if (operation instanceof Op.UpdateEntityKey) {
			Op.UpdateEntityKey op = (UpdateEntityKey) operation;
			if (keyMap.containsKey(op.getNewKey())) {
				Entity prev = keyMap.get(op.getNewKey());
				op.setPreviousOwner(prev);
				prev.setKey(null);
			}
			op.getObjects().getFirst().setKey(op.getNewKey().toString());
			keyMap.put(op.getNewKey(), op.getEntity());
			if (op.getPreviousOwner() != null)
				fireEvent(Event.get(Event.Type.Update, op.getObjects().getFirst(), op.getPreviousOwner()));
			else
				fireEvent(Event.get(Event.Type.Update, op.getObjects().getFirst()));
			history.push(op);
		} else if (operation instanceof Op.UpdateEntityColor) {
			Op.UpdateEntityColor op = (UpdateEntityColor) operation;
			op.getObjects().getFirst().setColor(op.getNewColor());
			fireEvent(Event.get(Event.Type.Update, op.getObjects()));
			fireEvent(Event.get(Event.Type.Update, op.getObjects().flatCollect(e -> entityMentionMap.get(e))));
			history.push(operation);
		} else if (operation instanceof Op.ToggleEntityFlag) {
			edit((Op.ToggleEntityFlag) operation);
			history.push(operation);
		} else if (operation instanceof Op.ToggleMentionFlag) {
			edit((Op.ToggleMentionFlag) operation);
			history.push(operation);
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
			MutableList<Mention> ms = Lists.mutable.empty();
			for (Span span : op.getSpans()) {
				if (op.getEntity() == null) {
					Mention fst = add(span);
					ms.add(fst);
					op.setEntity(fst.getEntity());
				} else
					ms.add(addTo(op.getEntity(), span));
			}
			fireEvent(Event.get(Event.Type.Add, null, op.getEntity()));
			fireEvent(Event.get(Event.Type.Add, op.getEntity(), ms.toImmutable()));
			history.push(op);
		} else if (operation instanceof Op.AddMentionsToEntity) {
			Op.AddMentionsToEntity op = (Op.AddMentionsToEntity) operation;
			op.setMentions(op.getSpans().collect(sp -> {
				return addTo(op.getEntity(), sp);
			}));
			fireEvent(Event.get(Event.Type.Add, op.getEntity(), op.getMentions()));
			history.push(op);
		} else if (operation instanceof Op.AttachPart) {
			Op.AttachPart op = (AttachPart) operation;
			op.setPart(addTo(op.getMention(), op.getSpan()));
			fireEvent(Event.get(Event.Type.Add, op.getMention(), op.getPart()));
			history.push(op);
		} else if (operation instanceof Op.MoveMentionsToEntity) {
			Op.MoveMentionsToEntity op = (Op.MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getTarget(), m));
			fireEvent(Event.get(Event.Type.Update, op.getObjects()));
			fireEvent(op.toEvent());
			history.push(op);
		} else if (operation instanceof Op.MoveMentionPartToMention) {
			Op.MoveMentionPartToMention op = (MoveMentionPartToMention) operation;
			op.getObjects().forEach(d -> {
				d.setMention(op.getTarget());
				op.getTarget().setDiscontinuous(d);
				op.getSource().setDiscontinuous(null);
			});
			fireEvent(op.toEvent());
			fireEvent(Event.get(Event.Type.Move, op.getSource(), op.getTarget(), op.getObjects()));
			history.push(op);
		} else if (operation instanceof Op.RemoveMention) {
			edit((RemoveMention) operation);
		} else if (operation instanceof Op.RemoveEntities) {
			Op.RemoveEntities op = (RemoveEntities) operation;
			op.getEntities().forEach(e -> {
				if (entityEntityGroupMap.containsKey(e))
					op.entityEntityGroupMap.putAll(e, entityEntityGroupMap.get(e));
				remove(e);
			});

			history.push(op);
		} else if (operation instanceof Op.RemoveEntitiesFromEntityGroup) {
			Op.RemoveEntitiesFromEntityGroup op = (RemoveEntitiesFromEntityGroup) operation;
			op.getEntities().forEach(e -> removeFrom(op.getEntityGroup(), e));
			history.push(op);
		} else if (operation instanceof Op.RemovePart) {
			Op.RemovePart op = (Op.RemovePart) operation;
			remove(op.getPart());
			fireEvent(Event.get(Type.Remove, op.getMention(), op.getPart()));
			history.push(op);
		} else if (operation instanceof Op.GroupEntities) {
			Op.GroupEntities op = (GroupEntities) operation;
			Annotator.logger.trace("Forming entity group with {}.", op.getEntities());
			EntityGroup eg = createEntityGroup(
					op.getEntities().subList(0, 2).select(e -> e.getLabel() != null).collect(e -> e.getLabel())
							.makeString(" " + Annotator.getString(Constants.Strings.ENTITY_GROUP_AND) + " "),
					op.getEntities().size());
			for (int i = 0; i < op.getEntities().size(); i++) {
				eg.setMembers(i, op.getEntities().get(i));
				entityEntityGroupMap.put(op.getEntities().get(i), eg);
			}
			fireEvent(Event.get(Event.Type.Add, null, eg));
			op.setEntityGroup(eg);
			history.push(op);
		} else if (operation instanceof Op.MergeEntities) {
			Op.MergeEntities op = (MergeEntities) operation;
			edit(op);
			history.push(op);
		} else {
			throw new UnsupportedOperationException();
		}
		documentModel.fireDocumentChangedEvent();
	}

	private void edit(Op.MergeEntities op) {
		MutableSetMultimap<Entity, Mention> currentState = Multimaps.mutable.set.empty();
		op.getEntities().forEach(e -> currentState.putAll(e, entityMentionMap.get(e)));
		op.setPreviousState(currentState.toImmutable());
		op.setEntity(merge(op.getEntities()));
	}

	private void edit(Op.RemoveDuplicateMentionsInEntities op) {
		MutableSet<Mention> allRemoved = Sets.mutable.empty();

		op.getEntities().forEach(e -> {
			MutableListMultimap<Span, Mention> map = Multimaps.mutable.list.empty();
			MutableList<Mention> toRemove = Lists.mutable.empty();
			for (Mention m : entityMentionMap.get(e)) {
				Span s = new Span(m);
				if (map.containsKey(s)) {
					for (Mention m2 : map.get(s)) {
						if (m2.getDiscontinuous() == null && m.getDiscontinuous() == null) {
							toRemove.add(m);
						} else if (m2.getDiscontinuous() != null && m.getDiscontinuous() != null) {
							Span s1 = new Span(m.getDiscontinuous());
							Span s2 = new Span(m2.getDiscontinuous());
							if (s1.equals(s2)) {
								toRemove.add(m);
							} else {
								map.put(s, m);
							}
						} else {
							map.put(s, m);
						}
					}
				} else {
					map.put(s, m);
				}
			}

			toRemove.forEach(m -> {
				remove(m, false);
				if (m.getDiscontinuous() != null) {
					DetachedMentionPart dmp = m.getDiscontinuous();
					remove(dmp);
					fireEvent(Event.get(Type.Remove, m, dmp));
				}
			});
			fireEvent(Event.get(Event.Type.Remove, e, toRemove.toImmutable()));
			allRemoved.addAll(toRemove);
		});
		op.setRemovedMentions(allRemoved.toImmutable());
		history.push(op);
	}

	private void edit(Op.RemoveMention op) {
		op.getMentions().forEach(m -> {
			remove(m, false);
			if (m.getDiscontinuous() != null) {
				DetachedMentionPart dmp = m.getDiscontinuous();
				remove(dmp);
				fireEvent(Event.get(Type.Remove, m, dmp));
			}
		});
		fireEvent(Event.get(Event.Type.Remove, op.getEntity(), op.getMentions()));
		history.push(op);
	}

	private void edit(Op.ToggleEntityFlag operation) {
		operation.getObjects().forEach(e -> {
			if (Util.contains(e.getFlags(), operation.getFlag())) {
				e.setFlags(Util.removeFrom(jcas, e.getFlags(), operation.getFlag()));
			} else
				e.setFlags(Util.addTo(jcas, e.getFlags(), operation.getFlag()));
		});
		fireEvent(Event.get(Event.Type.Update, operation.getObjects()));
	}

	private void edit(Op.ToggleMentionFlag operation) {
		operation.getObjects().forEach(m -> {
			if (Util.contains(m.getFlags(), operation.getFlag())) {
				m.setFlags(Util.removeFrom(jcas, m.getFlags(), operation.getFlag()));
			} else
				m.setFlags(Util.addTo(jcas, m.getFlags(), operation.getFlag()));
		});
		fireEvent(Event.get(Event.Type.Update, operation.getObjects()));
	}

	protected void fireEvent(FeatureStructureEvent event) {
		crModelListeners.forEach(l -> l.entityEvent(event));
	}

	public ImmutableSet<Mention> get(Entity entity) {
		return entityMentionMap.get(entity).toImmutable();
	}

	public Deque<Op> getHistory() {
		return history;
	}

	public JCas getJCas() {
		return jcas;
	}

	public Map<Character, Entity> getKeyMap() {
		return keyMap;
	}

	public String getLabel(Entity entity) {
		if (entity.getLabel() != null)
			return entity.getLabel();

		return get(entity).collect(m -> m.getCoveredText()).maxBy(s -> s.length());
	}

	public ImmutableSet<Mention> getMentions(Entity entity) {
		return entityMentionMap.get(entity).toImmutable();
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

	public void initialPainting() {
		if (initialised)
			return;
		for (Entity entity : JCasUtil.select(jcas, Entity.class)) {
			fireEvent(Event.get(Event.Type.Add, null, entity));
			if (entity.getKey() != null)
				keyMap.put(new Character(entity.getKey().charAt(0)), entity);
		}
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			entityMentionMap.put(mention.getEntity(), mention);
			mention.getEntity().addToIndexes();
			registerAnnotation(mention);
			fireEvent(Event.get(Event.Type.Add, mention.getEntity(), mention));
		}
		initialised = true;
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
					fireEvent(Event.get(Event.Type.Move, n, tgt, entityMentionMap.get(n).toList().toImmutable()));
					fireEvent(Event.get(Event.Type.Remove, n));
					entityMentionMap.get(n).toSet().forEach(m -> moveTo(tgt, m));

					entityMentionMap.removeAll(n);
					n.removeFromIndexes();
				}
			}
		return biggest;
	}

	/**
	 * does not fire events
	 * 
	 * @param newEntity
	 * @param mentions
	 */
	private void moveTo(Entity newEntity, Mention... mentions) {
		Entity oldEntity = null;
		for (Mention m : mentions) {
			oldEntity = m.getEntity();
			m.setEntity(newEntity);
			entityMentionMap.remove(oldEntity, m);
			entityMentionMap.put(newEntity, m);
		}
	}

	public void registerAnnotation(Annotation a) {
		characterPosition2AnnotationMap.add(a);
	}

	/**
	 * does not fire evetns
	 * 
	 * @param dmp
	 */
	private void remove(DetachedMentionPart dmp) {
		dmp.removeFromIndexes();
		characterPosition2AnnotationMap.remove(dmp);
	}

	private void remove(Entity entity) {
		fireEvent(Event.get(Event.Type.Remove, entity, entityMentionMap.get(entity).toList().toImmutable()));
		for (Mention m : entityMentionMap.get(entity)) {
			characterPosition2AnnotationMap.remove(m);
			m.removeFromIndexes();
		}
		for (EntityGroup group : entityEntityGroupMap.get(entity)) {
			group.setMembers(Util.removeFrom(jcas, group.getMembers(), entity));
		}

		entityEntityGroupMap.removeAll(entity);

		fireEvent(Event.get(Event.Type.Remove, null, entity));
		entityMentionMap.removeAll(entity);
		entity.removeFromIndexes();

	};

	private void remove(Mention m, boolean autoRemove) {
		Entity entity = m.getEntity();
		characterPosition2AnnotationMap.remove(m);
		entityMentionMap.remove(entity, m);
		m.removeFromIndexes();
		if (autoRemove && entityMentionMap.get(entity).isEmpty()
				&& preferences.getBoolean(Constants.CFG_DELETE_EMPTY_ENTITIES, Defaults.CFG_DELETE_EMPTY_ENTITIES)) {
			remove(entity);
		}

	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	}

	/**
	 * TODO: this could have a unit test
	 * 
	 * @param eg
	 * @param entity
	 */
	private void removeFrom(EntityGroup eg, Entity entity) {
		FSArray oldArray = eg.getMembers();
		FSArray arr = new FSArray(jcas, eg.getMembers().size() - 1);

		for (int i = 0, j = 0; i < oldArray.size() - 1 && j < arr.size() - 1; i++, j++) {

			if (eg.getMembers(i) == entity) {
				i++;
			}
			arr.set(j, eg.getMembers(i));

		}
		eg.setMembers(arr);
		fireEvent(Event.get(Event.Type.Remove, eg, entity));
	}

	public void undo() {
		if (!history.isEmpty()) {
			undo(history.pop());
			documentModel.fireDocumentChangedEvent();
		}
	}

	protected void undo(Op operation) {
		Annotator.logger.entry(operation);
		if (operation instanceof Op.RenameEntity) {
			Op.RenameEntity op = (Op.RenameEntity) operation;
			op.getEntity().setLabel(op.getOldLabel());
		} else if (operation instanceof Op.UpdateEntityKey) {
			Op.UpdateEntityKey op = (UpdateEntityKey) operation;
			if (op.getPreviousOwner() != null) {
				op.getPreviousOwner().setKey(op.getNewKey().toString());
				keyMap.put(op.getNewKey(), op.getPreviousOwner());
			} else {
				keyMap.remove(op.getNewKey());
			}
			if (op.getOldKey() != null) {
				op.getEntity().setKey(op.getOldKey().toString());
				keyMap.put(op.getOldKey(), op.getEntity());
			} else {
				op.getEntity().setKey(null);
			}
			if (op.getPreviousOwner() != null)
				fireEvent(Event.get(Event.Type.Update, op.getObjects().getFirst(), op.getPreviousOwner()));
			else
				fireEvent(Event.get(Event.Type.Update, op.getObjects().getFirst()));
		} else if (operation instanceof Op.ToggleEntityFlag) {
			edit((ToggleEntityFlag) operation);
		} else if (operation instanceof Op.ToggleMentionFlag) {
			edit((ToggleMentionFlag) operation);
		} else if (operation instanceof Op.UpdateEntityColor) {
			Op.UpdateEntityColor op = (UpdateEntityColor) operation;
			op.getObjects().getFirst().setColor(op.getOldColor());
			fireEvent(Event.get(Event.Type.Update, op.getObjects()));
			fireEvent(Event.get(Event.Type.Update, op.getObjects().flatCollect(e -> entityMentionMap.get(e))));
		} else if (operation instanceof Op.AddEntityToEntityGroup) {
			Op.AddEntityToEntityGroup op = (AddEntityToEntityGroup) operation;
			op.getEntities().forEach(e -> removeFrom(op.getEntityGroup(), e));
		} else if (operation instanceof Op.AddMentionsToNewEntity) {
			Op.AddMentionsToNewEntity op = (Op.AddMentionsToNewEntity) operation;
			remove(op.getEntity());
		} else if (operation instanceof Op.AddMentionsToEntity) {
			Op.AddMentionsToEntity op = (AddMentionsToEntity) operation;
			op.getMentions().forEach(m -> remove(m, false));
			fireEvent(Event.get(Event.Type.Remove, op.getEntity(), op.getMentions()));
		} else if (operation instanceof Op.AttachPart) {
			Op.AttachPart op = (AttachPart) operation;
			remove(op.getPart());
			fireEvent(Event.get(Event.Type.Remove, op.getMention(), op.getPart()));
		} else if (operation instanceof Op.MoveMentionPartToMention) {
			Op.MoveMentionPartToMention op = (MoveMentionPartToMention) operation;
			op.getObjects().forEach(d -> {
				op.getSource().setDiscontinuous(d);
				d.setMention(op.getSource());
				op.getTarget().setDiscontinuous(null);
			});
			fireEvent(op.toReversedEvent());
		} else if (operation instanceof Op.MoveMentionsToEntity) {
			Op.MoveMentionsToEntity op = (MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getSource(), m));
			fireEvent(Event.get(Event.Type.Update, op.getObjects()));
			fireEvent(op.toReversedEvent());
		} else if (operation instanceof Op.RemoveDuplicateMentionsInEntities) {
			Op.RemoveDuplicateMentionsInEntities op = (RemoveDuplicateMentionsInEntities) operation;

			op.getRemovedMentions().forEach(m -> {
				m.addToIndexes();
				entityMentionMap.put(m.getEntity(), m);
				registerAnnotation(m);
				fireEvent(Event.get(Type.Add, m.getEntity(), m));
			});
		} else if (operation instanceof Op.RemovePart) {
			Op.RemovePart op = (RemovePart) operation;
			op.getPart().setMention(op.getMention());
			op.getMention().setDiscontinuous(op.getPart());
			fireEvent(Event.get(Type.Add, op.getMention(), op.getPart()));
		} else if (operation instanceof Op.RemoveMention) {
			undo((RemoveMention) operation);
		} else if (operation instanceof Op.RemoveEntities) {
			Op.RemoveEntities op = (RemoveEntities) operation;
			op.getEntities().forEach(e -> {
				e.addToIndexes();
				if (op.entityEntityGroupMap.containsKey(e)) {
					for (EntityGroup group : op.entityEntityGroupMap.get(e))
						group.setMembers(Util.addTo(jcas, group.getMembers(), e));
				}
			});
			fireEvent(Event.get(Event.Type.Add, null, op.getEntities()));
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
			op.getEntityGroup().setMembers(newArr);
			newArr.addToIndexes();
			oldArr.removeFromIndexes();
		} else if (operation instanceof Op.MergeEntities) {
			Op.MergeEntities op = (MergeEntities) operation;
			for (Entity oldEntity : op.getEntities()) {
				if (op.getEntity() != oldEntity) {
					oldEntity.addToIndexes();
					fireEvent(Event.get(Event.Type.Add, null, oldEntity));
					for (Mention m : op.getPreviousState().get(oldEntity)) {
						moveTo(oldEntity, m);
					}
					fireEvent(Event.get(Type.Move, null, oldEntity,
							op.getPreviousState().get(oldEntity).toList().toImmutable()));
				}
			}
		} else if (operation instanceof Op.GroupEntities) {
			Op.GroupEntities op = (GroupEntities) operation;
			remove(op.getEntityGroup());
			op.getEntities().forEach(e -> entityEntityGroupMap.remove(e, op.getEntityGroup()));
			fireEvent(Event.get(Event.Type.Remove, null, op.getEntityGroup()));
		}
	}

	private void undo(Op.RemoveMention op) {
		// re-create all mentions and set them to the op
		op.getMentions().forEach(m -> {
			m.addToIndexes();
			m.setEntity(op.getEntity());
			entityMentionMap.put(op.getEntity(), m);
			characterPosition2AnnotationMap.add(m);
			if (m.getDiscontinuous() != null) {
				m.getDiscontinuous().addToIndexes();
				characterPosition2AnnotationMap.add(m.getDiscontinuous());
			}
		});
		// fire event to draw them
		fireEvent(Event.get(Event.Type.Add, op.getEntity(), op.getMentions()));
		// re-create attached parts (if any)
		op.getMentions().select(m -> m.getDiscontinuous() != null)
				.forEach(m -> fireEvent(Event.get(Event.Type.Add, m, m.getDiscontinuous())));

	}

}
