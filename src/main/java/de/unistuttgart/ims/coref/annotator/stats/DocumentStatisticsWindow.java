package de.unistuttgart.ims.coref.annotator.stats;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;

public class DocumentStatisticsWindow extends JDialog implements WindowListener {

	private static final long serialVersionUID = 1L;

	public DocumentStatisticsWindow(AbstractTextWindow dw) {
		super(dw);
		this.setResizable(false);
		this.setModalityType(ModalityType.MODELESS);
		DocumentStatistics ds = new DocumentStatistics();
		ds.setDocumentModel(dw.getDocumentModel());
		dw.getDocumentModel().getCoreferenceModel().addCoreferenceModelListener(ds);
		StatisticsPanel panel = new StatisticsPanel();
		panel.setDocumentStatistics(ds);

		dw.addWindowListener(this);

		add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
