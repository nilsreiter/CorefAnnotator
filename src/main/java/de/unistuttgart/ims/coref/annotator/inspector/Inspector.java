package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class Inspector extends JDialog {

	private static final long serialVersionUID = 1L;

	JList<Issue> issueList;

	public Inspector(DocumentWindow dw) {
		super(dw);

		DefaultListModel<Issue> listModel = new DefaultListModel<Issue>();
		issueList = new JList<Issue>(listModel);
		issueList.setCellRenderer(new IssuePanelFactory());
		getContentPane().add(new JToolBar(), BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(issueList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		setModalityType(ModalityType.MODELESS);

		Checker checker = new Checker(dw.getDocumentModel(), this, listModel);
		checker.execute();

		issueList.setVisible(true);
		setVisible(true);

	}

	public void setListModel(ListModel<Issue> listModel) {
		issueList.setModel(listModel);
		// if (listModel.getSize() > 0)
		// issueList.intervalAdded(
		// new ListDataEvent(listModel, ListDataEvent.INTERVAL_ADDED, 0,
		// listModel.getSize() - 1));
		setVisible(true);
		pack();
	}

}
