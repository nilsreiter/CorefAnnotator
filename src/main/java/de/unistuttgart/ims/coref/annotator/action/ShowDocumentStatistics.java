package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.stats.DocumentStatistics;
import de.unistuttgart.ims.coref.annotator.stats.StatisticsPanel;

public class ShowDocumentStatistics extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ShowDocumentStatistics(DocumentWindow dw) {
		super(dw, "action.show_statistics", MaterialDesign.MDI_CHART_BAR);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DocumentStatistics ds = new DocumentStatistics();
		ds.setDocumentModel(getTarget().getDocumentModel());
		getTarget().getDocumentModel().getCoreferenceModel().addCoreferenceModelListener(ds);
		StatisticsPanel panel = new StatisticsPanel();
		panel.setDocumentStatistics(ds);
		JFrame window = new JFrame();
		window.add(panel, BorderLayout.CENTER);
		window.pack();
		window.setVisible(true);
	}

}
