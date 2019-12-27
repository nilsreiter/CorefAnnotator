package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class DuplicateMentions extends TargetedIkonAction<DocumentWindow> implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public DuplicateMentions(DocumentWindow dw) {
		super(dw, Strings.ACTION_DUPLICATE_MENTIONS, MaterialDesign.MDI_CONTENT_DUPLICATE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MutableList<TreePath> paths = Lists.mutable.with(getTarget().getTree().getSelectionPaths());

		MutableList<Mention> mentions = paths.collect(p -> (CATreeNode) p.getLastPathComponent())
				.collect(tn -> (Mention) tn.getFeatureStructure());

		getTarget().getDocumentModel()
				.edit(new de.unistuttgart.ims.coref.annotator.document.op.DuplicateMentions(mentions));
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreeSelectionUtil tsu = new TreeSelectionUtil(e);
		setEnabled(tsu.isMention());
	}

}
