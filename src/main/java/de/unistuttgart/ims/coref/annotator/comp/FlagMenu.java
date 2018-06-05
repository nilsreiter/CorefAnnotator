package de.unistuttgart.ims.coref.annotator.comp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.action.ToggleFlagAction;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.Flag;

public class FlagMenu extends JMenu implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	DocumentWindow dw;

	public FlagMenu(String s, DocumentWindow dw) {
		super(s);
		this.dw = dw;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(CoreferenceModel.PROPERTY_EVENT_FLAG_ADDED)) {
			Flag f = (Flag) evt.getNewValue();
			ToggleFlagAction a = new ToggleFlagAction(dw, f);
			dw.getTreeSelectionListener().addListener(a);
			add(new JCheckBoxMenuItem(a));
		}
	}

}
