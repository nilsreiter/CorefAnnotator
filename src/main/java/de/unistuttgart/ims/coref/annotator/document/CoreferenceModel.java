package de.unistuttgart.ims.coref.annotator.document;

import java.util.Comparator;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.RangedHashSetValuedHashMap;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.Spans;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Comment;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;
import de.unistuttgart.ims.coref.annotator.document.op.AddEntityToEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddSpanToMention;
import de.unistuttgart.ims.coref.annotator.document.op.CoreferenceModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.DuplicateMentions;
import de.unistuttgart.ims.coref.annotator.document.op.GroupEntities;
import de.unistuttgart.ims.coref.annotator.document.op.MergeEntities;
import de.unistuttgart.ims.coref.annotator.document.op.MergeMentions;
import de.unistuttgart.ims.coref.annotator.document.op.MoveMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveDuplicateMentionsInEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveEntitiesFromEntityGroup;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMention;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMentionSurface;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveSingletons;
import de.unistuttgart.ims.coref.annotator.document.op.RenameAllEntities;
import de.unistuttgart.ims.coref.annotator.document.op.ToggleGenericFlag;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityColor;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityKey;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityName;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationUtil;
import de.unistuttgart.ims.coref.annotator.uima.MentionComparator;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

/**
 * Class represents the document and the tree view on the document. All
 * annotation happens through this class.
 * 
 *
 */
public class CoreferenceModel extends SubModel implements Model, PreferenceChangeListener {

	public static enum EntitySorter {
		LABEL, CHILDREN, COLOR, ADDRESS
	}

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

	MutableSortedSetMultimap<Entity, Mention> entityMentionMap = Multimaps.mutable.sortedSet
			.of(new MentionComparator());

	MutableSetMultimap<Entity, Entity> entityEntityGroupMap = Multimaps.mutable.set.empty();

	Map<Character, Entity> keyMap = Maps.mutable.empty();

	public CoreferenceModel(DocumentModel documentModel) {
		super(documentModel);
		documentModel.getPreferences().addPreferenceChangeListener(this);
	}

	public boolean addCoreferenceModelListener(CoreferenceModelListener e) {
		e.entityEvent(Event.get(this, Event.Type.Init));
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

	protected Entity createEntity(String l) {
		Entity e = new Entity(documentModel.getJcas());
		e.setColor(colorMap.getNextColor().getRGB());
		e.setLabel(l);
		e.setFlags(new EmptyFSList<Flag>(documentModel.getJcas()));
		e.setMembers(new FSArray<Entity>(documentModel.getJcas(), 0));
		e.addToIndexes();
		return e;
	}

	protected String createEntityGroupLabel(ImmutableList<Entity> entityList) {
		String s = entityList.subList(0, Math.min(entityList.size(), 2)).select(e -> e.getLabel() != null)
				.collect(
						e -> StringUtils.abbreviate(e.getLabel(), "…", (Constants.UI_MAX_STRING_WIDTH_IN_TREE / 2) - 4))
				.makeString(" " + Annotator.getString(Strings.ENTITY_GROUP_AND) + " ");
		if (entityList.size() > 2)
			s += " + " + String.valueOf(entityList.size() - 2);
		return s;
	}

	/**
	 * Creates a new mention annotation in the document and adds it to the indexes
	 * 
	 * @param b the begin character position
	 * @param e the end character position
	 * @return the created mention
	 */
	protected Mention createMention(int b, int e) {
		Mention m = UimaUtil.createMention(documentModel.getJcas(), b, e);
		if (getPreferences().getBoolean(Constants.CFG_TRIM_WHITESPACE, Defaults.CFG_TRIM_WHITESPACE))
			AnnotationUtil.trim(m.getSurface(0));
		if (getPreferences().getBoolean(Constants.CFG_FULL_TOKENS, Defaults.CFG_FULL_TOKENS))
			UimaUtil.extend(m.getSurface(0));
		for (MentionSurface ms : m.getSurface())
			registerAnnotation(ms);
		return m;
	}

	protected void edit(MergeEntities op) {
		MutableSetMultimap<Entity, Mention> currentState = Multimaps.mutable.set.empty();
		op.getEntities().forEach(e -> currentState.putAll(e, entityMentionMap.get(e)));
		op.setPreviousState(currentState.toImmutable());
		op.setEntity(merge(op.getEntities()));
		registerEdit(op);
	}

	protected void edit(MergeMentions op) {
		Mention firstMention = op.getMentions().getFirstOptional().get();
		int begin = UimaUtil.getBegin(op.getMentions().getFirstOptional().get());
		int end = UimaUtil.getEnd(op.getMentions().getLastOptional().get());
		Mention newMention = addTo(firstMention.getEntity(), begin, end);

		op.getMentions().forEach(m -> {
			remove(m, false);
		});
		fireEvent(Event.get(this, Event.Type.Remove, firstMention.getEntity(), op.getMentions()));
		fireEvent(Event.get(this, Type.Add, newMention.getEntity(), newMention));
		op.setNewMention(newMention);
		registerEdit(op);
	}

	protected synchronized void edit(CoreferenceModelOperation operation) {
		Annotator.logger.traceEntry();
		if (operation instanceof UpdateEntityName) {
			UpdateEntityName op = (UpdateEntityName) operation;
			op.getEntity().setLabel(op.getNewLabel());
			fireEvent(Event.get(this, Event.Type.Update, op.getEntity()));
		} else if (operation instanceof RemoveDuplicateMentionsInEntities) {
			edit((RemoveDuplicateMentionsInEntities) operation);
		} else if (operation instanceof UpdateEntityKey) {
			UpdateEntityKey op = (UpdateEntityKey) operation;
			if (op.getNewKey() != null && keyMap.containsKey(op.getNewKey())) {
				Entity prev = keyMap.get(op.getNewKey());
				op.setPreviousOwner(prev);
				prev.setKey(null);
			}
			if (op.getNewKey() == null)
				op.getObjects().getFirst().setKey(null);
			else {
				op.getObjects().getFirst().setKey(op.getNewKey().toString());
				keyMap.put(op.getNewKey(), op.getEntity());
			}
			if (op.getPreviousOwner() != null)
				fireEvent(Event.get(this, Event.Type.Update, op.getObjects().getFirst(), op.getPreviousOwner()));
			else
				fireEvent(Event.get(this, Event.Type.Update, op.getObjects().getFirst()));
		} else if (operation instanceof UpdateEntityColor) {
			UpdateEntityColor op = (UpdateEntityColor) operation;
			op.getObjects().getFirst().setColor(op.getNewColor());
			fireEvent(Event.get(this, Event.Type.Update, op.getObjects()));
			fireEvent(Event.get(this, Event.Type.Update, op.getObjects().flatCollect(e -> entityMentionMap.get(e))));
		} else if (operation instanceof AddEntityToEntityGroup) {
			AddEntityToEntityGroup op = (AddEntityToEntityGroup) operation;
			MutableList<Entity> oldArr = UimaUtil.toList(op.getEntityGroup().getMembers());

			MutableList<Entity> newMembers = Lists.mutable.withAll(op.getEntities());
			newMembers.removeAll(oldArr);

			op.setEntities(newMembers.toImmutable());

			FSArray<Entity> arr = new FSArray<Entity>(documentModel.getJcas(),
					op.getEntityGroup().getMembers().size() + newMembers.size());
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
			updateEntityGroupLabel(op.getEntityGroup());
			fireEvent(Event.get(this, Event.Type.Add, op.getEntityGroup(), op.getEntities()));
		} else if (operation instanceof AddMentionsToNewEntity) {
			AddMentionsToNewEntity op = (AddMentionsToNewEntity) operation;
			MutableList<Mention> ms = Lists.mutable.empty();
			op.setEntity(createEntity(""));
			for (Span span : op.getSpans()) {
				Mention fst = addTo(op.getEntity(), span);
				ms.add(fst);
				if (op.getEntity().getLabel() == "") {
					op.getEntity().setLabel(UimaUtil.getCoveredText(fst));
				}
			}
			fireEvent(Event.get(this, Event.Type.Add, null, op.getEntity()));
			fireEvent(Event.get(this, Event.Type.Add, op.getEntity(), ms.toImmutable()));
		} else if (operation instanceof AddMentionsToEntity) {
			AddMentionsToEntity op = (AddMentionsToEntity) operation;
			op.setMentions(op.getSpans().collect(sp -> {
				return addTo(op.getEntity(), sp);
			}));
			fireEvent(Event.get(this, Event.Type.Add, op.getEntity(), op.getMentions()));
			int newNumberOfMentions = getSize(op.getEntity());
			// may trigger underlining in gray if singleton
			if (getSpecialHandlingForSingletons() && newNumberOfMentions - op.getMentions().size() == 1
					&& newNumberOfMentions > 1) {
				fireEvent(Event.get(this, Event.Type.Update, op.getEntity()));
				fireEvent(Event.get(this, Event.Type.Update, get(op.getEntity())));
			}
		} else if (operation instanceof MoveMentionsToEntity) {
			MoveMentionsToEntity op = (MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getTarget(), m));
			fireEvent(Event.get(this, Event.Type.Move, op.getSource(), op.getTarget(), op.getMentions()));
			// may trigger underlining in gray if singleton
			if (getSpecialHandlingForSingletons()) {
				int numberOfMentionsMoved = op.getMentions().size();
				// handle the source
				if (getSize(op.getSource()) == 1) {
					fireEvent(Event.get(this, Event.Type.Update, op.getSource()));
					fireEvent(Event.get(this, Event.Type.Update, get(op.getSource())));
				}
				// target
				if (getSize(op.getTarget()) - numberOfMentionsMoved == 1) {
					fireEvent(Event.get(this, Event.Type.Update, op.getTarget()));
					fireEvent(Event.get(this, Event.Type.Update, get(op.getTarget())));
				}
			}

		} else if (operation instanceof RemoveEntities) {
			boolean keepTreeSortedSetting = getPreferences().getBoolean(Constants.CFG_KEEP_TREE_SORTED,
					Defaults.CFG_KEEP_TREE_SORTED);
			getPreferences().putBoolean(Constants.CFG_KEEP_TREE_SORTED, false);
			RemoveEntities op = (RemoveEntities) operation;
			op.getFeatureStructures().forEach(e -> {
				if (entityEntityGroupMap.containsKey(e))
					op.entityEntityGroupMap.putAll(e, entityEntityGroupMap.get(e));
				remove(e);
			});
			getPreferences().putBoolean(Constants.CFG_KEEP_TREE_SORTED, keepTreeSortedSetting);

		} else if (operation instanceof RemoveEntitiesFromEntityGroup) {
			RemoveEntitiesFromEntityGroup op = (RemoveEntitiesFromEntityGroup) operation;
			op.getEntities().forEach(e -> {
				entityEntityGroupMap.remove(e, op.getEntityGroup());
				removeFrom(op.getEntityGroup(), e);
				updateEntityGroupLabel(op.getEntityGroup());
			});
		} else if (operation instanceof GroupEntities) {
			GroupEntities op = (GroupEntities) operation;
			Annotator.logger.trace("Forming entity group with {}.", op.getEntities());
			Entity eg = createEntity(createEntityGroupLabel(op.getEntities()));
			eg.setMembers(new FSArray<Entity>(documentModel.getJcas(), op.getEntities().size()));
			for (int i = 0; i < op.getEntities().size(); i++) {
				eg.setMembers(i, op.getEntities().get(i));
				entityEntityGroupMap.put(op.getEntities().get(i), eg);
			}
			fireEvent(Event.get(this, Event.Type.Add, null, eg));
			op.setEntityGroup(eg);
		} else if (operation instanceof RemoveMentionSurface) {
			edit((RemoveMentionSurface) operation);
		} else if (operation instanceof RemoveMention) {
			edit((RemoveMention) operation);
		} else if (operation instanceof RemoveSingletons) {
			edit((RemoveSingletons) operation);
		} else if (operation instanceof MergeEntities) {
			edit((MergeEntities) operation);
		} else if (operation instanceof MergeMentions) {
			edit((MergeMentions) operation);
		} else if (operation instanceof ToggleGenericFlag) {
			edit((ToggleGenericFlag) operation);
		} else if (operation instanceof RenameAllEntities) {
			edit((RenameAllEntities) operation);
		} else if (operation instanceof DuplicateMentions) {
			edit((DuplicateMentions) operation);
		} else if (operation instanceof AddSpanToMention) {
			edit((AddSpanToMention) operation);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	protected void edit(AddSpanToMention op) {
		MentionSurface ms = AnnotationFactory.createAnnotation(getJCas(), op.getSpan().begin, op.getSpan().end,
				MentionSurface.class);
		op.setMentionSurface(ms);
		ms.setMention(op.getTarget());

		UimaUtil.addMentionSurface(op.getTarget(), ms);
		characterPosition2AnnotationMap.add(ms);

		fireEvent(Event.get(this, Event.Type.Add, op.getTarget(), ms));
		registerEdit(op);
	}

	protected void edit(DuplicateMentions op) {
		op.setNewMentions(op.getSourceMentions().collect(oldMention -> {
			Mention newMention = addTo(oldMention.getEntity(), new Span(oldMention.getSurface(0)));
			for (int i = 1; i < oldMention.getSurface().size(); i++) {
				MentionSurface oMS = oldMention.getSurface(i);
				MentionSurface ms = AnnotationFactory.createAnnotation(getJCas(), oMS.getBegin(), oMS.getEnd(),
						MentionSurface.class);
				UimaUtil.addMentionSurface(newMention, ms);
				characterPosition2AnnotationMap.add(ms);
			}
			try {
				if (oldMention.getFlags() != null)
					newMention.setFlags(UimaUtil.clone(oldMention.getFlags()));
			} catch (CASException e) {
				Annotator.logger.catching(e);
			}
			return newMention;
		}));
		op.getNewMentions().forEach(m -> fireEvent(Event.get(this, Event.Type.Add, m.getEntity(), m)));
		registerEdit(op);
	}

	protected void edit(RemoveDuplicateMentionsInEntities op) {
		MutableSet<Mention> allRemoved = Sets.mutable.empty();

		op.getEntities().forEach(e -> {
			MutableListMultimap<Spans, Mention> spanMentionMap = Multimaps.mutable.list.empty();
			MutableList<Mention> toRemove = Lists.mutable.empty();
			for (Mention m : entityMentionMap.get(e)) {
				Spans s = new Spans(m);
				if (spanMentionMap.containsKey(s)) {
					toRemove.add(m);
				} else {
					spanMentionMap.put(s, m);
				}
			}

			toRemove.forEach(m -> {
				remove(m, false);
			});
			fireEvent(Event.get(this, Event.Type.Remove, e, toRemove.toImmutable()));
			allRemoved.addAll(toRemove);
		});
		op.setFeatureStructures(allRemoved.toList().toImmutable());
		registerEdit(op);
	}

	protected void edit(RemoveMention op) {
		op.getFeatureStructures().forEach(m -> {
			remove(m, false);
			// TODO: remove surfaces
		});
		fireEvent(Event.get(this, Event.Type.Remove, op.getEntity(), op.getFeatureStructures()));

		// may trigger underlining in gray if singleton
		if (getSpecialHandlingForSingletons()) {
			if (getSize(op.getEntity()) == 1) {
				fireEvent(Event.get(this, Event.Type.Update, op.getEntity()));
				fireEvent(Event.get(this, Event.Type.Update, get(op.getEntity())));
			}
		}

		// remove entity if its empty
		Entity e = op.getEntity();
		if (entityMentionMap.get(e).isEmpty() && getPreferences().getBoolean(Constants.CFG_DELETE_EMPTY_ENTITIES,
				Defaults.CFG_DELETE_EMPTY_ENTITIES)) {
			remove(e);
			op.setEntityAutoDeleted(true);
		}
		registerEdit(op);
	}

	protected void edit(RemoveMentionSurface op) {
		MutableList<Mention> mentions = Lists.mutable.empty();
		MutableList<Span> spans = Lists.mutable.empty();
		op.getMentionSurface().forEach(ms -> {
			Mention m = ms.getMention();
			UimaUtil.removeMentionSurface(m, ms);
			fireEvent(Event.get(this, Type.Remove, m, ms));
			mentions.add(m);
			spans.add(new Span(ms));
			ms.removeFromIndexes();
			characterPosition2AnnotationMap.remove(ms);
		});
		op.setMention(mentions.toImmutable());
		op.setSpans(spans.toImmutable());
		registerEdit(op);
	}

	protected void edit(RemoveSingletons operation) {
		MutableSet<Entity> entities = Sets.mutable.empty();
		MutableSet<Mention> mentions = Sets.mutable.empty();
		for (Entity entity : Lists.immutable.withAll(JCasUtil.select(documentModel.getJcas(), Entity.class))) {
			ImmutableSortedSet<Mention> ms = getMentions(entity);
			switch (ms.size()) {
			case 0:
				remove(entity);
				entities.add(entity);
				break;
			case 1:
				Mention m = ms.getOnly();
				remove(m.getEntity());
				mentions.add(m);
				break;
			default:
				break;
			}
		}
		operation.setFeatureStructures(entities.toList().toImmutable());
		operation.setMentions(mentions.toList().toImmutable());
		// fireEvent(null); // TODO
		registerEdit(operation);
	}

	protected void edit(RenameAllEntities operation) {
		for (Entity entity : entityMentionMap.keySet()) {

			Mention nameGiver;

			switch (operation.getStrategy()) {
			case LAST:
				nameGiver = entityMentionMap.get(entity).maxBy(m -> UimaUtil.getBegin(m));
				break;
			case LONGEST:
				nameGiver = entityMentionMap.get(entity).maxBy(m -> UimaUtil.getEnd(m) - UimaUtil.getBegin(m));
				break;
			case FIRST:
			default:
				nameGiver = entityMentionMap.get(entity).minBy(m -> UimaUtil.getBegin(m));
				break;

			}
			operation.registerOldName(entity, getLabel(entity));
			String newName = UimaUtil.getCoveredText(nameGiver);
			entity.setLabel(newName);

		}
		fireEvent(Event.get(this, Event.Type.Update, operation.getOldNames().keySet()));

	}

	protected void edit(ToggleGenericFlag operation) {
		MutableSet<FeatureStructure> featureStructures = Sets.mutable.empty();
		operation.getObjects().forEach(fs -> {

			Feature feature = fs.getType().getFeatureByBaseName("Flags");

			featureStructures.add(fs);

			@SuppressWarnings("unchecked")
			FSList<Flag> flags = (FSList<Flag>) fs.getFeatureValue(feature);

			if (UimaUtil.isX(fs, operation.getFlag())) {
				fs.setFeatureValue(feature, UimaUtil.removeFrom(flags, operation.getFlag()));
			} else {
				if (flags == null) {
					fs.setFeatureValue(feature, new NonEmptyFSList<Flag>(documentModel.getJcas(), operation.getFlag()));
				} else {
					fs.setFeatureValue(feature, flags.push(operation.getFlag()));
				}
			}
		});
		fireEvent(Event.get(this, Event.Type.Update, operation.getObjects()));
		fireEvent(Event.get(this, Event.Type.Update,
				featureStructures.selectInstancesOf(Entity.class).flatCollect(e -> entityMentionMap.get(e)).toList()));
		registerEdit(operation);

	}

	protected void fireEvent(FeatureStructureEvent event) {
		crModelListeners.forEach(l -> l.entityEvent(event));
	}

	public ImmutableSortedSet<Mention> get(Entity entity) {
		return entityMentionMap.get(entity).toImmutable();
	}

	@Override
	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public ImmutableList<Entity> getSingletons() {
		return Sets.mutable.withAll(JCasUtil.select(documentModel.getJcas(), Entity.class))
				.select(e -> getMentions(e).size() == 1).toList().toImmutable();
	}

	protected int getSize(Entity e) {
		return get(e).size();
	}

	public ImmutableList<Entity> getEntities(final EntitySorter entitySorter) {

		MutableSet<Entity> eset = Sets.mutable.withAll(JCasUtil.select(documentModel.getJcas(), Entity.class));
		return eset.toSortedList(new Comparator<Entity>() {

			@Override
			public int compare(Entity o1, Entity o2) {
				switch (entitySorter) {
				case CHILDREN:
					return Integer.compare(entityMentionMap.get(o2).size(), entityMentionMap.get(o1).size());
				case COLOR:
					return Integer.compare(o1.getColor(), o2.getColor());
				case ADDRESS:
				default:
					return Integer.compare(o1.getAddress(), o2.getAddress());
				}
			}

		}).toImmutable();
	}

	public JCas getJCas() {
		return documentModel.getJcas();
	}

	public Map<Character, Entity> getKeyMap() {
		return keyMap;
	}

	public String getLabel(Entity entity) {
		if (entity.getLabel() != null)
			return entity.getLabel();

		return get(entity).collect(m -> UimaUtil.getCoveredText(m)).maxBy(s -> s.length());
	}

	public ImmutableSortedSet<Mention> getMentions() {
		return SortedSets.immutable.withAll(new MentionComparator(), JCasUtil.select(getJCas(), Mention.class));
	}

	public ImmutableSortedSet<Mention> getMentions(Entity entity) {
		return entityMentionMap.get(entity).toImmutable();
	}

	public Mention getNextMention(int position) {
		for (int i = position; i < getDocumentModel().getJcas().getDocumentText().length(); i++) {
			MutableSet<Mention> mentions = characterPosition2AnnotationMap.get(i)
					.selectInstancesOf(MentionSurface.class).collect(ms -> ms.getMention());
			if (!mentions.isEmpty())
				return mentions.iterator().next();
		}
		return null;
	}

	public Mention getPreviousMention(int position) {
		for (int i = position - 1; i >= 0; i--) {
			MutableSet<Mention> mentions = characterPosition2AnnotationMap.get(i)
					.selectInstancesOf(MentionSurface.class).collect(ms -> ms.getMention());
			if (!mentions.isEmpty())
				return mentions.iterator().next();
		}
		return null;
	}

	/**
	 * Retrieve all annotations that cover the current character position
	 * 
	 * @param position The character position
	 * @return A collection of annotations
	 */
	public MutableSet<Annotation> getMentionSurfaces(int position) {
		return this.characterPosition2AnnotationMap.get(position);
	}

	public MutableSet<Mention> getMentions(int position) {
		return this.characterPosition2AnnotationMap.get(position).selectInstancesOf(MentionSurface.class)
				.collect(ms -> ms.getMention());
	}

	public ImmutableSet<Mention> getMentionsBetween(int start, int end) {
		MutableSet<Mention> mentions = Sets.mutable.empty();
		for (int i = start; i <= end; i++) {
			mentions.addAll(characterPosition2AnnotationMap.get(i).selectInstancesOf(MentionSurface.class)
					.collect(ms -> ms.getMention()));
		}
		return mentions.toImmutable();
	}

	// TODO: adapt to non-continuous mentions
	public ImmutableSet<Mention> getMatchingMentions(int start, int end) {
		MutableSet<Annotation> mentionSurfaces = Sets.mutable.empty();
		mentionSurfaces.addAll(
				characterPosition2AnnotationMap.get(start).select(m -> m.getEnd() == end && m.getBegin() == start));
		return mentionSurfaces.selectInstancesOf(MentionSurface.class).collect(ms -> ms.getMention()).toImmutable();
	}

	public Preferences getPreferences() {
		return documentModel.getPreferences();
	}

	private boolean getSpecialHandlingForSingletons() {
		return getPreferences().getBoolean(Constants.CFG_UNDERLINE_SINGLETONS_IN_GRAY,
				Defaults.CFG_UNDERLINE_SINGLETONS_IN_GRAY);
	}

	public String getToolTipText(FeatureStructure featureStructure) {
		if (featureStructure instanceof Entity) {
			Entity e = (Entity) featureStructure;
			if (UimaUtil.isGroup(featureStructure)) {
				StringBuilder b = new StringBuilder();
				if (e.getMembers(0) != null && e.getMembers(0).getLabel() != null)
					b.append(e.getMembers(0).getLabel());
				for (int i = 1; i < e.getMembers().size(); i++) {
					b.append(", ");
					b.append(e.getMembers(i).getLabel());
				}
				return b.toString();
			} else
				return e.getLabel();

		}
		return null;
	}

	@Override
	protected void initializeOnce() {
		for (Entity entity : JCasUtil.select(documentModel.getJcas(), Entity.class)) {
			if (entity.getKey() != null)
				keyMap.put(Character.valueOf(entity.getKey().charAt(0)), entity);
		}
		for (Mention mention : JCasUtil.select(documentModel.getJcas(), Mention.class)) {
			entityMentionMap.put(mention.getEntity(), mention);
			try {
				// TODO: Why do we do that?
				// mention.getEntity().addToIndexes();
			} catch (CASRuntimeException e) {
				Annotator.logger.catching(e);
			}
			registerAnnotation(mention);
		}
	}

	@Deprecated
	public void initialPainting() {
		if (initialized)
			return;
		for (Entity entity : JCasUtil.select(documentModel.getJcas(), Entity.class)) {
			fireEvent(Event.get(this, Event.Type.Add, null, entity));
			if (entity.getKey() != null)
				keyMap.put(new Character(entity.getKey().charAt(0)), entity);
		}
		for (Mention mention : JCasUtil.select(documentModel.getJcas(), Mention.class)) {
			entityMentionMap.put(mention.getEntity(), mention);
			mention.getEntity().addToIndexes();
			registerAnnotation(mention);
			fireEvent(Event.get(this, Event.Type.Add, mention.getEntity(), mention));

		}
		initialized = true;
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
					fireEvent(Event.get(this, Event.Type.Move, n, tgt, entityMentionMap.get(n).toList().toImmutable()));
					fireEvent(Event.get(this, Event.Type.Remove, n));
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

	public void registerAnnotation(Mention a) {
		for (MentionSurface ms : a.getSurface())
			registerAnnotation(ms);
	}

	private void registerEdit(Operation operation) {
		documentModel.fireDocumentChangedEvent();
	}

	/**
	 * Removes entity and fires events
	 * 
	 * @param entity
	 */
	private void remove(Entity entity) {
		Annotator.logger.traceEntry();
		fireEvent(Event.get(this, Event.Type.Remove, entity, entityMentionMap.get(entity).toList().toImmutable()));
		for (Mention m : entityMentionMap.get(entity)) {
			for (MentionSurface ms : m.getSurface())
				characterPosition2AnnotationMap.remove(ms);
			m.removeFromIndexes();
			// TODO: remove parts
		}
		for (Entity group : entityEntityGroupMap.get(entity)) {
			group.setMembers(UimaUtil.removeFrom(documentModel.getJcas(), group.getMembers(), entity));
			updateEntityGroupLabel(group);
			fireEvent(Event.get(this, Event.Type.Remove, group, entity));
		}

		entityEntityGroupMap.removeAll(entity);

		fireEvent(Event.get(this, Event.Type.Remove, null, entity));
		entityMentionMap.removeAll(entity);
		entity.removeFromIndexes();
		Annotator.logger.traceExit();
	}

	private void remove(Mention m, boolean autoRemove) {
		Entity entity = m.getEntity();
		for (MentionSurface ms : m.getSurface())
			characterPosition2AnnotationMap.remove(ms);
		entityMentionMap.remove(entity, m);
		m.removeFromIndexes();
		if (autoRemove && entityMentionMap.get(entity).isEmpty() && getPreferences()
				.getBoolean(Constants.CFG_DELETE_EMPTY_ENTITIES, Defaults.CFG_DELETE_EMPTY_ENTITIES)) {
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
	private void removeFrom(Entity eg, Entity entity) {
		FSArray<Entity> oldArray = eg.getMembers();
		FSArray<Entity> arr = new FSArray<Entity>(documentModel.getJcas(), oldArray.size() - 1);

		for (int i = 0, j = 0; i < oldArray.size() - 1 && j < arr.size() - 1; i++, j++) {

			if (eg.getMembers(i) == entity) {
				i++;
			}
			arr.set(j, eg.getMembers(i));

		}
		eg.setMembers(arr);
		fireEvent(Event.get(this, Event.Type.Remove, eg, entity));
	}

	protected void undo(AddSpanToMention op) {
		MentionSurface ms = op.getMentionSurface();
		UimaUtil.removeMentionSurface(ms.getMention(), ms);
		fireEvent(Event.get(this, Event.Type.Remove, ms.getMention(), ms));
		ms.removeFromIndexes();
		characterPosition2AnnotationMap.remove(ms);
	}

	protected void undo(CoreferenceModelOperation operation) {
		Annotator.logger.traceEntry();
		if (operation instanceof UpdateEntityName) {
			UpdateEntityName op = (UpdateEntityName) operation;
			op.getEntity().setLabel(op.getOldLabel());
			fireEvent(Event.get(this, Event.Type.Update, op.getEntity()));
		} else if (operation instanceof UpdateEntityKey) {
			UpdateEntityKey op = (UpdateEntityKey) operation;
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
				fireEvent(Event.get(this, Event.Type.Update, op.getObjects().getFirst(), op.getPreviousOwner()));
			else
				fireEvent(Event.get(this, Event.Type.Update, op.getObjects().getFirst()));
		} else if (operation instanceof ToggleGenericFlag) {
			edit((ToggleGenericFlag) operation);
		} else if (operation instanceof UpdateEntityColor) {
			UpdateEntityColor op = (UpdateEntityColor) operation;
			op.getObjects().getFirst().setColor(op.getOldColor());
			fireEvent(Event.get(this, Event.Type.Update, op.getObjects()));
			fireEvent(Event.get(this, Event.Type.Update, op.getObjects().flatCollect(e -> entityMentionMap.get(e))));
		} else if (operation instanceof AddEntityToEntityGroup) {
			AddEntityToEntityGroup op = (AddEntityToEntityGroup) operation;
			op.getEntities().forEach(e -> removeFrom(op.getEntityGroup(), e));
			updateEntityGroupLabel(op.getEntityGroup());
		} else if (operation instanceof AddMentionsToNewEntity) {
			AddMentionsToNewEntity op = (AddMentionsToNewEntity) operation;
			remove(op.getEntity());
		} else if (operation instanceof AddMentionsToEntity) {
			AddMentionsToEntity op = (AddMentionsToEntity) operation;
			op.getMentions().forEach(m -> remove(m, false));
			fireEvent(Event.get(this, Event.Type.Remove, op.getEntity(), op.getMentions()));
			if (getSpecialHandlingForSingletons() && getSize(op.getEntity()) == 1) {
				fireEvent(Event.get(this, Event.Type.Update, get(op.getEntity())));
			}
		} else if (operation instanceof MoveMentionsToEntity) {
			MoveMentionsToEntity op = (MoveMentionsToEntity) operation;
			op.getMentions().forEach(m -> moveTo(op.getSource(), m));
			fireEvent(Event.get(this, Event.Type.Move, op.getTarget(), op.getSource(), op.getMentions()));
			if (getSpecialHandlingForSingletons() && getSize(op.getTarget()) == 1) {
				fireEvent(Event.get(this, Event.Type.Update, get(op.getTarget())));
			}
			if (getSpecialHandlingForSingletons() && getSize(op.getSource()) - op.getMentions().size() == 1) {
				fireEvent(Event.get(this, Event.Type.Update, get(op.getSource())));
			}

		} else if (operation instanceof RemoveDuplicateMentionsInEntities) {
			RemoveDuplicateMentionsInEntities op = (RemoveDuplicateMentionsInEntities) operation;

			op.getFeatureStructures().forEach(m -> {
				m.addToIndexes();
				entityMentionMap.put(m.getEntity(), m);
				registerAnnotation(m);
				fireEvent(Event.get(this, Type.Add, m.getEntity(), m));
			});
		} else if (operation instanceof RemoveMention) {
			undo((RemoveMention) operation);
		} else if (operation instanceof RemoveEntities) {
			RemoveEntities op = (RemoveEntities) operation;
			op.getFeatureStructures().forEach(e -> {
				e.addToIndexes();
				if (op.entityEntityGroupMap.containsKey(e)) {
					for (Entity group : op.entityEntityGroupMap.get(e)) {
						group.setMembers(UimaUtil.addTo(documentModel.getJcas(), group.getMembers(), e));
						entityEntityGroupMap.put(e, group);
						updateEntityGroupLabel(group);
					}
				}
			});
			fireEvent(Event.get(this, Event.Type.Add, null, op.getFeatureStructures()));
		} else if (operation instanceof RemoveEntitiesFromEntityGroup) {
			RemoveEntitiesFromEntityGroup op = (RemoveEntitiesFromEntityGroup) operation;
			FSArray<Entity> oldArr = op.getEntityGroup().getMembers();
			FSArray<Entity> newArr = new FSArray<Entity>(documentModel.getJcas(),
					oldArr.size() + op.getEntities().size());
			int i = 0;
			for (; i < oldArr.size(); i++) {
				newArr.set(i, oldArr.get(i));
			}
			for (; i < newArr.size(); i++) {
				newArr.set(i, op.getEntities().get(i - oldArr.size()));
			}
			op.getEntityGroup().setMembers(newArr);
			updateEntityGroupLabel(op.getEntityGroup());
			newArr.addToIndexes();
			oldArr.removeFromIndexes();
		} else if (operation instanceof RemoveSingletons) {
			undo((RemoveSingletons) operation);
		} else if (operation instanceof MergeEntities) {
			MergeEntities op = (MergeEntities) operation;
			for (Entity oldEntity : op.getEntities()) {
				if (op.getEntity() != oldEntity) {
					oldEntity.addToIndexes();
					fireEvent(Event.get(this, Event.Type.Add, null, oldEntity));
					for (Mention m : op.getPreviousState().get(oldEntity)) {
						moveTo(oldEntity, m);
					}
					fireEvent(Event.get(this, Type.Move, null, oldEntity,
							op.getPreviousState().get(oldEntity).toList().toImmutable()));
				}
			}
		} else if (operation instanceof GroupEntities) {
			GroupEntities op = (GroupEntities) operation;
			remove(op.getEntityGroup());
			op.getEntities().forEach(e -> entityEntityGroupMap.remove(e, op.getEntityGroup()));
			fireEvent(Event.get(this, Event.Type.Remove, null, op.getEntityGroup()));
		} else if (operation instanceof RenameAllEntities) {
			undo((RenameAllEntities) operation);
		} else if (operation instanceof MergeMentions) {
			undo((MergeMentions) operation);
		} else if (operation instanceof DuplicateMentions) {
			undo((DuplicateMentions) operation);
		} else if (operation instanceof RemoveMentionSurface) {
			undo((RemoveMentionSurface) operation);
		} else if (operation instanceof AddSpanToMention) {
			undo((AddSpanToMention) operation);
		}
	}

	private void undo(DuplicateMentions op) {

	}

	private void undo(RemoveMention op) {
		if (op.isEntityAutoDeleted()) {
			op.getEntity().addToIndexes();
			fireEvent(Event.get(this, Event.Type.Add, null, op.getEntity()));
		}

		// re-create all mentions and set them to the op
		op.getFeatureStructures().forEach(m -> {
			m.addToIndexes();
			m.setEntity(op.getEntity());
			entityMentionMap.put(op.getEntity(), m);
			for (MentionSurface ms : m.getSurface())
				characterPosition2AnnotationMap.add(ms);
		});
		// fire event to draw them
		fireEvent(Event.get(this, Event.Type.Add, op.getEntity(), op.getFeatureStructures()));
		// re-create attached parts (if any)
		// TODO: re-create surfaces
	}

	private void undo(RemoveMentionSurface op) {
		for (int i = 0; i < op.getSpans().size(); i++) {
			Mention m = op.getMention(i);
			Span s = op.getSpan(i);
			MentionSurface ms = AnnotationFactory.createAnnotation(getJCas(), s.begin, s.end, MentionSurface.class);
			ms.setMention(m);
			UimaUtil.addMentionSurface(m, ms);
			fireEvent(Event.get(this, Event.Type.Add, m, ms));
		}
	}

	private void undo(RemoveSingletons op) {
		op.getFeatureStructures().forEach(e -> e.addToIndexes());
		op.getMentions().forEach(m -> {
			entityMentionMap.put(m.getEntity(), m);
			for (MentionSurface ms : m.getSurface())
				characterPosition2AnnotationMap.add(ms);
			m.addToIndexes();
			m.getEntity().addToIndexes();
			fireEvent(Event.get(this, Event.Type.Add, null, m.getEntity()));
			fireEvent(Event.get(this, Event.Type.Add, m.getEntity(), m));
		});
		fireEvent(Event.get(this, Event.Type.Add, null, op.getFeatureStructures()));
	}

	protected void undo(RenameAllEntities operation) {
		for (Entity entity : operation.getOldNames().keySet()) {
			entity.setLabel(operation.getOldNames().get(entity));
		}
		fireEvent(Event.get(this, Event.Type.Update, operation.getOldNames().keySet()));
	}

	/**
	 * Mention firstMention = op.getMentions().getFirstOptional().get(); int begin =
	 * op.getMentions().getFirstOptional().get().getBegin(); int end =
	 * op.getMentions().getLastOptional().get().getEnd(); Mention newMention =
	 * addTo(firstMention.getEntity(), begin, end);
	 * 
	 * op.getMentions().forEach(m -> { remove(m, false); if (m.getDiscontinuous() !=
	 * null) { DetachedMentionPart dmp = m.getDiscontinuous(); remove(dmp);
	 * fireEvent(Event.get(this, Type.Remove, m, dmp)); } });
	 * fireEvent(Event.get(this, Event.Type.Remove, firstMention.getEntity(),
	 * op.getMentions())); fireEvent(Event.get(this, Type.Add,
	 * newMention.getEntity(), newMention)); op.setNewMention(newMention);
	 * registerEdit(op);
	 * 
	 * @param operation
	 */
	protected void undo(MergeMentions operation) {
		operation.getMentions().forEach(m -> {
			m.addToIndexes();
			entityMentionMap.put(operation.getNewMention().getEntity(), m);
			fireEvent(Event.get(this, Type.Add, m.getEntity(), m));
		});
		remove(operation.getNewMention(), false);
		fireEvent(Event.get(this, Type.Remove, operation.getNewMention().getEntity(), operation.getNewMention()));
	}

	private void updateEntityGroupLabel(Entity entityGroup) {
		entityGroup.setLabel(createEntityGroupLabel(
				Lists.immutable.ofAll(entityGroup.getMembers()).selectInstancesOf(Entity.class)));

	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equalsIgnoreCase(Constants.CFG_UNDERLINE_SINGLETONS_IN_GRAY)) {
			ImmutableList<Mention> mentions = getSingletons().collect(e -> getMentions(e).getOnly());
			fireEvent(Event.get(this, Event.Type.Update, mentions));
		}
	}

}
