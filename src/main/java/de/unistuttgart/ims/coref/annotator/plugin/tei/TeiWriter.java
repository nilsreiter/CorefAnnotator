package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.ims.uima.io.xml.GenericXmlWriter;

public class TeiWriter extends JCasFileWriter_ImplBase {

	public static final String PARAM_OUTPUT_FILE = "Output File";

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE)
	String fileName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		GenericXmlWriter gxw = new GenericXmlWriter();

		try (OutputStream os = getOutputStream(jcas)) {
			gxw.write(jcas, os);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	OutputStream getOutputStream(JCas jcas) throws FileNotFoundException {
		return new FileOutputStream(new File(fileName));

	}

}
