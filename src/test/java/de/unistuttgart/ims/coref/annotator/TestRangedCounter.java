package de.unistuttgart.ims.coref.annotator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestRangedCounter {

	RangedCounter counter = new RangedCounter();

	Object[] obj = new Object[] { new Object(), new Object(), new Object(), new Object(), new Object() };

	@Test
	public void testGetNextLevelSpan() {
		int level;
		Span span;
		span = new Span(0, 5);
		level = counter.getNextLevel(span);
		assertEquals(0, level);

		level = counter.add(span, obj[0]);

		level = counter.getNextLevel(span);
		assertEquals(1, level);

		span = new Span(0, 20);
		level = counter.getNextLevel(span);
		assertEquals(1, level);
		counter.add(span, obj[1]);

		span = new Span(10, 12);
		level = counter.getNextLevel(span);
		assertEquals(0, level);
		counter.add(span, obj[2]);

		level = counter.getNextLevel(span);
		assertEquals(2, level);

		counter.subtract(new Span(0, 20), obj[1]);

		level = counter.getNextLevel(span);
		assertEquals(1, level);

	}

}
