package de.unistuttgart.ims.coref.annotator.uima;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.xml.sax.SAXException;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.api.Meta;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
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
		return StringUtils.join(JCasUtil.toText(mention.getSurface()), Defaults.CFG_MENTIONSURFACE_SEPARATOR);
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
		mention.addToIndexes();
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

	public static int compare(Annotation m1, Annotation m2) {
		int returnValue = Integer.compare(m1.getBegin(), m2.getBegin());
		if (returnValue == 0)
			returnValue = Integer.compare(m2.getEnd(), m1.getEnd());
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

	public static Mention selectMentionByIndex(JCas jcas, int index) {
		Iterator<Mention> iterator = jcas.getIndexedFSs(Mention.class).iterator();
		while (iterator.hasNext() && index-- >= 0) {
			Mention m = iterator.next();
			if (index == 0)
				return m;
		}
		return null;
	}

	public static void addMentionSurface(Mention mention, MentionSurface ms) {
		FSArray<MentionSurface> oldArray = mention.getSurface();
		FSArray<MentionSurface> newArray = new FSArray<MentionSurface>(mention.getJCas(), oldArray.size() + 1);

		Iterator<MentionSurface> oldArrayIterator = oldArray.iterator();
		int i = 0;
		boolean added = false;
		while (oldArrayIterator.hasNext()) {
			MentionSurface surf = oldArrayIterator.next();

			if (!added && UimaUtil.compare(surf, ms) > 0) {
				newArray.set(i++, ms);
				added = true;
			}
			newArray.set(i++, surf);
		}
		if (!added)
			newArray.set(i++, ms);
		oldArray.removeFromIndexes();
		mention.setSurface(newArray);
	}

	public static void removeMentionSurface(Mention mention, MentionSurface ms) {
		FSArray<MentionSurface> oldArray = mention.getSurface();
		FSArray<MentionSurface> newArray = new FSArray<MentionSurface>(mention.getJCas(), oldArray.size() - 1);

		Iterator<MentionSurface> oldArrayIterator = oldArray.iterator();
		int i = 0;
		while (oldArrayIterator.hasNext()) {
			MentionSurface surf = oldArrayIterator.next();

			if (surf == ms)
				continue;
			newArray.set(i++, surf);
		}
		oldArray.removeFromIndexes();
		mention.setSurface(newArray);

	}

	public static boolean contains(StringArray array, String s) {
		if (array == null)
			return false;
		for (int i = 0; i < array.size(); i++)
			if (array.get(i).equals(s))
				return true;
		return false;
	}

	public static void addFlagKey(FeatureStructure fs, String flagKey) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		try {
			StringArray arr = addTo(fs.getCAS().getJCas(), (StringArray) fs.getFeatureValue(feature), flagKey);
			fs.setFeatureValue(feature, arr);
		} catch (CASRuntimeException | CASException e) {
			e.printStackTrace();
		}
	}

	public static StringArray addTo(JCas jcas, StringArray arr, String fs) {
		int i = 0;
		StringArray nArr;
		int oldSize = arr == null ? 0 : arr.size();
		if (arr != null) {
			nArr = new StringArray(jcas, oldSize + 1);
			for (; i < oldSize; i++) {
				nArr.set(i, arr.get(i));
			}
		} else {
			nArr = new StringArray(jcas, 1);
		}
		nArr.set(i, fs);
		if (arr != null)
			arr.removeFromIndexes();
		nArr.addToIndexes();
		return nArr;

	}

	public static <T extends FeatureStructure> FSArray<T> addTo(JCas jcas, FSArray<T> arr, T fs) {
		int i = 0;
		FSArray<T> nArr;
		if (arr != null) {
			nArr = new FSArray<T>(jcas, arr.size() + 1);
			for (; i < arr.size(); i++) {
				nArr.set(i, arr.get(i));
			}
		} else {
			nArr = new FSArray<T>(jcas, 1);
		}
		nArr.set(i, fs);
		arr.removeFromIndexes();
		nArr.addToIndexes();
		return nArr;

	}

	public static void removeFlagKey(FeatureStructure fs, String flagKey) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		try {
			StringArray arr = removeFrom(fs.getCAS().getJCas(), (StringArray) fs.getFeatureValue(feature), flagKey);
			fs.setFeatureValue(feature, arr);
		} catch (CASRuntimeException | CASException e) {
			e.printStackTrace();
		}

	}

	public static StringArray removeFrom(JCas jcas, StringArray arr, String fs) {
		int i = 0, j = 0;
		StringArray nArr = null;
		int oldSize = arr == null ? 0 : arr.size();
		nArr = new StringArray(jcas, oldSize - 1);
		for (; i < oldSize; i++, j++) {
			if (!arr.get(i).equals(fs))
				nArr.set(j, arr.get(i));
			else
				j--;
		}
		return nArr;
	}

	public static <T extends FeatureStructure> FSArray<T> removeFrom(JCas jcas, FSArray<T> arr, T fs) {
		int i = 0, j = 0;
		FSArray<T> nArr = null;
		arr.removeFromIndexes();
		int oldSize = arr == null ? 0 : arr.size();
		nArr = new FSArray<T>(jcas, oldSize - 1);
		for (; i < oldSize; i++, j++) {
			if (!arr.get(i).equals(fs))
				nArr.set(j, arr.get(i));
			else
				j--;
		}
		nArr.addToIndexes();
		return nArr;
	}

	public static boolean isX(FeatureStructure fs, String flag) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		return contains((StringArray) fs.getFeatureValue(feature), flag);
	}

	public static Meta getMeta(JCas jcas) {
		if (!JCasUtil.exists(jcas, Meta.class)) {
			Meta m = new Meta(jcas);
			m.addToIndexes();
			return m;
		}
		try {
			return JCasUtil.selectSingle(jcas, Meta.class);
		} catch (IllegalArgumentException e) {
			Annotator.logger.catching(e);
			return null;
		}
	}

	public static <T extends Annotation> T extend(T annotation) {
		final char[] s = annotation.getCoveredText().toCharArray();
		char[] text = annotation.getCAS().getDocumentText().toCharArray();
		if (s.length == 0)
			return annotation;

		int b = annotation.getBegin(), e = annotation.getEnd();

		if (b > 0) {
			char prev = text[b - 1];
			while (b > 0 && Character.isLetter(prev)) {
				b--;
				// if we have reached the beginning, we pretend the
				// previous character to be a white space.
				if (b == 0)
					prev = ' ';
				else
					prev = text[b - 1];
			}
		}

		if (e < text.length) {
			char next = text[e];
			while (e < text.length && Character.isLetter(next)) {
				e++;
				if (e == text.length)
					next = ' ';
				else
					next = text[e];
			}
		}

		annotation.setBegin(b);
		annotation.setEnd(e);
		return annotation;
	}

	public static <T extends TOP> int count(JCas jcas, Class<T> cl) {
		return JCasUtil.select(jcas, cl).size();
	}

	public static <T extends FeatureStructure> MutableList<T> toList(FSArray<T> arr) {
		MutableList<T> list = Lists.mutable.empty();
		arr.forEach(fs -> list.add(fs));
		return list;
	}

	public static StringArray getFlags(FeatureStructure fs) throws CASException {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return new StringArray(fs.getCAS().getJCas(), 0);
		else {
			StringArray sa = (StringArray) fs.getFeatureValue(feature);
			if (sa == null)
				return new StringArray(fs.getCAS().getJCas(), 0);
			else
				return sa;
		}
	}

	public static String[] getFlagsAsStringArray(FeatureStructure fs) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return new String[0];
		else {
			StringArray sa = (StringArray) fs.getFeatureValue(feature);
			if (sa == null)
				return new String[0];
			else
				return sa.toStringArray();
		}
	}

	public static void setFlags(FeatureStructure fs, StringArray arr) throws CASException {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return;
		else
			fs.setFeatureValue(feature, arr);
	}

	public static Iterable<Mention> selectFollowing(Mention m, int n) {
		ImmutableList<MentionSurface> mss = Lists.immutable
				.withAll(JCasUtil.selectFollowing(MentionSurface.class, UimaUtil.getLast(m), n));
		return mss.collect(ms -> ms.getMention()).toSet().toList();
	}

	public static Iterable<Mention> selectPreceding(Mention m, int n) {
		ImmutableList<MentionSurface> mss = Lists.immutable
				.withAll(JCasUtil.selectPreceding(MentionSurface.class, UimaUtil.getFirst(m), n));
		return mss.collect(ms -> ms.getMention()).toSet().toList();
	}

	public static boolean isGroup(Object o) {
		if (!(o instanceof Entity))
			return false;
		Entity e = (Entity) o;
		if (e.getMembers() == null)
			return false;
		return e.getMembers().size() > 0;
	}

}
