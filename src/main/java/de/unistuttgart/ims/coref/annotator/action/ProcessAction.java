package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.document.DocumentState;
import de.unistuttgart.ims.coref.annotator.document.DocumentStateListener;
import de.unistuttgart.ims.coref.annotator.document.Op;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;

public class ProcessAction extends DocumentWindowAction implements DocumentStateListener {

	private static final long serialVersionUID = 1L;

	ProcessingPlugin plugin;

	public ProcessAction(DocumentWindow dw, ProcessingPlugin plugin) {
		super(dw, plugin.getName(), false, MaterialDesign.MDI_AUTO_FIX);
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					getTarget().setIndeterminateProgress();
					JCas jcas = getTarget().getJCas();

					SimplePipeline.runPipeline(jcas, plugin.getEngineDescription());

					MutableMultimap<String, Span> map = Multimaps.mutable.set.empty();
					for (NamedEntity ne : JCasUtil.select(jcas, NamedEntity.class)) {
						map.put(ne.getCoveredText(), new Span(ne));
					}

					for (String surface : map.keySet()) {
						Op op = new Op.AddMentionsToNewEntity(map.get(surface));
						getTarget().getDocumentModel().getCoreferenceModel().edit(op);
					}

					getTarget().stopIndeterminateProgress();
				} catch (AnalysisEngineProcessException | ResourceInitializationException e) {
					Annotator.logger.catching(e);
				} finally {
					getTarget().stopIndeterminateProgress();
				}

			}

		};

		new Thread(r).start();
	}

	@Override
	public void documentStateEvent(DocumentState state) {
		setEnabled(ArrayUtils.contains(plugin.getSupportedLanguages(), state.getLanguage()));
	}

}
