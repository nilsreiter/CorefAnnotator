package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.DeleteFlag;

public class DeleteFlagAction extends TargetedIkonAction<DocumentModel> implements ListSelectionListener {

	private static final long serialVersionUID = 1L;

	JTable table;

	public DeleteFlagAction(DocumentModel dw, JTable table) {
		super(dw, Constants.Strings.ACTION_DELETE_FLAG, MaterialDesign.MDI_FLAG);
		this.table = table;
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_FLAG_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		table.clearSelection();
		if (row != -1) {
			String key = (String) table.getModel().getValueAt(row, 1);
			Flag f = getTarget().getFlagModel().getFlag(key);
			getTarget().edit(new DeleteFlag(f));
		}
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			int row = table.getSelectedRow();
			try {
				String key = (String) table.getModel().getValueAt(row, 1);
				setEnabled(!(key.equals(Constants.ENTITY_FLAG_GENERIC) || key.equals(Constants.ENTITY_FLAG_HIDDEN)
						|| key.equals(Constants.MENTION_FLAG_AMBIGUOUS) || key.equals(Constants.MENTION_FLAG_DIFFICULT)
						|| key.equals(Constants.MENTION_FLAG_NON_NOMINAL)));
			} catch (IndexOutOfBoundsException ex) {
				Annotator.logger.catching(ex);
			}

		}

	}

}
