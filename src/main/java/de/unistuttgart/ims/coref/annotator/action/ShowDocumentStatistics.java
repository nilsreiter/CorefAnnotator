package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.stats.DocumentStatisticsWindow;

public class ShowDocumentStatistics extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ShowDocumentStatistics(DocumentWindow dw) {
		super(dw, "action.show_statistics", MaterialDesign.MDI_TABLE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SHOW_DOCUMENT_STATISTICS_TOOLTIP));
	}

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		new DocumentStatisticsWindow(getTarget());
	}

}
