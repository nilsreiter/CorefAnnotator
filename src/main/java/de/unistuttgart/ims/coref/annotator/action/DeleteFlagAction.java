package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.document.op.DeleteFlag;

public class DeleteFlagAction extends TargetedOperationIkonAction<DocumentWindow> implements ListSelectionListener {

	private static final long serialVersionUID = 1L;

	JTable table;

	public DeleteFlagAction(DocumentWindow dw, JTable table) {
		super(dw, Strings.ACTION_DELETE_FLAG, MaterialDesign.MDI_FLAG);
		this.table = table;
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_FLAG_TOOLTIP));
		this.operationClass = DeleteFlag.class;
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Flag f = getSelectedFlag();
		if (f != null)
			getTarget().getDocumentModel().edit(new DeleteFlag(f));
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	private Flag getSelectedFlag() {
		int row = table.getSelectedRow();
		table.clearSelection();
		if (row != -1) {
			String key = (String) table.getModel().getValueAt(row, 1);
			Flag f = getTarget().getDocumentModel().getFlagModel().getFlag(key);
			return f;
		} else
			return null;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Flag f = getSelectedFlag();

		if (f == null) {
			this.setEnabled(false);
			return;
		}
		// TODO: Check wether the flag is used
		this.setEnabled(true);
	}

}
