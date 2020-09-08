package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.analyzer.AnalyzerWindow;

public class AnalyzeAction extends TargetedIkonAction<AbstractTextWindow> {

	private static final long serialVersionUID = 1L;

	public AnalyzeAction(AbstractTextWindow dw) {
		super(dw, MaterialDesign.MDI_CHART_LINE);
		putValue(Action.NAME, Annotator.getString(Strings.ACTION_ANALYZER));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ANALYZER_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AnalyzerWindow aw = new AnalyzerWindow();
		aw.setDocumentModel(getTarget().getDocumentModel());
	}

}
