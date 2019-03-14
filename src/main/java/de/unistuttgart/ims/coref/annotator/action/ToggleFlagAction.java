package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CAAbstractTreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.op.ToggleGenericFlag;

public class ToggleFlagAction extends TargetedIkonAction<DocumentWindow> implements CATreeSelectionListener {

	private static final long serialVersionUID = 1L;

	Flag flag;
	FlagModel flagModel;

	public ToggleFlagAction(DocumentWindow dw, FlagModel flagModel, Flag flag) {
		super(dw, flag.getLabel(), false, flagModel.getIkon(flag));
		this.flag = flag;
		this.flagModel = flagModel;
		// putValue(Action.SHORT_DESCRIPTION,
		// Annotator.getString(Strings.ACTION_FLAG_MENTION_NON_NOMINAL_TOOLTIP));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel()
				.edit(new ToggleGenericFlag(flag.getKey(), Lists.immutable.of(getTarget().getTree().getSelectionPaths())
						.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure())));
	}

	@Override
	public void valueChanged(CAAbstractTreeSelectionListener l) {
		boolean en;
		try {
			en = l.isClass(flagModel.getTargetClass(flag));
			setEnabled(en);
			putValue(Action.SELECTED_KEY, en && l.getFeatureStructures().allSatisfy(fs -> Util.isX(fs, flag.getKey())));
		} catch (ClassNotFoundException e) {
			Annotator.logger.catching(e);
		}
	}

}