package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.action.ToggleFlagAction;
import  de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.FlagModelListener;

public class FlagMenu extends JMenu implements FlagModelListener {

	private static final long serialVersionUID = 1L;
	DocumentWindow dw;
	private MutableMap<Flag, JMenuItem> actionMap = Maps.mutable.empty();
	Class<? extends FeatureStructure> targetClass = null;

	public FlagMenu(String s, DocumentWindow dw) {
		super(s);
		this.dw = dw;
	}

	public FlagMenu(String s, DocumentWindow dw, Class<? extends FeatureStructure> tClass) {
		super(s);
		this.dw = dw;
		this.targetClass = tClass;
	}

	public JMenuItem add(Flag f, JMenuItem mi) {
		mi = add(mi);
		actionMap.put(f, mi);
		return mi;

	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		Flag f;
		switch (event.getType()) {
		case Remove:
			f = (Flag) event.getArgument(0);
			if (actionMap.containsKey(f)) {
				remove(actionMap.get(f));
				actionMap.remove(f);
			}
			break;
		case Update:
			f = (Flag) event.getArgument(0);
			if (actionMap.containsKey(f))
				remove(actionMap.get(f));
			//$FALL-THROUGH$
		case Add:
			f = (Flag) event.getArgument(0);
			if (f.getTargetClass().equalsIgnoreCase(targetClass.getName())) {
				ToggleFlagAction a = new ToggleFlagAction(dw, (FlagModel) event.getSource(), f);
				dw.getTree().addTreeSelectionListener(a);
				add(f, new JCheckBoxMenuItem(a));
			}
			break;
		default:
			break;
		}

	}

	public Class<? extends FeatureStructure> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

}
