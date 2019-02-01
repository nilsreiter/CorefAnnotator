package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;

public interface DirectFileIOPlugin {
	JCas getJCas(File f) throws IOException, UIMAException;
}
