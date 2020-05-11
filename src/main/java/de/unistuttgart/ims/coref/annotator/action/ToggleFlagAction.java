package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.Util;
import  de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.op.ToggleGenericFlag;

public class ToggleFlagAction extends TargetedIkonAction<DocumentWindow> implements TreeSelectionListener {

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
		MutableSet<FeatureStructure> targets = Sets.mutable.of(getTarget().getTree().getSelectionPaths())
				.collect(tp -> ((CATreeNode) tp.getLastPathComponent()).getFeatureStructure());
		if (Annotator.app.getPreferences().getBoolean(Constants.CFG_STICKY_FLAGS, Defaults.CFG_STICKY_FLAGS)) {
			targets.addAll(targets.selectInstancesOf(Mention.class).flatCollect(m -> getTarget().getDocumentModel()
					.getCoreferenceModel().getMatchingMentions(m.getBegin(), m.getEnd())));
		}
		getTarget().getDocumentModel().edit(new ToggleGenericFlag(flag.getKey(), targets));
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreeSelectionUtil tsu = new TreeSelectionUtil(e);
		boolean en;
		try {
			en = tsu.isClass(flagModel.getTargetClass(flag));
			setEnabled(en);
			putValue(Action.SELECTED_KEY,
					en && tsu.getFeatureStructures().allSatisfy(fs -> Util.isX(fs, flag.getKey())));
		} catch (ClassNotFoundException ex) {
			Annotator.logger.catching(ex);
			setEnabled(false);
		}

	}

}