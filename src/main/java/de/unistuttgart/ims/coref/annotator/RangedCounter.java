package de.unistuttgart.ims.coref.annotator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.list.Interval;

public class RangedCounter {

	Map<Integer, HashBiMap<Integer, Object>> map = new HashMap<Integer, HashBiMap<Integer, Object>>();

	public synchronized int add(Span span, Object object) {
		int l = getNextLevel(span);
		add(span, object, l);
		return l;
	}

	private void add(Span span, Object hilight, int level) {
		RichIterable<Integer> interval = Interval.fromTo(span.begin, span.end);
		interval.forEach(Procedures.cast(i -> {
			if (!map.containsKey(i)) {
				map.put(i, HashBiMap.newMap());
			}
			map.get(i).put(level, hilight);
		}));
	}

	public void clear() {
		map.clear();
	}

	public int getNextLevel(Span span) {
		return getNextLevel(span, 0, 3);
	}

	private int getNextLevel(Span span, int from, int to) {

		MutableSet<Integer> range = Interval.fromTo(from, to).toSet();
		for (int i = span.begin; i < span.end; i++) {
			range = range.rejectWith((Integer j, Integer k) -> map.get(k) != null && map.get(k).containsKey(j),
					new Integer(i));
		}

		if (range.isEmpty()) {
			return getNextLevel(span, to, to + 5);
		}
		return Collections.min(range);
	}

	public void subtract(Span span, Object hi) {
		RichIterable<Integer> interval = Interval.fromTo(span.begin, span.end);
		interval.forEach(Procedures.cast(i -> map.get(i).inverse().remove(hi)));
	}

}
