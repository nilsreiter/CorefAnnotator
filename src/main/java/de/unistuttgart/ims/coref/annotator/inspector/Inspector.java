package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListModel;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.comp.PanelList;

public class Inspector extends JDialog {

	private static final long serialVersionUID = 1L;

	PanelList<Issue, JPanel> issueList;

	public Inspector(DocumentWindow dw) {
		super(dw);

		issueList = new PanelList<Issue, JPanel>(new IssuePanelFactory());

		DefaultListModel<Issue> listModel = new DefaultListModel<Issue>();
		listModel.addListDataListener(issueList);
		Checker checker = new Checker(dw.getDocumentModel().getJcas(), this, listModel);
		checker.execute();

		add(issueList, BorderLayout.CENTER);

		setModalityType(ModalityType.DOCUMENT_MODAL);

	}

	public void setListModel(ListModel<Issue> listModel) {
		listModel.addListDataListener(issueList);

	}

}
