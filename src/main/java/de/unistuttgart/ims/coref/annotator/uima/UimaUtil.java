package de.unistuttgart.ims.coref.annotator.uima;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

public class UimaUtil {
	public static JCas readJCas(String filename)
			throws UIMAException, FileNotFoundException, SAXException, IOException {
		if (filename.endsWith(".gz")) {
			try (InputStream is = new GZIPInputStream(new FileInputStream(filename))) {
				return readJCas(is);
			}
		} else if (filename.endsWith(".zip")) {
			try (InputStream is = new ZipArchiveInputStream(new FileInputStream(filename))) {
				return readJCas(is);
			}
		} else {
			try (InputStream is = new FileInputStream(filename)) {
				return readJCas(is);
			}
		}
	}

	public static JCas readJCas(InputStream is) throws UIMAException, SAXException, IOException {
		JCas jcas = JCasFactory.createJCas();
		XmiCasDeserializer.deserialize(is, jcas.getCas(), true);
		return jcas;
	}

	public static int nextCharacter(JCas jcas, int pos, Predicate<Character> pred) {
		char[] txt = jcas.getDocumentText().toCharArray();
		for (int i = pos; i < txt.length; i++) {
			if (pred.test(txt[i]))
				return i;
		}
		return -1;
	}
}
