package de.unistuttgart.ims.coref.annotator.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.SortedSets;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class Statistics {
	static MyOptions options;

	static String[] pronouns = new String[] { "ich", "mir", "mich", "mîn", "mîner", "mîne", "mîniu", "mînen", "mîns",
			"dû", "dir", "dich", "dîn", "dîm", "dîne", "dîner", "dînen", "er", "in", "im", "sîn", "sîniu", "sîne",
			"sînen", "sîns", "sînr", "wir", "uns", "unser", "ir", "iu", "iuch", "iwer", "iwerr", "iweren", "iwerm",
			"iurs", "iun", "sie", "si", "se", "ir", "sich", "ez", "der", "den", "dem", "des", "dez", "diu", "dise",
			"diese", "die", "ietweder", "ieslîcher", "i", "u", "du", "tir", "'r", "m", "n", "s", "z" };

	static MutableSortedSetMultimap<Entity, Mention> entityMentionMap = Multimaps.mutable.sortedSet
			.of(new AnnotationComparator<Mention>());

	public static void main(String[] args) throws UIMAException, FileNotFoundException, SAXException, IOException {
		options = CliFactory.parseArguments(MyOptions.class, args);

		JCas jcas = UimaUtil.readJCas(options.getInput().getAbsolutePath());
		jcas.setDocumentLanguage(options.getLanguage());

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			entityMentionMap.put(m.getEntity(), m);

		}

		PrintStream fw = System.out;
		if (options.getOutput() != null)
			fw = new PrintStream(options.getOutput());

		for (Mention m : JCasUtil.select(jcas, Mention.class))
			if (ArrayUtils.contains(pronouns, m.getCoveredText().toLowerCase())) {
				ImmutableSortedSet<Mention> mentions = entityMentionMap.get(m.getEntity()).toImmutable();
				ImmutableSortedSet<Mention> mentions_rev = SortedSets.immutable
						.ofAll(new AnnotationComparator<Mention>(true, true), mentions);
				Iterator<Mention> fwdIter = mentions.iterator();
				Iterator<Mention> backIter = mentions_rev.iterator();
				Mention nextMention = null, prevMention = null;
				while (fwdIter.hasNext() && nextMention != m)
					nextMention = fwdIter.next();
				while (backIter.hasNext() && prevMention != m)
					prevMention = backIter.next();
				Mention backRef = null, fwdRef = null;
				while (fwdRef == null && fwdIter.hasNext()) {
					nextMention = fwdIter.next();
					if (!ArrayUtils.contains(pronouns, nextMention.getCoveredText().toLowerCase())) {
						fwdRef = nextMention;
					}
				}
				while (backRef == null && backIter.hasNext()) {
					prevMention = backIter.next();
					if (!ArrayUtils.contains(pronouns, prevMention.getCoveredText().toLowerCase())) {
						backRef = prevMention;
					}
				}

				int backDist = (backRef != null ? JCasUtil.selectBetween(Token.class, m, backRef).size()
						: Integer.MAX_VALUE);
				int fwdDist = (fwdRef != null ? JCasUtil.selectBetween(Token.class, m, fwdRef).size()
						: Integer.MAX_VALUE);

				if (true) {
					if (backRef != null)
						System.err.format("%1$d %2$d %3$d %4$s\n", backRef.getBegin(), backRef.getEnd(), backDist,
								backRef.getCoveredText());
					System.err.format("%1$d %2$d %3$s\n", m.getBegin(), m.getEnd(), m.getCoveredText());
					if (fwdRef != null)
						System.err.format("%1$d %2$d %3$d %4$s\n", fwdRef.getBegin(), fwdRef.getEnd(), fwdDist,
								fwdRef.getCoveredText());
					System.err.println("---");
				}
				if (Math.min(backDist, fwdDist) < Integer.MAX_VALUE)
					fw.format("%1$s\t%2$d\n", m.getEntity().getLabel(), Math.min(backDist, fwdDist));
			}
		fw.close();

		/*
		 * R code zur Analyse:
		 * 
		 * library(data.table)
		 * 
		 * d <- read.delim(
		 * "/Users/reiterns/Documents/Projects/CorefAnnotator/code/target/output.tsv",
		 * header = FALSE)
		 * 
		 * dt <- data.table(d)
		 * 
		 * dt[V1 %in% c("Parzival", "Herzeloyde", "Artus", "Ither", "Jeschute",
		 * "Sigune", "Orilus"),.(mean=mean(V2), sd=sd(V2), min=min(V2), max=max(V2),
		 * mad=length(V2[V2>30]), n=.N, med=as.double(median(V2))),.(V1)]
		 * 
		 */

	}

	static interface MyOptions extends IOOptions {
		@Option(defaultValue = "de")
		String getLanguage();

	}
}
