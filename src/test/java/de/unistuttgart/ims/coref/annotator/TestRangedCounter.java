package de.unistuttgart.ims.coref.annotator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestRangedCounter {

	RangedCounter rangedCounter = new RangedCounter();

	@Test
	public void testBehaviour() {
		Span[] spans = new Span[] { new Span(0, 1), new Span(0, 2), new Span(5, 10) };

		int level;
		level = rangedCounter.getNextLevel(spans[0]);
		assertEquals(0, level);
		level = rangedCounter.getNextLevel(spans[1]);
		assertEquals(0, level);
		level = rangedCounter.getNextLevel(spans[2]);
		assertEquals(0, level);

		level = rangedCounter.getNextLevel(spans[0]);
		rangedCounter.add(spans[0], new Object(), level);

		assertEquals(1, rangedCounter.getNextLevel(spans[1]));

		assertEquals(0, rangedCounter.getNextLevel(spans[2]));

		level = rangedCounter.getNextLevel(spans[0]);
		assertEquals(1, level);

	}
}
