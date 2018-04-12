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

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class SearchDialog extends JDialog implements DocumentListener, WindowListener {
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

			Op.AddMentionsToEntity op = new Op.AddMentionsToEntity(node.getEntity(),
					Lists.immutable.withAll(list.getSelectedValuesList()).collect(r -> r.getSpan()));
			documentWindow.getCoreferenceModel().edit(op);
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
			Op.AddMentionsToNewEntity op = new Op.AddMentionsToNewEntity(
					Lists.immutable.withAll(list.getSelectedValuesList()).collect(r -> r.getSpan()));
			documentWindow.getCoreferenceModel().edit(op);
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

	final static Color HILIT_COLOR = Color.black;

	private static final long serialVersionUID = 1L;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	DocumentWindow documentWindow;
	String text;
	DefaultListModel<SearchResult> lm = new DefaultListModel<SearchResult>();
	JList<SearchResult> list;
	JTextField textField;
	JLabel searchResultsLabel = new JLabel(), selectedEntityLabel = new JLabel();
	int contexts = Defaults.CFG_SEARCH_RESULTS_CONTEXT;
	Set<Object> highlights = new HashSet<Object>();
	TSL tsl = null;
	int limit = 1000;

	AbstractAction annotateSelectedFindings = new AnnotateSelectedFindings(), runSearch = new RunSearch(),
			annotateSelectedFindingsAsNew = new AnnotateSelectedFindingsAsNewEntity();

	public SearchDialog(DocumentWindow xdw, Preferences configuration) {
		documentWindow = xdw;
		text = xdw.textPane.getText();
		contexts = configuration.getInt(Constants.CFG_SEARCH_RESULTS_CONTEXT, Defaults.CFG_SEARCH_RESULTS_CONTEXT);
		annotateSelectedFindings.setEnabled(false);
		tsl = new TSL(documentWindow.tree);
		documentWindow.tree.addTreeSelectionListener(tsl);

		this.initialiseWindow();
	}

	protected void initialiseWindow() {

		hilit = documentWindow.textPane.getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

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

		list = new JList<SearchResult>(lm);
		list.getSelectionModel().addListSelectionListener(tsl);
		list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setCellRenderer(new SearchResultRenderer());
		list.setVisibleRowCount(10);
		list.setTransferHandler(new ListTransferHandler());
		list.setDragEnabled(true);

		JScrollPane listScroller = new JScrollPane(list);
		setLocation(documentWindow.getLocation().x + documentWindow.getWidth(), documentWindow.getLocation().y);

		JPanel statusbar = new JPanel();
		statusbar.add(searchResultsLabel);
		statusbar.add(selectedEntityLabel);
		getContentPane().add(statusbar, BorderLayout.SOUTH);
		getContentPane().add(searchPanel, BorderLayout.NORTH);
		getContentPane().add(listScroller, BorderLayout.CENTER);

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

	public synchronized void search(String s) {
		list.getSelectionModel().removeListSelectionListener(tsl);
		list.clearSelection();
		annotateSelectedFindings.setEnabled(false);
		annotateSelectedFindingsAsNew.setEnabled(false);
		searchResultsLabel.setText("");
		lm.clear();
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
					lm.addElement(new SearchResult(m.start(), m.end()));
					highlights.add(hilit.addHighlight(m.start(), m.end(), painter));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				finding++;
			}
			list.getSelectionModel().addListSelectionListener(tsl);
			tsl.listCondition = false;

			searchResultsLabel.setText(
					(m.find() ? Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS_MORE_THAN) + " " : "")
							+ lm.size() + " " + Annotator.getString(Constants.Strings.STATUS_SEARCH_RESULTS));

		}

		pack();
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

		@Override
		public String toString() {
			return text.substring(Integer.max(span.begin - contexts, 0),
					Integer.min(span.end + contexts, text.length() - 1));
		}

		public Span getSpan() {
			return span;
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
			panel.add(left);
			panel.add(center);
			panel.add(right);

			return panel;
		}

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		for (Object o : highlights)
			hilit.removeHighlight(o);
		dispose();

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

	class TSL extends CATreeSelectionListener implements ListSelectionListener {

		public TSL(JTree tree) {
			super(tree);
		}

		boolean treeCondition = false;
		boolean listCondition = false;

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

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (list.getSelectedIndices().length == 1) {
					SearchResult result = lm.getElementAt(((ListSelectionModel) e.getSource()).getMinSelectionIndex());
					documentWindow.textPane.setCaretPosition(result.getEnd());
				}
				listCondition = (list.getSelectedValuesList().size() > 0);
				annotateSelectedFindings.setEnabled(treeCondition && listCondition);
				annotateSelectedFindingsAsNew.setEnabled(list.getSelectedValuesList().size() > 0);
				Annotator.logger.debug("Setting listCondition to {}", listCondition);
			}
		}

	}
}
