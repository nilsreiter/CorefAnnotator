package de.unistuttgart.ims.coref.annotator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

public class Counter<K> extends HashMap<K, Integer> {
	private static final long serialVersionUID = 1L;

	public Counter() {
	}

	/**
	 * Copy constructor
	 *
	 * @param c
	 *            A counter object to copy from
	 */
	public Counter(final Counter<K> c) {
		for (final K k : c.keySet()) {
			put(k, c.get(k));
		}
	}

	/**
	 * Increases the value of k by 1. If k was not in the map before, it will be
	 * afterwards.
	 *
	 * @param k
	 *            the object we count
	 */
	public void add(final K k) {
		this.add(k, 1);
	}

	/**
	 * Increases the value of k by i. Adds k to the map if necessary.
	 *
	 * @param k
	 *            The countee
	 * @param i
	 *            The amount
	 */
	public void add(final K k, final int i) {
		if (!super.containsKey(k)) {
			super.put(k, i);
		} else {
			super.put(k, super.get(k) + i);
		}
	}

	/**
	 * Decreases the value of k by 1.
	 *
	 * @param k
	 *            the countee
	 */
	public void subtract(final K k) {
		if (!super.containsKey(k)) {
			super.put(k, -1);
		} else {
			super.put(k, super.get(k) - 1);
		}
	}

	/**
	 * Increases the value of <i>all</i> elements in arg by 1.
	 *
	 * @param arg
	 *            a collection of countees
	 */
	public void addAll(final Collection<? extends K> arg) {
		for (final K k : arg) {
			this.add(k);
		}
	}

	/**
	 * Decreases the value of <i>all</i> elements in arg by 1.
	 *
	 * @param arg
	 *            a collection of countees
	 */
	public void subtractAll(final Collection<? extends K> arg) {
		for (final K k : arg) {
			this.subtract(k);
		}
	}

	/**
	 * Returns a pair with the maximal value and all elements that have it.
	 *
	 * @return a pair
	 */
	public Pair<Integer, Set<K>> getMax() {
		final HashSet<K> set = new HashSet<K>();

		int r = Integer.MIN_VALUE;
		for (final K k : keySet()) {
			final int i = this.get(k);
			if (i > r) {
				set.clear();
				set.add(k);
				r = i;
			} else if (i == r) {
				set.add(k);
			}
		}

		return new Pair<Integer, Set<K>>(r, set);
	}

	/**
	 * Returns a pair with the maximal value and all elements that have it.
	 *
	 * @return a pair
	 */
	public Pair<Integer, Set<K>> getMin() {
		final HashSet<K> set = new HashSet<K>();

		int r = Integer.MAX_VALUE;
		for (final K k : keySet()) {
			final int i = this.get(k);
			if (i < r) {
				set.clear();
				set.add(k);
				r = i;
			} else if (i == r) {
				set.add(k);
			}
		}

		return new Pair<Integer, Set<K>>(r, set);
	}

	/**
	 * Returns the maximal number
	 *
	 */
	public int getHighestCount() {
		// TODO: make faster
		return this.getMax().getFirst();
	}

	/**
	 *
	 * @return a set containing all things with the maximal number
	 */
	public Set<K> getKeysWithMaxCount() {
		return this.getMax().getSecond();
	}

	@Override
	public Integer get(final Object k) {
		if (super.containsKey(k)) {
			return super.get(k);
		}
		return 0;
	}

	/**
	 * A static function that creates a <code>Counter&lt;String&gt;</code>
	 * object from a stream. The function assumes that string and count are
	 * separated by a tab character.
	 *
	 * @param r
	 *            The reader object from which we read.
	 * @return A new Counter object.
	 * @throws IOException
	 *             thrown in case of IO exceptions
	 */
	public static Counter<String> fromString(Reader r) throws IOException {
		final Counter<String> c = new Counter<String>();

		final BufferedReader buf = new BufferedReader(r);
		String l;
		while ((l = buf.readLine()) != null) {
			if (l != null) {
				final String[] line = l.split("\t");
				c.add(line[0], Integer.valueOf(line[1]));
			}
		}
		buf.close();
		return c;
	}
}
