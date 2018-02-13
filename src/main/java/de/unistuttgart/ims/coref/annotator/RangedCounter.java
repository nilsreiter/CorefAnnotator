package de.unistuttgart.ims.coref.annotator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.list.Interval;

public class RangedCounter {

	Map<Integer, DualHashBidiMap<Integer, Object>> map = new HashMap<Integer, DualHashBidiMap<Integer, Object>>();

	int max = 10;

	public void subtract(Span span, Object hi) {
		for (int i = span.begin; i < span.end; i++)
			map.get(i).removeValue(hi);

	}

	public void add(Span span, Object hilight, int level) {
		for (int i = span.begin; i < span.end; i++) {
			if (!map.containsKey(i)) {
				map.put(i, new DualHashBidiMap<Integer, Object>());
			}
			map.get(i).put(level, hilight);
		}
	}

	public int getNextLevel(Span span) {
		return getNextLevel(span, 0, 3);
	}

	public int getNextLevel(Span span, int from, int to) {

		MutableSet<Integer> range = Interval.fromTo(from, to).toSet();
		for (int i = span.begin; i < span.end; i++) {
			for (Integer j : range) {
				if (map.get(i) != null && map.get(i).containsKey(j)) {
					range.remove(j);
				}
			}
		}

		if (range.isEmpty()) {
			return getNextLevel(span, to, to + 5);
		}
		return Collections.min(range);
	}

	public void clear() {
		map.clear();
	}

}
