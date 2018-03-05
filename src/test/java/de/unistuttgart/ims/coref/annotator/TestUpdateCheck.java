package de.unistuttgart.ims.coref.annotator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.UpdateCheck.Version;

public class TestUpdateCheck {

	@Test
	public void testVersionParsing() {
		String s = "1.0.0";
		assertEquals(s, Version.get(s).toString());
		s = "1.0.0-beta1";
		assertEquals(s, Version.get(s).toString());
		s = "1.0.0-SNAPSHOT";
		assertEquals(s, Version.get(s).toString());
	}

	@Test
	public void testComparing() {
		assertEquals(0, Version.get("1.0.0").compareTo(Version.get("1.0.0")));
		assertEquals(-1, Version.get("1.0.0").compareTo(Version.get("2.0.0")));
		assertEquals(-1, Version.get("1.0.0").compareTo(Version.get("1.1.0")));
		assertEquals(-1, Version.get("1.0.0").compareTo(Version.get("1.0.1")));
		assertEquals(-1, Version.get("0.1.0").compareTo(Version.get("1.1.0")));

		assertEquals(-1, Version.get("1.0.0-beta1").compareTo(Version.get("1.0.0")));
		assertEquals(-1, Version.get("1.0.0-beta1").compareTo(Version.get("1.0.0-beta2")));
		assertEquals(-1, Version.get("1.0.0-alpha2").compareTo(Version.get("1.0.0-beta1")));
		assertEquals(1, Version.get("1.0.0").compareTo(Version.get("1.0.0-SNAPSHOT")));
	}

	@Test
	public void testCurrent() {
		assertEquals("0.0.1-SNAPSHOT", Version.get().toString());
	}

}
