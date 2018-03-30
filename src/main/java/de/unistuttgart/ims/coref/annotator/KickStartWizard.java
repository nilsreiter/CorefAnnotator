package de.unistuttgart.ims.coref.annotator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.unistuttgart.ims.coref.annotator.comp.ExtendedChangeListener;
import de.unistuttgart.ims.coref.annotator.comp.Wizard;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class KickStartWizard implements ExtendedChangeListener {

	Wizard wizard;
	CoreferenceModel coreferenceModel;

	AnalysisEngineDescription nerEngine = null;

	JComboBox<String> language_input;
	JTable ner_input;
	DefaultTableModel tableModel;

	public KickStartWizard(DocumentWindow dw) {
		coreferenceModel = dw.getCoreferenceModel();

		wizard = new Wizard();
		wizard.addChangeListener​(this);

		wizard.addPage(getSettingsPanel());
		wizard.addPage(getNamedEntitiesPanel());
		wizard.start();
		wizard.setVisible(true);
		wizard.pack();

		try {
			nerEngine = AnalysisEngineFactory.createEngineDescription(StanfordNamedEntityRecognizer.class);
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
	}

	protected JPanel getNamedEntitiesPanel() {
		if (nerEngine == null)
			return null;
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel();
		panel.setLayout(layout);

		tableModel = new DefaultTableModel();

		ner_input = new JTable();
		ner_input.setModel(tableModel);
		panel.add(ner_input);

		return panel;

	}

	protected JPanel getSettingsPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel();
		panel.setLayout(layout);

		JLabel language_label = new JLabel("wizard.document_language");
		language_input = new JComboBox<String>(Util.getSupportedLanguageNames());
		panel.add(language_label);
		panel.add(language_input);

		layout.putConstraint(SpringLayout.NORTH, language_label, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, language_input, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, language_label, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, panel, 10, SpringLayout.EAST, language_input);
		return panel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	@Override
	public void beforeStateChanged(ChangeEvent e) {
		Wizard src = (Wizard) e.getSource();
		if (src.getCurrentPageIndex() == 0) {
			coreferenceModel.getJCas().setDocumentLanguage(Util.getLanguage((String) language_input.getSelectedItem()));
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (nerEngine != null) {
						try {
							SimplePipeline.runPipeline(coreferenceModel.getJCas(), nerEngine);
						} catch (AnalysisEngineProcessException | ResourceInitializationException e) {
							e.printStackTrace();
						}
						MutableMultimap<String, Span> ners = Multimaps.mutable.set.empty();

						for (NamedEntity per : JCasUtil.select(coreferenceModel.getJCas(), NamedEntity.class)) {
							ners.put(per.getCoveredText(), new Span(per));
						}

						for (String entityName : ners.keySet()) {
							tableModel.addRow(new String[] { entityName });
						}
					}
				}

			});
		}
	}

}
