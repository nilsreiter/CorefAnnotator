package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Highlighter;
import javax.swing.tree.TreePath;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class SearchAnnotationPanel extends JPanel implements WindowListener {

	class SearchFlaggedMentions extends IkonAction {
		private static final long serialVersionUID = 1L;

		String flag;

		public SearchFlaggedMentions(String s, String key, Ikon ik) {
			super(key, ik);
			this.flag = s;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			struct_lm.clear();
			searchResultsLabel.setText("");
			int found = 0;
			JCas jcas = searchDialog.getDocumentWindow().getDocumentModel().getJcas();
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				if (Util.isX(m, flag)) {
					struct_lm.addElement(new SearchResultMention(searchDialog, m));
					found++;
				}
			}

			if (found > 0) {
				searchResultsLabel.setText(
						(found > limit ? Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS_MORE_THAN) + " "
								: "") + struct_lm.size() + " "
								+ Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS));

			}
			searchDialog.pack();

		}

	}

	class SearchFlaggedMentionsAmbiguous extends SearchFlaggedMentions {

		private static final long serialVersionUID = 1L;

		public SearchFlaggedMentionsAmbiguous() {
			super(Constants.MENTION_FLAG_AMBIGUOUS, Constants.Strings.ACTION_SEARCH_MENTION_AMBIGUOUS,
					MaterialDesign.MDI_SHARE_VARIANT);
		}

	}

	class SearchFlaggedMentionsDifficult extends SearchFlaggedMentions {

		private static final long serialVersionUID = 1L;

		public SearchFlaggedMentionsDifficult() {
			super(Constants.MENTION_FLAG_DIFFICULT, Constants.Strings.ACTION_SEARCH_MENTION_DIFFICULT,
					MaterialDesign.MDI_ALERT_BOX);
		}

	}

	class SearchFlaggedMentionsNonNominal extends SearchFlaggedMentions {

		private static final long serialVersionUID = 1L;

		public SearchFlaggedMentionsNonNominal() {
			super(Constants.MENTION_FLAG_NON_NOMINAL, Constants.Strings.ACTION_SEARCH_MENTION_NONNOMINAL,
					MaterialDesign.MDI_FLAG);
		}

	}

	class StructuredSearchResultListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting())
				return;

			int index = text_list.getSelectedIndex();
			SearchResultMention sr;
			try {
				sr = struct_lm.get(index);
			} catch (ArrayIndexOutOfBoundsException ex) {
				return;
			}
			Mention m = sr.getMention();

			Object[] path = searchDialog.getDocumentWindow().getDocumentModel().getTreeModel().getPathToRoot(m);
			TreePath tp = new TreePath(path);
			searchDialog.getDocumentWindow().getTree().setSelectionPath(tp);
			searchDialog.getDocumentWindow().getTree().scrollPathToVisible(tp);

			searchDialog.getDocumentWindow().annotationSelected(m);

		}

	}

	private static final long serialVersionUID = 1L;
	JList<SearchResultMention> text_list;
	DefaultListModel<SearchResultMention> struct_lm = new DefaultListModel<SearchResultMention>();
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	Set<Object> highlights = new HashSet<Object>();
	JLabel searchResultsLabel = new JLabel(), selectedEntityLabel = new JLabel();
	int limit = 1000;
	SearchContainer searchDialog;

	public SearchAnnotationPanel(SearchContainer sd) {
		searchDialog = sd;

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(new SearchFlaggedMentionsAmbiguous());
		bar.add(new SearchFlaggedMentionsDifficult());
		bar.add(new SearchFlaggedMentionsNonNominal());

		JPanel searchPanel = new JPanel();
		searchPanel.add(bar);

		text_list = new JList<SearchResultMention>(struct_lm);
		text_list.getSelectionModel().addListSelectionListener(new StructuredSearchResultListSelectionListener());
		text_list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		text_list.setCellRenderer(
				new SearchResultRenderer<SearchResult>(searchDialog.getText(), searchDialog.getContexts()));
		text_list.setVisibleRowCount(10);
		text_list.setDragEnabled(false);

		JScrollPane listScroller = new JScrollPane(text_list);

		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(listScroller, BorderLayout.CENTER);
		add(searchResultsLabel, BorderLayout.SOUTH);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}