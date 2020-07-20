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

	/**
	 * @param keyValuePair
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#add(org.eclipse.collections.api.tuple.Pair)
	 */
	@Override
	public boolean add(Pair<? extends Integer, ? extends V> keyValuePair) {
		return map.add(keyValuePair);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#asSynchronized()
	 */
	@Override
	public MutableSetMultimap<Integer, V> asSynchronized() {
		return map.asSynchronized();
	}

	/**
	 * 
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#clear()
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * @param <K2>
	 * @param <V2>
	 * @param <R>
	 * @param keyFunction
	 * @param valueFunction
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#collectKeyMultiValues(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <K2, V2, R extends MutableMultimap<K2, V2>> R collectKeyMultiValues(
			Function<? super Integer, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction,
			R target) {
		return map.collectKeyMultiValues(keyFunction, valueFunction, target);
	}

	/**
	 * @param <K2>
	 * @param <V2>
	 * @param keyFunction
	 * @param valueFunction
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#collectKeyMultiValues(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.block.function.Function)
	 */
	@Override
	public <K2, V2> MutableBagMultimap<K2, V2> collectKeyMultiValues(
			Function<? super Integer, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction) {
		return map.collectKeyMultiValues(keyFunction, valueFunction);
	}

	/**
	 * @param <K2>
	 * @param <V2>
	 * @param <R>
	 * @param function
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#collectKeysValues(org.eclipse.collections.api.block.function.Function2,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <K2, V2, R extends MutableMultimap<K2, V2>> R collectKeysValues(
			Function2<? super Integer, ? super V, Pair<K2, V2>> function, R target) {
		return map.collectKeysValues(function, target);
	}

	/**
	 * @param <K2>
	 * @param <V2>
	 * @param function
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#collectKeysValues(org.eclipse.collections.api.block.function.Function2)
	 */
	@Override
	public <K2, V2> MutableBagMultimap<K2, V2> collectKeysValues(
			Function2<? super Integer, ? super V, Pair<K2, V2>> function) {
		return map.collectKeysValues(function);
	}

	/**
	 * @param <V2>
	 * @param <R>
	 * @param function
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#collectValues(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <V2, R extends MutableMultimap<Integer, V2>> R collectValues(Function<? super V, ? extends V2> function,
			R target) {
		return map.collectValues(function, target);
	}

	/**
	 * @param <V2>
	 * @param function
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#collectValues(org.eclipse.collections.api.block.function.Function)
	 */
	@Override
	public <V2> MutableBagMultimap<Integer, V2> collectValues(Function<? super V, ? extends V2> function) {
		return map.collectValues(function);
	}

	/**
	 * @param key
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#containsKeyAndValue(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean containsKeyAndValue(Object key, Object value) {
		return map.containsKeyAndValue(key, value);
	}

	/**
	 * @param value
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * @param obj
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return map.equals(obj);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#flip()
	 */
	@Override
	public MutableSetMultimap<V, Integer> flip() {
		return map.flip();
	}

	/**
	 * @param procedure
	 * @see org.eclipse.collections.api.multimap.Multimap#forEachKey(org.eclipse.collections.api.block.procedure.Procedure)
	 */
	@Override
	public void forEachKey(Procedure<? super Integer> procedure) {
		map.forEachKey(procedure);
	}

	/**
	 * @param procedure
	 * @see org.eclipse.collections.api.multimap.Multimap#forEachKeyMultiValues(org.eclipse.collections.api.block.procedure.Procedure2)
	 */
	@Override
	public void forEachKeyMultiValues(Procedure2<? super Integer, ? super Iterable<V>> procedure) {
		map.forEachKeyMultiValues(procedure);
	}

	/**
	 * @param procedure
	 * @see org.eclipse.collections.api.multimap.Multimap#forEachKeyValue(org.eclipse.collections.api.block.procedure.Procedure2)
	 */
	@Override
	public void forEachKeyValue(Procedure2<? super Integer, ? super V> procedure) {
		map.forEachKeyValue(procedure);
	}

	/**
	 * @param procedure
	 * @see org.eclipse.collections.api.multimap.Multimap#forEachValue(org.eclipse.collections.api.block.procedure.Procedure)
	 */
	@Override
	public void forEachValue(Procedure<? super V> procedure) {
		map.forEachValue(procedure);
	}

	/**
	 * @param key
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#get(java.lang.Object)
	 */
	@Override
	public MutableSet<V> get(Integer key) {
		return map.get(key);
	}

	/**
	 * @param key
	 * @param values
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#getIfAbsentPutAll(java.lang.Object,
	 *      java.lang.Iterable)
	 */
	@Override
	public MutableSet<V> getIfAbsentPutAll(Integer key, Iterable<? extends V> values) {
		return map.getIfAbsentPutAll(key, values);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#keyBag()
	 */
	@Override
	public Bag<Integer> keyBag() {
		return map.keyBag();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#keyMultiValuePairsView()
	 */
	@Override
	public RichIterable<Pair<Integer, RichIterable<V>>> keyMultiValuePairsView() {
		return map.keyMultiValuePairsView();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#keySet()
	 */
	@Override
	public SetIterable<Integer> keySet() {
		return map.keySet();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#keyValuePairsView()
	 */
	@Override
	public RichIterable<Pair<Integer, V>> keyValuePairsView() {
		return map.keyValuePairsView();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#keysView()
	 */
	@Override
	public RichIterable<Integer> keysView() {
		return map.keysView();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#multiValuesView()
	 */
	@Override
	public RichIterable<RichIterable<V>> multiValuesView() {
		return map.multiValuesView();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#newEmpty()
	 */
	@Override
	public MutableSetMultimap<Integer, V> newEmpty() {
		return map.newEmpty();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#notEmpty()
	 */
	@Override
	public boolean notEmpty() {
		return map.notEmpty();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#put(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean put(Integer key, V value) {
		return map.put(key, value);
	}

	/**
	 * @param key
	 * @param values
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#putAll(java.lang.Object,
	 *      java.lang.Iterable)
	 */
	@Override
	public boolean putAll(Integer key, Iterable<? extends V> values) {
		return map.putAll(key, values);
	}

	/**
	 * @param <KK>
	 * @param <VV>
	 * @param multimap
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#putAll(org.eclipse.collections.api.multimap.Multimap)
	 */
	@Override
	public <KK extends Integer, VV extends V> boolean putAll(Multimap<KK, VV> multimap) {
		return map.putAll(multimap);
	}

	/**
	 * @param pairs
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#putAllPairs(java.lang.Iterable)
	 */
	@Override
	public boolean putAllPairs(Iterable<? extends Pair<? extends Integer, ? extends V>> pairs) {
		return map.putAllPairs(pairs);
	}

	/**
	 * @param pairs
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#putAllPairs(org.eclipse.collections.api.tuple.Pair[])
	 */
	@Override
	public boolean putAllPairs(Pair<? extends Integer, ? extends V>... pairs) {
		return map.putAllPairs(pairs);
	}

	/**
	 * @param <R>
	 * @param predicate
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#rejectKeysMultiValues(org.eclipse.collections.api.block.predicate.Predicate2,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <R extends MutableMultimap<Integer, V>> R rejectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate, R target) {
		return map.rejectKeysMultiValues(predicate, target);
	}

	/**
	 * @param predicate
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#rejectKeysMultiValues(org.eclipse.collections.api.block.predicate.Predicate2)
	 */
	@Override
	public MutableSetMultimap<Integer, V> rejectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate) {
		return map.rejectKeysMultiValues(predicate);
	}

	/**
	 * @param <R>
	 * @param predicate
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#rejectKeysValues(org.eclipse.collections.api.block.predicate.Predicate2,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <R extends MutableMultimap<Integer, V>> R rejectKeysValues(Predicate2<? super Integer, ? super V> predicate,
			R target) {
		return map.rejectKeysValues(predicate, target);
	}

	/**
	 * @param predicate
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#rejectKeysValues(org.eclipse.collections.api.block.predicate.Predicate2)
	 */
	@Override
	public MutableSetMultimap<Integer, V> rejectKeysValues(Predicate2<? super Integer, ? super V> predicate) {
		return map.rejectKeysValues(predicate);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see org.eclipse.collections.api.multimap.MutableMultimap#remove(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean remove(Object key, Object value) {
		return map.remove(key, value);
	}

	/**
	 * @param key
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#removeAll(java.lang.Object)
	 */
	@Override
	public MutableSet<V> removeAll(Object key) {
		return map.removeAll(key);
	}

	/**
	 * @param key
	 * @param values
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#replaceValues(java.lang.Object,
	 *      java.lang.Iterable)
	 */
	@Override
	public MutableSet<V> replaceValues(Integer key, Iterable<? extends V> values) {
		return map.replaceValues(key, values);
	}

	/**
	 * @param <R>
	 * @param predicate
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#selectKeysMultiValues(org.eclipse.collections.api.block.predicate.Predicate2,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <R extends MutableMultimap<Integer, V>> R selectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate, R target) {
		return map.selectKeysMultiValues(predicate, target);
	}

	/**
	 * @param predicate
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#selectKeysMultiValues(org.eclipse.collections.api.block.predicate.Predicate2)
	 */
	@Override
	public MutableSetMultimap<Integer, V> selectKeysMultiValues(
			Predicate2<? super Integer, ? super Iterable<V>> predicate) {
		return map.selectKeysMultiValues(predicate);
	}

	/**
	 * @param <R>
	 * @param predicate
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#selectKeysValues(org.eclipse.collections.api.block.predicate.Predicate2,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <R extends MutableMultimap<Integer, V>> R selectKeysValues(Predicate2<? super Integer, ? super V> predicate,
			R target) {
		return map.selectKeysValues(predicate, target);
	}

	/**
	 * @param predicate
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.MutableSetMultimap#selectKeysValues(org.eclipse.collections.api.block.predicate.Predicate2)
	 */
	@Override
	public MutableSetMultimap<Integer, V> selectKeysValues(Predicate2<? super Integer, ? super V> predicate) {
		return map.selectKeysValues(predicate);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#sizeDistinct()
	 */
	@Override
	public int sizeDistinct() {
		return map.sizeDistinct();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.UnsortedSetMultimap#toImmutable()
	 */
	@Override
	public ImmutableSetMultimap<Integer, V> toImmutable() {
		return map.toImmutable();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#toMap()
	 */
	@Override
	public MutableMap<Integer, RichIterable<V>> toMap() {
		return map.toMap();
	}

	/**
	 * @param <R>
	 * @param collectionFactory
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#toMap(org.eclipse.collections.api.block.function.Function0)
	 */
	@Override
	public <R extends Collection<V>> MutableMap<Integer, R> toMap(Function0<R> collectionFactory) {
		return map.toMap(collectionFactory);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.set.UnsortedSetMultimap#toMutable()
	 */
	@Override
	public MutableSetMultimap<Integer, V> toMutable() {
		return map.toMutable();
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.multimap.Multimap#valuesView()
	 */
	@Override
	public RichIterable<V> valuesView() {
		return map.valuesView();
	}

}
