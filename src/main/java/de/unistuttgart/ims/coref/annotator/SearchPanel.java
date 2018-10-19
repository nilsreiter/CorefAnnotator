package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;

public abstract class SearchPanel<T> extends JPanel implements WindowListener {

	class ClearFindings extends IkonAction {

		private static final long serialVersionUID = 1L;

		public ClearFindings() {
			super(Constants.Strings.ACTION_CLEAR, MaterialDesign.MDI_BOOKMARK_REMOVE);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_CLEAR_TOOLTIP));
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clearResults();
		}

	}

	private static final long serialVersionUID = 1L;

	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	SearchContainer searchContainer;
	Set<Object> highlights = new HashSet<Object>();
	DefaultListModel<T> listModel = new DefaultListModel<T>();
	JLabel searchResultsLabel = new JLabel();

	Action clearFindings = new ClearFindings();

	public SearchPanel(SearchContainer searchContainer) {
		this.searchContainer = searchContainer;

		hilit = searchContainer.getDocumentWindow().getTextPane().getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

	}

	public void clearResults() {
		listModel.clear();
		for (Object o : highlights) {
			hilit.removeHighlight(o);
		}
		highlights.clear();
		updateLabel();
	}

	protected void updateLabel() {
		clearFindings.setEnabled(listModel.size() > 0);
		searchResultsLabel
				.setText(listModel.size() + " " + Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS));
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		clearResults();
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
