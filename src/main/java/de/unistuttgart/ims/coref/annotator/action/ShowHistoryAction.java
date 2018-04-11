package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class ShowHistoryAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public ShowHistoryAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_SHOW_HISTORY, MaterialDesign.MDI_HISTORY);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog frame = new JDialog(getTarget());
		frame.setModal(true);
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (Op edit : getTarget().getCoreferenceModel().getHistory())
			model.addElement(edit.toString());

		JList<String> list = new JList<String>();
		list.setModel(model);

		frame.add(list, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
