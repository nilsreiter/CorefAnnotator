package de.unistuttgart.ims.coref.annotator;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;

public class RangedHashSetValuedHashMap<V extends Annotation> implements MutableSetMultimap<Integer, V> {

	MutableSetMultimap<Integer, V> map = Multimaps.mutable.set.empty();

	public void add(V value) {
		this.put(new Span(value), value);
	}

	public void put(Span span, V value) {
		for (int i = span.begin; i < span.end; i++)
			put(i, value);
	}

	public void remove(V value) {
		for (int i = value.getBegin(); i < value.getEnd(); i++)
			remove(i, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return map.remove(key, value);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean notEmpty() {
		return map.notEmpty();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public int sizeDistinct() {
		return map.sizeDistinct();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public boolean containsKeyAndValue(Object key, Object value) {
		return map.containsKeyAndValue(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		return map.equals(obj);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public MutableSetMultimap<V, Integer> flip() {
		return map.flip();
	}

	@Override
	public MutableSet<V> replaceValues(Integer key, Iterable<? extends V> values) {
		return map.replaceValues(key, values);
	}

	@Override
	public MutableSetMultimap<Integer, V> toMutable() {
		return map.toMutable();
	}

	@Override
	public MutableSet<V> removeAll(Object key) {
		return map.removeAll(key);
	}

	@Override
	public ImmutableSetMultimap<Integer, V> toImmutable() {
		return map.toImmutable();
	}

	@Override
	public boolean put(Integer key, V value) {
		return map.put(key, value);
	}

	@Override
	public MutableSetMultimap<Integer, V> newEmpty() {
		return map.newEmpty();
	}

	@Override
	public MutableSet<V> get(Integer key) {
		return map.get(key);
	}

	@Override
	public MutableSetMultimap<Integer, V> selectKeysValues(Predicate2<? super Integer, ? super V> predicate) {
		return map.selectKeysValues(predicate);
	}

	@Override
	public MutableSetMultimap<Integer, V> rejectKeysValues(Predicate2<? super Integer, ? super V> predicate) {
		return map.rejectKeysValues(predicate);
	}

	@Override
	public MutableSetMultimap<Integer, V> selectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate) {
		return map.selectKeysMultiValues(predicate);
	}

	@Override
	public boolean putAllPairs(Pair<Integer, V>... pairs) {
		return map.putAllPairs(pairs);
	}

	@Override
	public MutableSetMultimap<Integer, V> rejectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate) {
		return map.rejectKeysMultiValues(predicate);
	}

	@Override
	public <K2, V2> MutableBagMultimap<K2, V2> collectKeysValues(
			Function2<? super Integer, ? super V, Pair<K2, V2>> function) {
		return map.collectKeysValues(function);
	}

	@Override
	public boolean putAllPairs(Iterable<Pair<Integer, V>> pairs) {
		return map.putAllPairs(pairs);
	}

	@Override
	public <V2> MutableBagMultimap<Integer, V2> collectValues(Function<? super V, ? extends V2> function) {
		return map.collectValues(function);
	}

	@Override
	public MutableSetMultimap<Integer, V> asSynchronized() {
		return map.asSynchronized();
	}

	@Override
	public boolean putAll(Integer key, Iterable<? extends V> values) {
		return map.putAll(key, values);
	}

	@Override
	public <KK extends Integer, VV extends V> boolean putAll(Multimap<KK, VV> multimap) {
		return map.putAll(multimap);
	}

	@Override
	public void forEachValue(Procedure<? super V> procedure) {
		map.forEachValue(procedure);
	}

	@Override
	public void forEachKey(Procedure<? super Integer> procedure) {
		map.forEachKey(procedure);
	}

	@Override
	public void forEachKeyValue(Procedure2<? super Integer, ? super V> procedure) {
		map.forEachKeyValue(procedure);
	}

	@Override
	public void forEachKeyMultiValues(Procedure2<? super Integer, ? super Iterable<V>> procedure) {
		map.forEachKeyMultiValues(procedure);
	}

	@Override
	public RichIterable<Integer> keysView() {
		return map.keysView();
	}

	@Override
	public SetIterable<Integer> keySet() {
		return map.keySet();
	}

	@Override
	public Bag<Integer> keyBag() {
		return map.keyBag();
	}

	@Override
	public RichIterable<RichIterable<V>> multiValuesView() {
		return map.multiValuesView();
	}

	@Override
	public RichIterable<V> valuesView() {
		return map.valuesView();
	}

	@Override
	public RichIterable<Pair<Integer, RichIterable<V>>> keyMultiValuePairsView() {
		return map.keyMultiValuePairsView();
	}

	@Override
	public RichIterable<Pair<Integer, V>> keyValuePairsView() {
		return map.keyValuePairsView();
	}

	@Override
	public MutableMap<Integer, RichIterable<V>> toMap() {
		return map.toMap();
	}

	@Override
	public <R extends Collection<V>> MutableMap<Integer, R> toMap(Function0<R> collectionFactory) {
		return map.toMap(collectionFactory);
	}

	@Override
	public <R extends MutableMultimap<Integer, V>> R selectKeysValues(Predicate2<? super Integer, ? super V> predicate,
			R target) {
		return map.selectKeysValues(predicate, target);
	}

	@Override
	public <R extends MutableMultimap<Integer, V>> R rejectKeysValues(Predicate2<? super Integer, ? super V> predicate,
			R target) {
		return map.rejectKeysValues(predicate, target);
	}

	@Override
	public <R extends MutableMultimap<Integer, V>> R selectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate, R target) {
		return map.selectKeysMultiValues(predicate, target);
	}

	@Override
	public <R extends MutableMultimap<Integer, V>> R rejectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate, R target) {
		return map.rejectKeysMultiValues(predicate, target);
	}

	@Override
	public <K2, V2, R extends MutableMultimap<K2, V2>> R collectKeysValues(
			Function2<? super Integer, ? super V, Pair<K2, V2>> function, R target) {
		return map.collectKeysValues(function, target);
	}

	@Override
	public <V2, R extends MutableMultimap<Integer, V2>> R collectValues(Function<? super V, ? extends V2> function,
			R target) {
		return map.collectValues(function, target);
	}

}
