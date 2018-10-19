package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.TreePath;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;

public class SearchDialog extends JDialog implements DocumentListener, WindowListener {
	class AnnotateSelectedFindings extends IkonAction {

		private static final long serialVersionUID = 1L;

		public AnnotateSelectedFindings() {
			super(Constants.Strings.ACTION_ADD_FINDINGS_TO_ENTITY, MaterialDesign.MDI_ARROW_RIGHT);
			putValue(Action.SHORT_DESCRIPTION,
					Annotator.getString(Constants.Strings.ACTION_ADD_FINDINGS_TO_ENTITY_TOOLTIP));
			this.addIkon(MaterialDesign.MDI_ACCOUNT);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Annotator.logger.debug("Adding search results to entity");
			CATreeNode node = (CATreeNode) documentWindow.tree.getSelectionPath().getLastPathComponent();

			AddMentionsToEntity op = new AddMentionsToEntity(node.getEntity(),
					Lists.immutable.withAll(text_list.getSelectedValuesList()).collect(r -> r.getSpan()));
			documentWindow.getDocumentModel().edit(op);
		}
	}

	class AnnotateSelectedFindingsAsNewEntity extends IkonAction {
		private static final long serialVersionUID = 1L;

		public AnnotateSelectedFindingsAsNewEntity() {
			super(Constants.Strings.ACTION_ADD_FINDINGS_TO_NEW_ENTITY, MaterialDesign.MDI_ACCOUNT_PLUS);
			putValue(Action.SHORT_DESCRIPTION,
					Annotator.getString(Constants.Strings.ACTION_ADD_FINDINGS_TO_NEW_ENTITY_TOOLTIP));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Annotator.logger.debug("Adding search results to new entity");
			AddMentionsToNewEntity op = new AddMentionsToNewEntity(
					Lists.immutable.withAll(text_list.getSelectedValuesList()).collect(r -> r.getSpan()));
			documentWindow.getDocumentModel().edit(op);
		}
	}

	class ListTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public Transferable createTransferable(JComponent comp) {
			@SuppressWarnings("unchecked")
			JList<SearchResult> list = (JList<SearchResult>) comp;

			return new PotentialAnnotationTransfer(documentWindow.textPane,
					Lists.immutable.ofAll(list.getSelectedValuesList()).collect(sr -> sr.getSpan()));
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.LINK;
		}
	}

	class MentionSearchResult extends SearchResult {

		Mention mention;

		public MentionSearchResult(Mention m) {
			super(m.getBegin(), m.getEnd());
			this.mention = m;
		}

		@Override
		public int getBegin() {
			return span.begin;
		}

		@Override
		public int getEnd() {
			return span.end;
		}

		public Mention getMention() {
			return mention;
		}

		@Override
		public Span getSpan() {
			return span;
		}

		@Override
		public String toString() {
			return text.substring(Integer.max(span.begin - contexts, 0),
					Integer.min(span.end + contexts, text.length() - 1));
		}
	}

	class RunSearch extends IkonAction {

		private static final long serialVersionUID = 1L;

		public RunSearch() {
			super(Constants.Strings.ACTION_SEARCH, MaterialDesign.MDI_FILE_FIND);
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			search(textField.getText());
		}

	}

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
			int found = 0;
			JCas jcas = documentWindow.getDocumentModel().getJcas();
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				if (Util.isX(m, flag)) {
					struct_lm.addElement(new MentionSearchResult(m));
					found++;
				}
			}

			if (found > 0) {
				searchResultsLabel.setText(
						(found > limit ? Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS_MORE_THAN) + " "
								: "") + struct_lm.size() + " "
								+ Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS));

			}
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

	class SearchResult {
		Span span;

		public SearchResult(int begin, int end) {
			super();
			this.span = new Span(begin, end);
		}

		public int getBegin() {
			return span.begin;
		}

		public int getEnd() {
			return span.end;
		}

		public Span getSpan() {
			return span;
		}

		@Override
		public String toString() {
			return text.substring(Integer.max(span.begin - contexts, 0),
					Integer.min(span.end + contexts, text.length() - 1));
		}
	}

	class SearchResultRenderer implements ListCellRenderer<SearchResult> {

		Font contextFont;
		Font centerFont;

		public SearchResultRenderer() {
			contextFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
			centerFont = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends SearchResult> list, SearchResult value, int index,
				boolean isSelected, boolean cellHasFocus) {

			JPanel panel = new JPanel();
			if (isSelected) {
				panel.setBackground(list.getSelectionBackground());
				panel.setForeground(list.getSelectionForeground());
			} else {
				panel.setBackground(list.getBackground());
				panel.setForeground(list.getForeground());
			}
			JLabel left = new JLabel(
					text.substring(Integer.max(value.getSpan().begin - contexts, 0), value.getSpan().begin));
			JLabel right = new JLabel(text.substring(value.getSpan().end,
					Integer.min(value.getSpan().end + contexts, text.length() - 1)));
			left.setFont(contextFont);
			right.setFont(contextFont);

			JLabel center = new JLabel(text.substring(value.getSpan().begin, value.getSpan().end));
			center.setFont(centerFont);
			if (value instanceof MentionSearchResult) {
				Mention m = ((MentionSearchResult) value).getMention();
				center.setForeground(new Color(m.getEntity().getColor()));
			}
			panel.add(left);
			panel.add(center);
			panel.add(right);

			return panel;
		}

	}

	class StructuredSearchResultListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				int index = e.getLastIndex();
				MentionSearchResult sr = struct_lm.get(index);
				Mention m = sr.getMention();

				Object[] path = documentWindow.getDocumentModel().getTreeModel().getPathToRoot(m);
				TreePath tp = new TreePath(path);
				documentWindow.getTree().setSelectionPath(tp);
				documentWindow.getTree().scrollPathToVisible(tp);

				documentWindow.annotationSelected(m);
			}
		}

	}

	class TSL extends CATreeSelectionListener implements ListSelectionListener {

		boolean treeCondition = false;

		boolean listCondition = false;

		public TSL(JTree tree) {
			super(tree);
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (text_list.getSelectedIndices().length == 1) {
					SearchResult result = text_lm
							.getElementAt(((ListSelectionModel) e.getSource()).getMinSelectionIndex());
					documentWindow.textPane.setCaretPosition(result.getEnd());
				}
				listCondition = (text_list.getSelectedValuesList().size() > 0);
				annotateSelectedFindings.setEnabled(treeCondition && listCondition);
				annotateSelectedFindingsAsNew.setEnabled(text_list.getSelectedValuesList().size() > 0);
				Annotator.logger.debug("Setting listCondition to {}", listCondition);
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			collectData(e);
			treeCondition = (isSingle() && isEntity());
			Annotator.logger.debug("Setting treeCondition to {}", treeCondition);
			annotateSelectedFindings.setEnabled(treeCondition && listCondition);
			if (treeCondition)
				selectedEntityLabel.setText(Annotator.getString(Constants.Strings.STATUS_SEARCH_SELECTED_ENTITY) + ": "
						+ ((Entity) featureStructures.get(0)).getLabel());
			else
				selectedEntityLabel.setText("");
		}

	}

	final static Color HILIT_COLOR = Color.black;
	private static final long serialVersionUID = 1L;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	DocumentWindow documentWindow;
	String text;
	DefaultListModel<SearchResult> text_lm = new DefaultListModel<SearchResult>();
	DefaultListModel<MentionSearchResult> struct_lm = new DefaultListModel<MentionSearchResult>();
	JList<SearchResult> text_list;
	JTextField textField;

	JLabel searchResultsLabel = new JLabel(), selectedEntityLabel = new JLabel();

	int contexts = Defaults.CFG_SEARCH_RESULTS_CONTEXT;

	Set<Object> highlights = new HashSet<Object>();

	TSL tsl = null;

	int limit = 1000;

	AbstractAction annotateSelectedFindings = new AnnotateSelectedFindings(), runSearch = new RunSearch(),
			annotateSelectedFindingsAsNew = new AnnotateSelectedFindingsAsNewEntity();
	JList<MentionSearchResult> struct_list;

	public SearchDialog(DocumentWindow xdw, Preferences configuration) {
		documentWindow = xdw;
		text = xdw.textPane.getText();
		contexts = configuration.getInt(Constants.CFG_SEARCH_RESULTS_CONTEXT, Defaults.CFG_SEARCH_RESULTS_CONTEXT);
		annotateSelectedFindings.setEnabled(false);
		tsl = new TSL(documentWindow.tree);
		documentWindow.tree.addTreeSelectionListener(tsl);

		this.initialiseWindow();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		runSearch.setEnabled(textField.getText().length() > 0);
		try {
			Pattern.compile(textField.getText());
			if (textField.getText().length() > 2)
				search(textField.getText());
		} catch (PatternSyntaxException ex) {
			searchResultsLabel.setText(ex.getLocalizedMessage());
			// silently catching
		}
	}

	protected JPanel initialiseStructuredSearchPanel() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(new JLabel(Annotator.getString(Constants.Strings.ACTION_SEARCH_MENTION)));
		bar.add(new SearchFlaggedMentionsAmbiguous());
		bar.add(new SearchFlaggedMentionsDifficult());
		bar.add(new SearchFlaggedMentionsNonNominal());

		JPanel searchPanel = new JPanel();
		searchPanel.add(bar);

		struct_list = new JList<MentionSearchResult>(struct_lm);
		struct_list.getSelectionModel().addListSelectionListener(new StructuredSearchResultListSelectionListener());
		struct_list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		struct_list.setCellRenderer(new SearchResultRenderer());
		struct_list.setVisibleRowCount(10);
		struct_list.setDragEnabled(false);

		JScrollPane listScroller = new JScrollPane(struct_list);

		JPanel textSearchPanel = new JPanel();
		textSearchPanel.setLayout(new BorderLayout());
		textSearchPanel.add(searchPanel, BorderLayout.NORTH);
		textSearchPanel.add(listScroller, BorderLayout.CENTER);
		return textSearchPanel;
	}

	protected JPanel initialiseTextSearchPanel() {
		textField = new JTextField(20);
		textField.setToolTipText(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TEXT_TOOLTIP));
		textField.getDocument().addDocumentListener(this);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(runSearch);
		bar.add(annotateSelectedFindings);
		bar.add(annotateSelectedFindingsAsNew);

		JPanel searchPanel = new JPanel();
		searchPanel.add(textField);
		searchPanel.add(bar);

		text_list = new JList<SearchResult>(text_lm);
		text_list.getSelectionModel().addListSelectionListener(tsl);
		text_list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		text_list.setCellRenderer(new SearchResultRenderer());
		text_list.setVisibleRowCount(10);
		text_list.setTransferHandler(new ListTransferHandler());
		text_list.setDragEnabled(true);

		JScrollPane listScroller = new JScrollPane(text_list);

		JPanel textSearchPanel = new JPanel();
		textSearchPanel.setLayout(new BorderLayout());
		textSearchPanel.add(searchPanel, BorderLayout.NORTH);
		textSearchPanel.add(listScroller, BorderLayout.CENTER);
		return textSearchPanel;
	}

	protected void initialiseWindow() {

		hilit = documentWindow.textPane.getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_TEXT), initialiseTextSearchPanel());
		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_STRUCTURE),
				initialiseStructuredSearchPanel());

		JPanel statusbar = new JPanel();
		statusbar.add(searchResultsLabel);
		statusbar.add(selectedEntityLabel);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(statusbar, BorderLayout.SOUTH);
		setLocation(documentWindow.getLocation().x + documentWindow.getWidth(), documentWindow.getLocation().y);

		setTitle(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TITLE));
		setMaximumSize(new Dimension(600, 800));
		setLocationRelativeTo(documentWindow);
		addWindowListener(this);
		pack();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		runSearch.setEnabled(textField.getText().length() > 0);
		try {
			Pattern.compile(textField.getText());
			if (textField.getText().length() > 2)
				search(textField.getText());
		} catch (PatternSyntaxException ex) {
			searchResultsLabel.setText(ex.getLocalizedMessage());
			// silently catching
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		runSearch.setEnabled(textField.getText().length() > 0);
		try {
			Pattern.compile(textField.getText());
			if (textField.getText().length() > 2)
				search(textField.getText());
		} catch (PatternSyntaxException ex) {
			searchResultsLabel.setText(ex.getLocalizedMessage());
			// silently catching
		}
	}

	public synchronized void search(String s) {
		text_list.getSelectionModel().removeListSelectionListener(tsl);
		text_list.clearSelection();
		annotateSelectedFindings.setEnabled(false);
		annotateSelectedFindingsAsNew.setEnabled(false);
		searchResultsLabel.setText("");
		text_lm.clear();
		for (Object o : highlights) {
			hilit.removeHighlight(o);
		}
		highlights.clear();
		if (s.length() > 0) {

			Pattern p = Pattern.compile(s);
			Matcher m = p.matcher(text);
			int finding = 0;
			while (m.find() && finding < limit) {
				try {
					text_lm.addElement(new SearchResult(m.start(), m.end()));
					highlights.add(hilit.addHighlight(m.start(), m.end(), painter));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				finding++;
			}
			text_list.getSelectionModel().addListSelectionListener(tsl);
			tsl.listCondition = false;

			searchResultsLabel.setText(
					(m.find() ? Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS_MORE_THAN) + " " : "")
							+ text_lm.size() + " " + Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS));

		}

		pack();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		for (Object o : highlights)
			hilit.removeHighlight(o);
		dispose();

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}
}
