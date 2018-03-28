package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.ims.uima.io.xml.GenericXmlWriter;

public class TeiWriter extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		GenericXmlWriter gxw = new GenericXmlWriter();

		try (OutputStream os = getOutputStream(jcas, "xml")) {
			gxw.write(jcas, os);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
