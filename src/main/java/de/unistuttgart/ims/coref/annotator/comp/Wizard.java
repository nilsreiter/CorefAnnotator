package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class Wizard extends JDialog {

	private static final long serialVersionUID = 1L;

	MutableList<JPanel> panelList = Lists.mutable.empty();
	MutableList<ExtendedChangeListener> changeListener = Lists.mutable.empty();

	JLabel header = new JLabel();
	JPanel footer = new JPanel();

	JButton nextButton = new JButton("next");
	JButton prevButton = new JButton("previous");

	int currentPage = 0;
	int nextPageIndex = 0;

	public Wizard() {
		super(null, ModalityType.APPLICATION_MODAL);

		footer.add(prevButton);
		footer.add(nextButton);

		prevButton.setEnabled(currentPage > 0);
		nextButton.setEnabled(currentPage < panelList.size());
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nextPageIndex = currentPage + 1;
				fireBeforeChangeEvent();
				showPage(nextPageIndex);
			}

		});

		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nextPageIndex = currentPage - 1;
				fireBeforeChangeEvent();
				showPage(nextPageIndex);
			}

		});

		add(header, BorderLayout.NORTH);
		add(footer, BorderLayout.SOUTH);

	}

	public void start() {
		showPage(0);
	}

	private void showPage(int page) {

		currentPage = page;

		header.setText("Page " + (page + 1) + " of " + panelList.size());

		prevButton.setEnabled(currentPage > 0);
		nextButton.setEnabled(currentPage < panelList.size() - 1);

		add(getPage(currentPage), BorderLayout.CENTER);

		fireChangeEvent();
	}

	public boolean addPage(JPanel e) {
		return panelList.add(e);
	}

	public boolean removePage(Object o) {
		return panelList.remove(o);
	}

	public JPanel getPage(int index) {
		return panelList.get(index);
	}

	public JPanel getCurrentPage() {
		return panelList.get(currentPage);
	}

	public void addPage(int index, JPanel element) {
		panelList.add(index, element);
	}

	public JPanel removePage(int index) {
		return panelList.remove(index);
	}

	public void addChangeListener​(ExtendedChangeListener l) {
		changeListener.add(l);
	}

	public void removeChangeListener​(ExtendedChangeListener l) {
		changeListener.remove(l);
	}

	protected void fireBeforeChangeEvent() {
		changeListener.forEach(cl -> cl.beforeStateChanged(new ChangeEvent(this)));
	}

	protected void fireChangeEvent() {
		changeListener.forEach(cl -> cl.stateChanged(new ChangeEvent(this)));
	}

	public int getNextPageIndex() {
		return nextPageIndex;
	}

	public int getCurrentPageIndex() {
		return currentPage;
	}
}
