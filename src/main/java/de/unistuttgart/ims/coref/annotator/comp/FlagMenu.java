package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.action.ToggleFlagAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.FlagModelListener;

public class FlagMenu extends JMenu implements FlagModelListener {

	private static final long serialVersionUID = 1L;
	DocumentWindow dw;
	private MutableMap<Flag, JMenuItem> actionMap = Maps.mutable.empty();

	public FlagMenu(String s, DocumentWindow dw) {
		super(s);
		this.dw = dw;
	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		Flag f = (Flag) event.getArgument(0);
		switch (event.getType()) {
		case Remove:
			remove(actionMap.get(f));
			actionMap.remove(f);
			break;
		case Update:
			remove(actionMap.get(f));
			//$FALL-THROUGH$
		case Add:
			ToggleFlagAction a = new ToggleFlagAction(dw, (FlagModel) event.getSource(), f);
			dw.getTreeSelectionListener().addListener(a);
			add(f, new JCheckBoxMenuItem(a));
			break;
		default:
		}
	}

	public JMenuItem add(Flag f, JMenuItem mi) {
		mi = add(mi);
		actionMap.put(f, mi);
		return mi;

	}

}
