package de.unistuttgart.ims.coref.annotator.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class Statistics {
	static MyOptions options;

	public static void main(String[] args) throws UIMAException, FileNotFoundException, SAXException, IOException {
		options = CliFactory.parseArguments(MyOptions.class, args);

		JCas jcas = UimaUtil.readJCas(options.getInput().getAbsolutePath());
		jcas.setDocumentLanguage(options.getLanguage());

		// SimplePipeline.runPipeline(jcas,
		// AnalysisEngineFactory.createEngine(BreakIteratorSegmenter.class));

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			List<Token> poss = JCasUtil.selectCovered(Token.class, m);
			if (poss.size() == 1)
				System.err.println(m.getCoveredText() + " " + poss.get(0).getCoveredText());
		}
	}

	static interface MyOptions extends IOOptions {
		@Option(defaultValue = "de")
		String getLanguage();

	}
}
