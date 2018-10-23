package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreePath;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class SearchAnnotationPanel extends SearchPanel<SearchResultMention> implements WindowListener {

	class SearchFlaggedMentions extends IkonAction {
		private static final long serialVersionUID = 1L;

		String flag;

		public SearchFlaggedMentions(String s, String key, Ikon ik) {
			super(key, ik);
			this.flag = s;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clearResults();
			JCas jcas = searchContainer.getDocumentWindow().getDocumentModel().getJcas();
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				if (Util.isX(m, flag)) {
					listModel.addElement(new SearchResultMention(searchContainer, m));
					try {
						highlights.add(hilit.addHighlight(m.getBegin(), m.getEnd(), painter));
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}

				}
			}

			updateLabel();
			searchContainer.pack();

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
				sr = listModel.get(index);
			} catch (ArrayIndexOutOfBoundsException ex) {
				return;
			}
			Mention m = sr.getMention();

			Object[] path = searchContainer.getDocumentWindow().getDocumentModel().getTreeModel().getPathToRoot(m);
			TreePath tp = new TreePath(path);
			searchContainer.getDocumentWindow().getTree().setSelectionPath(tp);
			searchContainer.getDocumentWindow().getTree().scrollPathToVisible(tp);
		}

	}

	private static final long serialVersionUID = 1L;
	JList<SearchResultMention> text_list;
	JLabel selectedEntityLabel = new JLabel();
	int limit = 1000;
	MutableList<JToggleButton> toggleButtons = Lists.mutable.empty();

	public SearchAnnotationPanel(SearchContainer sd) {
		super(sd);

		JPanel bar = new JPanel();
		// bar.setFloatable(false);
		ButtonGroup bg = new ButtonGroup();

		for (Flag flag : sd.getDocumentWindow().getDocumentModel().getFlagModel().getFlags()) {
			if (flag.getTargetClass().equalsIgnoreCase(Mention.class.getName())) {
				AbstractAction action = new SearchFlaggedMentions(flag.getKey(), flag.getLabel(),
						MaterialDesign.valueOf(flag.getIcon()));
				JToggleButton b = new JToggleButton(action);
				bg.add(b);
				bar.add(b);
				toggleButtons.add(b);
			}
		}

		bar.add(new JButton(clearFindings));

		JPanel searchPanel = new JPanel();
		searchPanel.add(bar);

		text_list = new JList<SearchResultMention>(listModel);
		text_list.getSelectionModel().addListSelectionListener(new StructuredSearchResultListSelectionListener());
		text_list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		text_list.setCellRenderer(
				new SearchResultRenderer<SearchResult>(searchContainer.getText(), searchContainer.getContexts()));
		text_list.setVisibleRowCount(10);
		text_list.setDragEnabled(false);

		JScrollPane listScroller = new JScrollPane(text_list);

		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(listScroller, BorderLayout.CENTER);
		add(searchResultsLabel, BorderLayout.SOUTH);
	}

	@Override
	public void clearEvent() {
		toggleButtons.forEach(tb -> tb.getAction().putValue(Action.SELECTED_KEY, Boolean.FALSE));
	}

}