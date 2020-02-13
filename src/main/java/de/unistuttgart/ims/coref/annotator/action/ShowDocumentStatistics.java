package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.stats.DocumentStatisticsWindow;

public class ShowDocumentStatistics extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ShowDocumentStatistics(DocumentWindow dw) {
		super(dw, "action.show_statistics", MaterialDesign.MDI_CHART_BAR);
	}

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		new DocumentStatisticsWindow(getTarget());
	}

}
