package de.unistuttgart.ims.coref.annotator.uima;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.xml.sax.SAXException;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.api.v2.Segment;

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

	public static String toString(AnnotationTreeNode<Segment> tn, String sep, int maxlength) {
		if (tn == null)
			return null;
		StringBuilder b = new StringBuilder();
		while (tn != null) {
			String s = tn.get().getLabel();
			s = StringUtils.abbreviate(s, maxlength);
			if (s != null && !s.isBlank()) {
				b.insert(0, s);
				b.insert(0, sep);
			}
			tn = tn.getParent();
		}
		return b.toString();
	}

	public static StringArray clone(StringArray arr) throws CASException {
		StringArray newArray = new StringArray(arr.getCAS().getJCas(), arr.size());
		for (int i = 0; i < newArray.size(); i++)
			newArray.set(i, arr.get(i));
		return newArray;
	}

	public static String getCoveredText(Mention mention) {
		return mention.getSurface(0).getCoveredText();
	}

	public static int getBegin(Mention mention) {
		return mention.getSurface(0).getBegin();
	}

	public static int getEnd(Mention mention) {
		return mention.getSurface(mention.getSurface().size() - 1).getEnd();
	}

	public static Mention getMention(JCas jcas, int begin, int end) {
		MentionSurface sf = AnnotationFactory.createAnnotation(jcas, begin, end, MentionSurface.class);
		Mention mention = new Mention(jcas);
		sf.setMention(mention);
		mention.setSurface(new FSArray<MentionSurface>(jcas, 1));
		mention.setSurface(0, sf);
		return mention;
	}

	public static int compare(Mention m1, Mention m2) {
		int returnValue = Integer.compare(UimaUtil.getBegin(m1), UimaUtil.getBegin(m2));
		if (returnValue == 0)
			returnValue = Integer.compare(UimaUtil.getEnd(m2), UimaUtil.getEnd(m1));
		if (returnValue == 0)
			returnValue = Integer.compare(m1.hashCode(), m2.hashCode());
		return returnValue;
	}

	public static MentionSurface getFirst(Mention m) {
		return m.getSurface(0);
	}

	public static MentionSurface getLast(Mention m) {
		return m.getSurface(m.getSurface().size() - 1);
	}

}
