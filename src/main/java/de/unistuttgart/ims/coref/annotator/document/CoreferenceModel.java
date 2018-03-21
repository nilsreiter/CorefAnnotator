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
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener.Event;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.RangedHashSetValuedHashMap;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.Op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.AttachPart;
import de.unistuttgart.ims.coref.annotator.document.Op.GroupEntities;
import de.unistuttgart.ims.coref.annotator.document.Op.MoveMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.Op.RemoveEntities;
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
		fireEntityAddedEvent(e);
		m.setEntity(e);
		entityMentionMap.put(e, m);
		fireMentionAddedEvent(m);

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

		fireEntityEvent(Event.Update, e);
		fireMentionAddedEvent(m);

		return m;
	}

	public void addTo(EntityGroup eg, Entity e) {
		// UIMA stuff
		FSArray oldArr = eg.getMembers();

		for (int i = 0; i < oldArr.size(); i++) {
			if (oldArr.get(i) == e) {
				return;
			}
		}

		FSArray arr = new FSArray(jcas, eg.getMembers().size() + 1);
		int i = 0;
		for (; i < eg.getMembers().size(); i++) {
			arr.set(i, eg.getMembers(i));
		}
		arr.set(i, e);
		eg.setMembers(arr);
		oldArr.removeFromIndexes();

		fireEntityGroupEvent(Event.Update, eg);

	}

	private DetachedMentionPart addTo(Mention m, int begin, int end) {
		// document model
		DetachedMentionPart d = createDetachedMentionPart(begin, end);
		d.setMention(m);
		m.setDiscontinuous(d);

		fireAnnotationChangedEvent(m);
		fireMentionAddedEvent(d);
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
			op.getMentions().forEach(m -> moveTo(m, op.getTarget()));
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
			fireEntityGroupEvent(Event.Add, eg);
			op.setEntityGroup(eg);
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
			op.getMentions().forEach(m -> moveTo(m, op.getSource()));
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
				fireEntityEvent(Event.Add, e);
			});
		} else if (operation instanceof Op.GroupEntities) {
			remove(((Op.GroupEntities) operation).getEntityGroup());
		}
	}

	protected void fireAnnotationChangedEvent(Annotation m) {
		crModelListeners.forEach(l -> l.annotationEvent(Event.Update, m));
	}

	protected void fireAnnotationMovedEvent(Annotation m, Object from, Object to) {
		crModelListeners.forEach(l -> l.annotationMovedEvent(m, from, to));
	}

	protected void fireAnnotationRemovedEvent(Annotation m) {
		crModelListeners.forEach(l -> l.annotationEvent(Event.Remove, m));
	}

	protected void fireEntityAddedEvent(Entity e) {
		crModelListeners.forEach(l -> l.entityEvent(Event.Add, e));
	}

	protected void fireEntityEvent(Event evt, Entity e) {
		crModelListeners.forEach(l -> l.entityEvent(evt, e));
	}

	protected void fireEntityGroupEvent(Event update, EntityGroup eg) {
		crModelListeners.forEach(l -> l.entityGroupEvent(update, eg));
	}

	protected void fireEntityRemovedEvent(Entity e) {
		crModelListeners.forEach(l -> l.entityEvent(Event.Remove, e));
	}

	protected void fireMentionAddedEvent(Annotation annotation) {
		crModelListeners.forEach(l -> l.annotationEvent(Event.Add, annotation));
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

	public void merge(Entity... nodes) {
		Entity biggest = nodes[0];
		int size = 0;
		for (Entity n : nodes) {
			if (entityMentionMap.get(n).size() > size) {
				size = entityMentionMap.get(n).size();
				biggest = n;
			}
		}

		for (Entity n : nodes) {
			if (n != biggest) {
				for (Mention mention : entityMentionMap.get(n)) {
					moveTo(mention, biggest);
				}
				remove(n);
			}
		}
		return;
	}

	public void moveTo(DetachedMentionPart dmp, Mention m) {
		Mention old = dmp.getMention();
		dmp.setMention(m);

		fireAnnotationChangedEvent(old);
		fireAnnotationChangedEvent(dmp);
		fireAnnotationChangedEvent(m);
	}

	private void moveTo(Mention m, Entity newEntity) {
		Entity oldEntity = m.getEntity();
		m.setEntity(newEntity);
		fireAnnotationMovedEvent(m, oldEntity, newEntity);
		fireAnnotationChangedEvent(m);
	}

	public void registerAnnotation(Annotation a) {
		characterPosition2AnnotationMap.add(a);
	}

	private void remove(DetachedMentionPart dmp) {
		fireAnnotationRemovedEvent(dmp);
		dmp.getMention().setDiscontinuous(null);
		dmp.removeFromIndexes();
		characterPosition2AnnotationMap.remove(dmp);
	}

	private void remove(Entity entity) {
		for (Mention m : entityMentionMap.get(entity)) {
			characterPosition2AnnotationMap.remove(m);
			fireAnnotationRemovedEvent(m);
			m.removeFromIndexes();
		}
		fireEntityEvent(Event.Remove, entity);
		entityMentionMap.removeAll(entity);
		entity.removeFromIndexes();

	}

	private void remove(EntityGroup entity) {
		fireEntityGroupEvent(Event.Remove, entity);
		entityMentionMap.removeAll(entity);
		entity.removeFromIndexes();
	}

	private void remove(Mention m, boolean autoRemove) {
		Entity entity = m.getEntity();
		characterPosition2AnnotationMap.remove(m);
		entityMentionMap.remove(entity, m);
		fireAnnotationRemovedEvent(m);
		m.removeFromIndexes();
		if (autoRemove && entityMentionMap.get(entity).isEmpty()
				&& preferences.getBoolean(Constants.CFG_DELETE_EMPTY_ENTITIES, Defaults.CFG_DELETE_EMPTY_ENTITIES)) {
			remove(entity);
		}

	}

	public boolean removeCoreferenceModelListener(Object o) {
		return crModelListeners.remove(o);
	};

	public void removeFrom(EntityGroup eg, Entity entity) {
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
		fireEntityGroupEvent(Event.Update, eg);
	}

	public void setHidden(Entity entity, boolean hidden) {
		entity.setHidden(hidden);
		fireEntityEvent(Event.Update, entity);
		for (Annotation a : entityMentionMap.get(entity)) {
			fireAnnotationChangedEvent(a);
		}
	}

	public void toggleFlagEntity(Entity m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		fireEntityEvent(Event.Update, m);
	}

	public void toggleFlagMention(Mention m, String flag) {
		if (Util.contains(m.getFlags(), flag)) {
			m.setFlags(Util.removeFrom(jcas, m.getFlags(), flag));
		} else
			m.setFlags(Util.addTo(jcas, m.getFlags(), flag));
		fireAnnotationChangedEvent(m);
	}

	public void toggleHidden(Entity entity) {
		setHidden(entity, !entity.getHidden());
	}

	public void updateColor(Entity entity, Color newColor) {
		entity.setColor(newColor.getRGB());
		fireEntityEvent(Event.Update, entity);
		for (Annotation a : entityMentionMap.get(entity)) {
			fireAnnotationChangedEvent(a);
		}
	}

	public void initialPainting() {
		if (initialised)
			return;
		for (Entity entity : JCasUtil.select(jcas, Entity.class)) {
			if (entity instanceof EntityGroup)
				fireEntityGroupEvent(Event.Add, (EntityGroup) entity);
			else
				fireEntityEvent(Event.Add, entity);
		}
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			entityMentionMap.put(mention.getEntity(), mention);
			mention.getEntity().addToIndexes();
			registerAnnotation(mention);
			fireMentionAddedEvent(mention);
		}
		initialised = true;
	}

	public Deque<Op> getHistory() {
		return history;
	}

}
