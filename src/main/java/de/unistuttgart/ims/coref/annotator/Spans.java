package de.unistuttgart.ims.coref.annotator;

import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class Spans implements ImmutableList<Span> {

	ImmutableList<Span> spans;

	public Spans(Annotation a) {
		spans = Lists.immutable.with(new Span(a));
	}

	public Spans(Mention m) {
		spans = Lists.immutable.withAll(m.getSurface()).collect(ms -> new Span(ms));
	}

	public int getBegin() {
		return spans.collect(s -> s.begin).min();
	}

	public int getEnd() {
		return spans.collect(s -> s.end).max();
	}

	@Override
	public void forEach(Procedure<? super Span> procedure) {
		spans.forEach(procedure);
	}

	@Override
	public Iterator<Span> iterator() {
		return spans.iterator();
	}

	@Override
	public void forEach(Consumer<? super Span> consumer) {
		spans.forEach(consumer);
	}

	@Override
	@Deprecated
	public void forEachWithIndex(ObjectIntProcedure<? super Span> objectIntProcedure) {
		spans.forEachWithIndex(objectIntProcedure);
	}

	@Override
	public void reverseForEach(Procedure<? super Span> procedure) {
		spans.reverseForEach(procedure);
	}

	@Override
	public int indexOf(Object object) {
		return spans.indexOf(object);
	}

	@Override
	public ImmutableList<Span> newWith(Span element) {
		return spans.newWith(element);
	}

	@Override
	public ImmutableList<Span> newWithout(Span element) {
		return spans.newWithout(element);
	}

	@Override
	public Span get(int index) {
		return spans.get(index);
	}

	@Override
	public ImmutableList<Span> newWithAll(Iterable<? extends Span> elements) {
		return spans.newWithAll(elements);
	}

	@Override
	public ImmutableList<Span> newWithoutAll(Iterable<? extends Span> elements) {
		return spans.newWithoutAll(elements);
	}

	@Override
	public void reverseForEachWithIndex(ObjectIntProcedure<? super Span> procedure) {
		spans.reverseForEachWithIndex(procedure);
	}

	@Override
	public int lastIndexOf(Object o) {
		return spans.lastIndexOf(o);
	}

	@Override
	public <P> void forEachWith(Procedure2<? super Span, ? super P> procedure, P parameter) {
		spans.forEachWith(procedure, parameter);
	}

	@Override
	public ImmutableList<Span> tap(Procedure<? super Span> procedure) {
		return spans.tap(procedure);
	}

	@Override
	public ImmutableList<Span> select(Predicate<? super Span> predicate) {
		return spans.select(predicate);
	}

	@Override
	public <P> ImmutableList<Span> selectWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.selectWith(predicate, parameter);
	}

	@Override
	public ImmutableList<Span> reject(Predicate<? super Span> predicate) {
		return spans.reject(predicate);
	}

	@Override
	public LazyIterable<Span> asReversed() {
		return spans.asReversed();
	}

	@Override
	public <P> ImmutableList<Span> rejectWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.rejectWith(predicate, parameter);
	}

	@Override
	public Optional<Span> getFirstOptional() {
		return spans.getFirstOptional();
	}

	@Override
	public PartitionImmutableList<Span> partition(Predicate<? super Span> predicate) {
		return spans.partition(predicate);
	}

	@Override
	public <P> PartitionImmutableList<Span> partitionWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.partitionWith(predicate, parameter);
	}

	@Override
	public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz) {
		return spans.selectInstancesOf(clazz);
	}

	@Override
	public ListIterator<Span> listIterator() {
		return spans.listIterator();
	}

	@Override
	public int detectLastIndex(Predicate<? super Span> predicate) {
		return spans.detectLastIndex(predicate);
	}

	@Override
	public <V> ImmutableList<V> collect(Function<? super Span, ? extends V> function) {
		return spans.collect(function);
	}

	@Override
	public ListIterator<Span> listIterator(int index) {
		return spans.listIterator(index);
	}

	@Override
	public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super Span, ? extends V> function) {
		return spans.collectWithIndex(function);
	}

	@Override
	public MutableStack<Span> toStack() {
		return spans.toStack();
	}

	@Override
	public ImmutableList<Span> toImmutable() {
		return spans.toImmutable();
	}

	@Override
	public ImmutableBooleanList collectBoolean(BooleanFunction<? super Span> booleanFunction) {
		return spans.collectBoolean(booleanFunction);
	}

	@Override
	public ImmutableByteList collectByte(ByteFunction<? super Span> byteFunction) {
		return spans.collectByte(byteFunction);
	}

	@Override
	public int size() {
		return spans.size();
	}

	@Override
	public ImmutableCharList collectChar(CharFunction<? super Span> charFunction) {
		return spans.collectChar(charFunction);
	}

	@Override
	public boolean isEmpty() {
		return spans.isEmpty();
	}

	@Override
	public ImmutableDoubleList collectDouble(DoubleFunction<? super Span> doubleFunction) {
		return spans.collectDouble(doubleFunction);
	}

	@Override
	public ImmutableFloatList collectFloat(FloatFunction<? super Span> floatFunction) {
		return spans.collectFloat(floatFunction);
	}

	@Override
	public boolean notEmpty() {
		return spans.notEmpty();
	}

	@Override
	public Optional<Span> getLastOptional() {
		return spans.getLastOptional();
	}

	@Override
	public ImmutableIntList collectInt(IntFunction<? super Span> intFunction) {
		return spans.collectInt(intFunction);
	}

	@Override
	public ImmutableLongList collectLong(LongFunction<? super Span> longFunction) {
		return spans.collectLong(longFunction);
	}

	@Override
	public Span getFirst() {
		return spans.getFirst();
	}

	@Override
	public ImmutableShortList collectShort(ShortFunction<? super Span> shortFunction) {
		return spans.collectShort(shortFunction);
	}

	@Override
	public <P, V> ImmutableList<V> collectWith(Function2<? super Span, ? super P, ? extends V> function, P parameter) {
		return spans.collectWith(function, parameter);
	}

	@Override
	public <V> ImmutableList<V> collectIf(Predicate<? super Span> predicate,
			Function<? super Span, ? extends V> function) {
		return spans.collectIf(predicate, function);
	}

	@Override
	public <V> ImmutableList<V> flatCollect(Function<? super Span, ? extends Iterable<V>> function) {
		return spans.flatCollect(function);
	}

	@Override
	public <P, V> ImmutableList<V> flatCollectWith(Function2<? super Span, ? super P, ? extends Iterable<V>> function,
			P parameter) {
		return spans.flatCollectWith(function, parameter);
	}

	@Override
	public <V> ImmutableListMultimap<V, Span> groupBy(Function<? super Span, ? extends V> function) {
		return spans.groupBy(function);
	}

	@Override
	public Span getLast() {
		return spans.getLast();
	}

	@Override
	public <V> ImmutableListMultimap<V, Span> groupByEach(Function<? super Span, ? extends Iterable<V>> function) {
		return spans.groupByEach(function);
	}

	@Override
	public ImmutableList<Span> distinct() {
		return spans.distinct();
	}

	@Override
	public ImmutableList<Span> distinct(HashingStrategy<? super Span> hashingStrategy) {
		return spans.distinct(hashingStrategy);
	}

	@Override
	public <V> ImmutableList<Span> distinctBy(Function<? super Span, ? extends V> function) {
		return spans.distinctBy(function);
	}

	@Override
	public <S> ImmutableList<Pair<Span, S>> zip(Iterable<S> that) {
		return spans.zip(that);
	}

	@Override
	public ImmutableList<Pair<Span, Integer>> zipWithIndex() {
		return spans.zipWithIndex();
	}

	@Override
	public ImmutableList<Span> take(int count) {
		return spans.take(count);
	}

	@Override
	public ImmutableList<Span> takeWhile(Predicate<? super Span> predicate) {
		return spans.takeWhile(predicate);
	}

	@Override
	public ImmutableList<Span> drop(int count) {
		return spans.drop(count);
	}

	@Override
	public ImmutableList<Span> dropWhile(Predicate<? super Span> predicate) {
		return spans.dropWhile(predicate);
	}

	@Override
	public Span getOnly() {
		return spans.getOnly();
	}

	@Override
	public PartitionImmutableList<Span> partitionWhile(Predicate<? super Span> predicate) {
		return spans.partitionWhile(predicate);
	}

	@Override
	public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super Span, ? super S> predicate) {
		return spans.corresponds(other, predicate);
	}

	@Override
	public List<Span> castToList() {
		return spans.castToList();
	}

	@Override
	public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super Span, ? extends V> groupBy,
			IntFunction<? super Span> function) {
		return spans.sumByInt(groupBy, function);
	}

	@Override
	public ImmutableList<Span> subList(int fromIndex, int toIndex) {
		return spans.subList(fromIndex, toIndex);
	}

	@Override
	public <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super Span, ? extends V> groupBy,
			FloatFunction<? super Span> function) {
		return spans.sumByFloat(groupBy, function);
	}

	@Override
	public ImmutableList<Span> toReversed() {
		return spans.toReversed();
	}

	@Override
	public <V> ImmutableObjectLongMap<V> sumByLong(Function<? super Span, ? extends V> groupBy,
			LongFunction<? super Span> function) {
		return spans.sumByLong(groupBy, function);
	}

	@Override
	public boolean contains(Object object) {
		return spans.contains(object);
	}

	@Override
	public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super Span, ? extends V> groupBy,
			DoubleFunction<? super Span> function) {
		return spans.sumByDouble(groupBy, function);
	}

	@Override
	public void forEach(int startIndex, int endIndex, Procedure<? super Span> procedure) {
		spans.forEach(startIndex, endIndex, procedure);
	}

	@Override
	public boolean containsAllIterable(Iterable<?> source) {
		return spans.containsAllIterable(source);
	}

	@Override
	public <V> ImmutableBag<V> countBy(Function<? super Span, ? extends V> function) {
		return spans.countBy(function);
	}

	@Override
	public boolean containsAll(Collection<?> source) {
		return spans.containsAll(source);
	}

	@Override
	public <V, P> ImmutableBag<V> countByWith(Function2<? super Span, ? super P, ? extends V> function, P parameter) {
		return spans.countByWith(function, parameter);
	}

	@Override
	public boolean containsAllArguments(Object... elements) {
		return spans.containsAllArguments(elements);
	}

	@Override
	public <V> ImmutableMap<V, Span> groupByUniqueKey(Function<? super Span, ? extends V> function) {
		return spans.groupByUniqueKey(function);
	}

	@Override
	public <K, V> ImmutableMap<K, V> aggregateInPlaceBy(Function<? super Span, ? extends K> groupBy,
			Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super Span> mutatingAggregator) {
		return spans.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
	}

	@Override
	public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super Span> objectIntProcedure) {
		spans.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
	}

	@Override
	public <K, V> ImmutableMap<K, V> aggregateBy(Function<? super Span, ? extends K> groupBy,
			Function0<? extends V> zeroValueFactory,
			Function2<? super V, ? super Span, ? extends V> nonMutatingAggregator) {
		return spans.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
	}

	@Override
	public void each(Procedure<? super Span> procedure) {
		spans.each(procedure);
	}

	@Override
	public Stream<Span> stream() {
		return spans.stream();
	}

	@Override
	public Stream<Span> parallelStream() {
		return spans.parallelStream();
	}

	@Override
	public Spliterator<Span> spliterator() {
		return spans.spliterator();
	}

	@Override
	public Collection<Span> castToCollection() {
		return spans.castToCollection();
	}

	@Override
	public ParallelListIterable<Span> asParallel(ExecutorService executorService, int batchSize) {
		return spans.asParallel(executorService, batchSize);
	}

	@Override
	public int binarySearch(Span key, Comparator<? super Span> comparator) {
		return spans.binarySearch(key, comparator);
	}

	@Override
	public int binarySearch(Span key) {
		return spans.binarySearch(key);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Spans))
			return false;
		Spans other = (Spans) o;
		if (spans.size() != other.spans.size())
			return false;
		for (int i = 0; i < spans.size(); i++) {
			if (!spans.get(i).equals(other.spans.get(i)))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return spans.hashCode();
	}

	@Override
	public <R extends Collection<Span>> R select(Predicate<? super Span> predicate, R target) {
		return spans.select(predicate, target);
	}

	@Override
	public <V, R extends Collection<V>> R collectWithIndex(
			ObjectIntToObjectFunction<? super Span, ? extends V> function, R target) {
		return spans.collectWithIndex(function, target);
	}

	@Override
	public <P, R extends Collection<Span>> R selectWith(Predicate2<? super Span, ? super P> predicate, P parameter,
			R targetCollection) {
		return spans.selectWith(predicate, parameter, targetCollection);
	}

	@Override
	public int detectIndex(Predicate<? super Span> predicate) {
		return spans.detectIndex(predicate);
	}

	@Override
	public <R extends Collection<Span>> R reject(Predicate<? super Span> predicate, R target) {
		return spans.reject(predicate, target);
	}

	@Override
	public <P, R extends Collection<Span>> R rejectWith(Predicate2<? super Span, ? super P> predicate, P parameter,
			R targetCollection) {
		return spans.rejectWith(predicate, parameter, targetCollection);
	}

	@Override
	public <V, R extends Collection<V>> R collect(Function<? super Span, ? extends V> function, R target) {
		return spans.collect(function, target);
	}

	@Override
	public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super Span> booleanFunction,
			R target) {
		return spans.collectBoolean(booleanFunction, target);
	}

	@Override
	public <R extends MutableByteCollection> R collectByte(ByteFunction<? super Span> byteFunction, R target) {
		return spans.collectByte(byteFunction, target);
	}

	@Override
	public <R extends MutableCharCollection> R collectChar(CharFunction<? super Span> charFunction, R target) {
		return spans.collectChar(charFunction, target);
	}

	@Override
	public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super Span> doubleFunction, R target) {
		return spans.collectDouble(doubleFunction, target);
	}

	@Override
	public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super Span> floatFunction, R target) {
		return spans.collectFloat(floatFunction, target);
	}

	@Override
	public <R extends MutableIntCollection> R collectInt(IntFunction<? super Span> intFunction, R target) {
		return spans.collectInt(intFunction, target);
	}

	@Override
	public <R extends MutableLongCollection> R collectLong(LongFunction<? super Span> longFunction, R target) {
		return spans.collectLong(longFunction, target);
	}

	@Override
	public <R extends MutableShortCollection> R collectShort(ShortFunction<? super Span> shortFunction, R target) {
		return spans.collectShort(shortFunction, target);
	}

	@Override
	public <P, V, R extends Collection<V>> R collectWith(Function2<? super Span, ? super P, ? extends V> function,
			P parameter, R targetCollection) {
		return spans.collectWith(function, parameter, targetCollection);
	}

	@Override
	public <V, R extends Collection<V>> R collectIf(Predicate<? super Span> predicate,
			Function<? super Span, ? extends V> function, R target) {
		return spans.collectIf(predicate, function, target);
	}

	@Override
	public <V, R extends Collection<V>> R flatCollect(Function<? super Span, ? extends Iterable<V>> function,
			R target) {
		return spans.flatCollect(function, target);
	}

	@Override
	public <P, V, R extends Collection<V>> R flatCollectWith(
			Function2<? super Span, ? super P, ? extends Iterable<V>> function, P parameter, R target) {
		return spans.flatCollectWith(function, parameter, target);
	}

	@Override
	public Span detect(Predicate<? super Span> predicate) {
		return spans.detect(predicate);
	}

	@Override
	public <P> Span detectWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.detectWith(predicate, parameter);
	}

	@Override
	public Optional<Span> detectOptional(Predicate<? super Span> predicate) {
		return spans.detectOptional(predicate);
	}

	@Override
	public <P> Optional<Span> detectWithOptional(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.detectWithOptional(predicate, parameter);
	}

	@Override
	public Span detectIfNone(Predicate<? super Span> predicate, Function0<? extends Span> function) {
		return spans.detectIfNone(predicate, function);
	}

	@Override
	public <P> Span detectWithIfNone(Predicate2<? super Span, ? super P> predicate, P parameter,
			Function0<? extends Span> function) {
		return spans.detectWithIfNone(predicate, parameter, function);
	}

	@Override
	public int count(Predicate<? super Span> predicate) {
		return spans.count(predicate);
	}

	@Override
	public <P> int countWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.countWith(predicate, parameter);
	}

	@Override
	public boolean anySatisfy(Predicate<? super Span> predicate) {
		return spans.anySatisfy(predicate);
	}

	@Override
	public <P> boolean anySatisfyWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.anySatisfyWith(predicate, parameter);
	}

	@Override
	public boolean allSatisfy(Predicate<? super Span> predicate) {
		return spans.allSatisfy(predicate);
	}

	@Override
	public <P> boolean allSatisfyWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.allSatisfyWith(predicate, parameter);
	}

	@Override
	public boolean noneSatisfy(Predicate<? super Span> predicate) {
		return spans.noneSatisfy(predicate);
	}

	@Override
	public <P> boolean noneSatisfyWith(Predicate2<? super Span, ? super P> predicate, P parameter) {
		return spans.noneSatisfyWith(predicate, parameter);
	}

	@Override
	public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super Span, ? extends IV> function) {
		return spans.injectInto(injectedValue, function);
	}

	@Override
	public int injectInto(int injectedValue, IntObjectToIntFunction<? super Span> function) {
		return spans.injectInto(injectedValue, function);
	}

	@Override
	public long injectInto(long injectedValue, LongObjectToLongFunction<? super Span> function) {
		return spans.injectInto(injectedValue, function);
	}

	@Override
	public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super Span> function) {
		return spans.injectInto(injectedValue, function);
	}

	@Override
	public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super Span> function) {
		return spans.injectInto(injectedValue, function);
	}

	@Override
	public <R extends Collection<Span>> R into(R target) {
		return spans.into(target);
	}

	@Override
	public MutableList<Span> toList() {
		return spans.toList();
	}

	@Override
	public MutableList<Span> toSortedList() {
		return spans.toSortedList();
	}

	@Override
	public MutableList<Span> toSortedList(Comparator<? super Span> comparator) {
		return spans.toSortedList(comparator);
	}

	@Override
	public <V extends Comparable<? super V>> MutableList<Span> toSortedListBy(
			Function<? super Span, ? extends V> function) {
		return spans.toSortedListBy(function);
	}

	@Override
	public MutableSet<Span> toSet() {
		return spans.toSet();
	}

	@Override
	public MutableSortedSet<Span> toSortedSet() {
		return spans.toSortedSet();
	}

	@Override
	public MutableSortedSet<Span> toSortedSet(Comparator<? super Span> comparator) {
		return spans.toSortedSet(comparator);
	}

	@Override
	public <V extends Comparable<? super V>> MutableSortedSet<Span> toSortedSetBy(
			Function<? super Span, ? extends V> function) {
		return spans.toSortedSetBy(function);
	}

	@Override
	public MutableBag<Span> toBag() {
		return spans.toBag();
	}

	@Override
	public MutableSortedBag<Span> toSortedBag() {
		return spans.toSortedBag();
	}

	@Override
	public MutableSortedBag<Span> toSortedBag(Comparator<? super Span> comparator) {
		return spans.toSortedBag(comparator);
	}

	@Override
	public <V extends Comparable<? super V>> MutableSortedBag<Span> toSortedBagBy(
			Function<? super Span, ? extends V> function) {
		return spans.toSortedBagBy(function);
	}

	@Override
	public <NK, NV> MutableMap<NK, NV> toMap(Function<? super Span, ? extends NK> keyFunction,
			Function<? super Span, ? extends NV> valueFunction) {
		return spans.toMap(keyFunction, valueFunction);
	}

	@Override
	public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super Span, ? extends NK> keyFunction,
			Function<? super Span, ? extends NV> valueFunction) {
		return spans.toSortedMap(keyFunction, valueFunction);
	}

	@Override
	public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator,
			Function<? super Span, ? extends NK> keyFunction, Function<? super Span, ? extends NV> valueFunction) {
		return spans.toSortedMap(comparator, keyFunction, valueFunction);
	}

	@Override
	public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
			Function<? super NK, KK> sortBy, Function<? super Span, ? extends NK> keyFunction,
			Function<? super Span, ? extends NV> valueFunction) {
		return spans.toSortedMapBy(sortBy, keyFunction, valueFunction);
	}

	@Override
	public LazyIterable<Span> asLazy() {
		return spans.asLazy();
	}

	@Override
	public Object[] toArray() {
		return spans.toArray();
	}

	@Override
	public <T> T[] toArray(T[] target) {
		return spans.toArray(target);
	}

	@Override
	public Span min(Comparator<? super Span> comparator) {
		return spans.min(comparator);
	}

	@Override
	public Span max(Comparator<? super Span> comparator) {
		return spans.max(comparator);
	}

	@Override
	public Optional<Span> minOptional(Comparator<? super Span> comparator) {
		return spans.minOptional(comparator);
	}

	@Override
	public Optional<Span> maxOptional(Comparator<? super Span> comparator) {
		return spans.maxOptional(comparator);
	}

	@Override
	public Span min() {
		return spans.min();
	}

	@Override
	public Span max() {
		return spans.max();
	}

	@Override
	public Optional<Span> minOptional() {
		return spans.minOptional();
	}

	@Override
	public Optional<Span> maxOptional() {
		return spans.maxOptional();
	}

	@Override
	public <V extends Comparable<? super V>> Span minBy(Function<? super Span, ? extends V> function) {
		return spans.minBy(function);
	}

	@Override
	public <V extends Comparable<? super V>> Span maxBy(Function<? super Span, ? extends V> function) {
		return spans.maxBy(function);
	}

	@Override
	public <V extends Comparable<? super V>> Optional<Span> minByOptional(
			Function<? super Span, ? extends V> function) {
		return spans.minByOptional(function);
	}

	@Override
	public <V extends Comparable<? super V>> Optional<Span> maxByOptional(
			Function<? super Span, ? extends V> function) {
		return spans.maxByOptional(function);
	}

	@Override
	public long sumOfInt(IntFunction<? super Span> function) {
		return spans.sumOfInt(function);
	}

	@Override
	public double sumOfFloat(FloatFunction<? super Span> function) {
		return spans.sumOfFloat(function);
	}

	@Override
	public long sumOfLong(LongFunction<? super Span> function) {
		return spans.sumOfLong(function);
	}

	@Override
	public double sumOfDouble(DoubleFunction<? super Span> function) {
		return spans.sumOfDouble(function);
	}

	@Override
	public IntSummaryStatistics summarizeInt(IntFunction<? super Span> function) {
		return spans.summarizeInt(function);
	}

	@Override
	public DoubleSummaryStatistics summarizeFloat(FloatFunction<? super Span> function) {
		return spans.summarizeFloat(function);
	}

	@Override
	public LongSummaryStatistics summarizeLong(LongFunction<? super Span> function) {
		return spans.summarizeLong(function);
	}

	@Override
	public DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super Span> function) {
		return spans.summarizeDouble(function);
	}

	@Override
	public <R, A> R reduceInPlace(Collector<? super Span, A, R> collector) {
		return spans.reduceInPlace(collector);
	}

	@Override
	public <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super Span> accumulator) {
		return spans.reduceInPlace(supplier, accumulator);
	}

	@Override
	public Optional<Span> reduce(BinaryOperator<Span> accumulator) {
		return spans.reduce(accumulator);
	}

	@Override
	public String makeString() {
		return spans.makeString();
	}

	@Override
	public String makeString(String separator) {
		return spans.makeString(separator);
	}

	@Override
	public String makeString(String start, String separator, String end) {
		return spans.makeString(start, separator, end);
	}

	@Override
	public void appendString(Appendable appendable) {
		spans.appendString(appendable);
	}

	@Override
	public void appendString(Appendable appendable, String separator) {
		spans.appendString(appendable, separator);
	}

	@Override
	public void appendString(Appendable appendable, String start, String separator, String end) {
		spans.appendString(appendable, start, separator, end);
	}

	@Override
	public <V, R extends MutableBagIterable<V>> R countBy(Function<? super Span, ? extends V> function, R target) {
		return spans.countBy(function, target);
	}

	@Override
	public <V, P, R extends MutableBagIterable<V>> R countByWith(
			Function2<? super Span, ? super P, ? extends V> function, P parameter, R target) {
		return spans.countByWith(function, parameter, target);
	}

	@Override
	public <V, R extends MutableMultimap<V, Span>> R groupBy(Function<? super Span, ? extends V> function, R target) {
		return spans.groupBy(function, target);
	}

	@Override
	public <V, R extends MutableMultimap<V, Span>> R groupByEach(Function<? super Span, ? extends Iterable<V>> function,
			R target) {
		return spans.groupByEach(function, target);
	}

	@Override
	public String toString() {
		return spans.toString();
	}

	@Override
	@Deprecated
	public <S, R extends Collection<Pair<Span, S>>> R zip(Iterable<S> that, R target) {
		return spans.zip(that, target);
	}

	@Override
	@Deprecated
	public <R extends Collection<Pair<Span, Integer>>> R zipWithIndex(R target) {
		return spans.zipWithIndex(target);
	}

	@Override
	public RichIterable<RichIterable<Span>> chunk(int size) {
		return spans.chunk(size);
	}

	/**
	 * @param <V>
	 * @param <R>
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#groupByUniqueKey(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.map.MutableMapIterable)
	 */
	@Override
	public <V, R extends MutableMapIterable<V, Span>> R groupByUniqueKey(Function<? super Span, ? extends V> arg0,
			R arg1) {
		return spans.groupByUniqueKey(arg0, arg1);
	}

	/**
	 * @param <V>
	 * @param <R>
	 * @param function
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#countByEach(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.bag.MutableBagIterable)
	 */
	@Override
	public <V, R extends MutableBagIterable<V>> R countByEach(Function<? super Span, ? extends Iterable<V>> function,
			R target) {
		return spans.countByEach(function, target);
	}

	/**
	 * @param <V>
	 * @param function
	 * @return
	 * @see org.eclipse.collections.api.collection.ImmutableCollection#countByEach(org.eclipse.collections.api.block.function.Function)
	 */
	@Override
	public <V> ImmutableBag<V> countByEach(Function<? super Span, ? extends Iterable<V>> function) {
		return spans.countByEach(function);
	}

	/**
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#getAny()
	 */
	@Override
	public Span getAny() {
		return spans.getAny();
	}

	/**
	 * @param <K>
	 * @param <V>
	 * @param <R>
	 * @param groupByFunction
	 * @param collectFunction
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#groupByAndCollect(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.multimap.MutableMultimap)
	 */
	@Override
	public <K, V, R extends MutableMultimap<K, V>> R groupByAndCollect(
			Function<? super Span, ? extends K> groupByFunction, Function<? super Span, ? extends V> collectFunction,
			R target) {
		return spans.groupByAndCollect(groupByFunction, collectFunction, target);
	}

	/**
	 * @param <NK>
	 * @param <NV>
	 * @param keyFunction
	 * @param valueFunction
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#toBiMap(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.block.function.Function)
	 */
	@Override
	public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super Span, ? extends NK> keyFunction,
			Function<? super Span, ? extends NV> valueFunction) {
		return spans.toBiMap(keyFunction, valueFunction);
	}

	/**
	 * @param <NK>
	 * @param <NV>
	 * @param <R>
	 * @param keyFunction
	 * @param valueFunction
	 * @param target
	 * @return
	 * @see org.eclipse.collections.api.RichIterable#toMap(org.eclipse.collections.api.block.function.Function,
	 *      org.eclipse.collections.api.block.function.Function, java.util.Map)
	 */
	@Override
	public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super Span, ? extends NK> keyFunction,
			Function<? super Span, ? extends NV> valueFunction, R target) {
		return spans.toMap(keyFunction, valueFunction, target);
	}
}
