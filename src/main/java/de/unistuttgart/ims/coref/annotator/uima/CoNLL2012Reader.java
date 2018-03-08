package de.unistuttgart.ims.coref.annotator.uima;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Maps;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CompressionUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class CoNLL2012Reader extends JCasResourceCollectionReader_ImplBase {
	ColorProvider colorProvider = new ColorProvider();

	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		Pattern pattern = Pattern.compile("\\d+");

		Resource res = nextFile();
		initCas(aJCas, res);

		BufferedReader reader = null;
		CSVParser p = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					CompressionUtils.getInputStream(res.getLocation(), res.getInputStream()), "UTF-8"));
			p = new CSVParser(reader, CSVFormat.TDF.withIgnoreEmptyLines(false).withCommentMarker('#').withEscape('\\')
					.withQuoteMode(QuoteMode.NONE).withQuote(null));
			Iterator<CSVRecord> iterator = p.iterator();
			JCasBuilder b = new JCasBuilder(aJCas);

			Map<Integer, Integer> beginMentionMap = Maps.mutable.empty();
			Map<Integer, Entity> entityMap = Maps.mutable.empty();
			int sentenceBegin = 0;
			while (iterator.hasNext()) {
				CSVRecord line = iterator.next();

				if (line.size() == 1) {
					b.add(sentenceBegin, Sentence.class);
					b.add("\n");
					sentenceBegin = b.getPosition();
				} else if (line.size() > 3) {
					String[] crP = line.get(10).split("\\|");
					for (String cr : crP) {
						Matcher m = pattern.matcher(cr);
						if (m.find() && cr.startsWith("(")) {
							int id = Integer.valueOf(m.group());
							beginMentionMap.put(id, b.getPosition());
						}
					}
					b.add(line.get(3), Token.class);
					for (String cr : crP) {
						Matcher m = pattern.matcher(cr);
						if (m.find() && cr.endsWith(")")) {
							int id = Integer.valueOf(m.group());
							Mention mention = b.add(beginMentionMap.get(id), Mention.class);
							Entity e = getOrCreate(aJCas, entityMap, id);
							mention.setEntity(e);
						}
					}
					b.add(" ");
				}
			}
			b.add(sentenceBegin, Sentence.class);
			b.close();
			AnnotationUtil.trim(new HashSet<Sentence>(JCasUtil.select(aJCas, Sentence.class)));
			AnnotationUtil.trim(new HashSet<Token>(JCasUtil.select(aJCas, Token.class)));
		} finally {
			if (reader != null)
				reader.close();
			if (p != null)
				p.close();
		}

	}

	protected Entity getOrCreate(JCas jcas, Map<Integer, Entity> map, int id) {
		if (!map.containsKey(id)) {
			Entity e = new Entity(jcas);
			e.setLabel(String.valueOf(id));
			e.setColor(colorProvider.getNextColor().getRGB());
			e.addToIndexes();
			map.put(id, e);
			return e;
		}
		return map.get(id);
	}

}
