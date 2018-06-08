package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.action.ToggleFlagAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.FlagModelListener;

public class FlagMenu extends JMenu implements FlagModelListener {

	private static final long serialVersionUID = 1L;
	DocumentWindow dw;

	public FlagMenu(String s, DocumentWindow dw) {
		super(s);
		this.dw = dw;
	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		switch (event.getType()) {
		case Add:
			ToggleFlagAction a = new ToggleFlagAction(dw, (FlagModel) event.getSource(), (Flag) event.getArgument1());
			dw.getTreeSelectionListener().addListener(a);
			add(new JCheckBoxMenuItem(a));
			break;
		default:
		}
	}

}
