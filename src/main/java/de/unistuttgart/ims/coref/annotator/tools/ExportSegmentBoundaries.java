package de.unistuttgart.ims.coref.annotator.tools;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class ExportSegmentBoundaries {
	static Options options;

	public static void main(String[] args)
			throws InvocationTargetException, InterruptedException, IOException, UIMAException, SAXException {
		options = CliFactory.parseArguments(Options.class, args);

		InputStream is = null;
		if (options.getInput().getName().endsWith(".xmi")) {
			is = new FileInputStream(options.getInput());
		} else if (options.getInput().getName().endsWith(".xmi.gz")) {
			is = new GZIPInputStream(new FileInputStream(options.getInput()));
		}

		JCas jcas = JCasFactory.createJCas();
		XmiCasDeserializer.deserialize(is, jcas.getCas());

		is.close();

		FileWriter fw = new FileWriter(options.getOutput());
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			if (options.getBoundary() == "end") {
				fw.write(String.valueOf(m.getEnd()));
			} else {
				fw.write(String.valueOf(m.getBegin()));
			}
			fw.write("\n");
		}
		fw.close();
	}

	public static interface Options extends InputOutputOptions {
		@Option(defaultValue = "begin")
		String getBoundary();
	}

}
