package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionEvent;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.document.Flag;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ToggleFlagAction extends TargetedIkonAction<DocumentWindow> implements CATreeSelectionListener {

	private static final long serialVersionUID = 1L;

	Flag flag;

	public ToggleFlagAction(DocumentWindow dw, Flag flag) {
		super(dw, flag.getTranslationKey(), flag.getIcon());
		this.flag = flag;
		// putValue(Action.SHORT_DESCRIPTION,
		// Annotator.getString(Strings.ACTION_FLAG_MENTION_NON_NOMINAL_TOOLTIP));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCoreferenceModel()
				.edit(new Op.ToggleGenericFlag(flag.getStringValue(),
						Lists.immutable.of(getTarget().getTree().getSelectionPaths())
								.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

	@Override
	public void valueChanged(CATreeSelectionEvent l) {
		boolean en = l.isClass(flag.getTargetClass());
		setEnabled(en);
		putValue(Action.SELECTED_KEY,
				en && l.getFeatureStructures().allSatisfy(fs -> Util.isX(fs, flag.getStringValue())));
	}

}