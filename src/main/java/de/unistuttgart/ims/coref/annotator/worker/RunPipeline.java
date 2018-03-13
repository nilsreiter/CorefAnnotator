package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

public class RunPipeline extends SwingWorker<JCas, Object> {

	JCas jcas;
	AnalysisEngineDescription engineDescription;
	Callback callback;

	public RunPipeline(JCas jcas, AnalysisEngineDescription engineDescription, Callback callback) {
		this.jcas = jcas;
		this.engineDescription = engineDescription;
		this.callback = callback;
	}

	@Override
	protected JCas doInBackground() throws Exception {
		SimplePipeline.runPipeline(jcas, engineDescription);
		return jcas;
	}

	@Override
	protected void done() {
		if (callback != null)
			try {
				callback.done(get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
	}

	public static interface Callback {
		void done(JCas jcas);
	}
}
