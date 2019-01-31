package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.BadLocationException;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;

public class SearchTextPanel extends SearchPanel<SearchResult> implements DocumentListener, WindowListener {
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
			CATreeNode node = (CATreeNode) searchContainer.getDocumentWindow().getTree().getSelectionPath()
					.getLastPathComponent();

			AddMentionsToEntity op = new AddMentionsToEntity(node.getEntity(),
					Lists.immutable.withAll(text_list.getSelectedValuesList()).collect(r -> r.getSpan()));
			searchContainer.getDocumentWindow().getDocumentModel().edit(op);
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
			searchContainer.getDocumentWindow().getDocumentModel().edit(op);
		}
	}

	class ListTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public Transferable createTransferable(JComponent comp) {
			@SuppressWarnings("unchecked")
			JList<SearchResult> list = (JList<SearchResult>) comp;

			return new PotentialAnnotationTransfer(searchContainer.getDocumentWindow().getTextPane(),
					Lists.immutable.ofAll(list.getSelectedValuesList()).collect(sr -> sr.getSpan()));
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.LINK;
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

	class TSL extends CATreeSelectionEvent implements ListSelectionListener {

		boolean treeCondition = false;

		boolean listCondition = false;

		public TSL(JTree tree) {
			super(tree);
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (text_list.getSelectedIndices().length == 1) {
					SearchResult result = listModel
							.getElementAt(((ListSelectionModel) e.getSource()).getMinSelectionIndex());
					searchContainer.getDocumentWindow().getTextPane().setCaretPosition(result.getEnd());
				}
				listCondition = (text_list.getSelectedValuesList().size() > 0);
				annotateSelectedFindings.setEnabled(treeCondition && listCondition);
				annotateSelectedFindingsAsNew.setEnabled(text_list.getSelectedValuesList().size() > 0);
				Annotator.logger.debug("Setting listCondition to {}", listCondition);
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			treeCondition = (isSingle() && isEntity());
			Annotator.logger.debug("Setting treeCondition to {}", treeCondition);
			annotateSelectedFindings.setEnabled(treeCondition && listCondition);
			if (treeCondition)
				selectedEntityLabel.setText(Annotator.getString(Constants.Strings.STATUS_SEARCH_SELECTED_ENTITY) + ": "
						+ getEntity(0).getLabel());
			else
				selectedEntityLabel.setText("");
		}

	}

	private static final long serialVersionUID = 1L;
	JTextField textField;
	JList<SearchResult> text_list;
	AbstractAction annotateSelectedFindings = new AnnotateSelectedFindings(), runSearch = new RunSearch(),
			annotateSelectedFindingsAsNew = new AnnotateSelectedFindingsAsNewEntity();
	TSL tsl = null;
	JLabel selectedEntityLabel = new JLabel();
	int limit = 1000;

	public SearchTextPanel(SearchContainer sd) {
		super(sd);

		tsl = new TSL(searchContainer.getDocumentWindow().tree);
		annotateSelectedFindings.setEnabled(false);

		textField = new JTextField(20);
		textField.setToolTipText(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TEXT_TOOLTIP));
		textField.getDocument().addDocumentListener(this);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(runSearch);
		bar.add(annotateSelectedFindings);
		bar.add(annotateSelectedFindingsAsNew);
		bar.add(clearFindings);

		JPanel searchPanel = new JPanel();
		searchPanel.add(textField);
		searchPanel.add(bar);

		text_list = new JList<SearchResult>(listModel);
		text_list.getSelectionModel().addListSelectionListener(tsl);
		text_list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		text_list.setCellRenderer(
				new SearchResultRenderer<SearchResult>(searchContainer.getText(), searchContainer.getContexts()));
		text_list.setVisibleRowCount(10);
		text_list.setTransferHandler(new ListTransferHandler());
		text_list.setDragEnabled(true);

		searchContainer.getDocumentWindow().getTree().addTreeSelectionListener(tsl);

		JScrollPane listScroller = new JScrollPane(text_list);

		JPanel statusbar = new JPanel();
		statusbar.add(searchResultsLabel);
		statusbar.add(selectedEntityLabel);

		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(listScroller, BorderLayout.CENTER);
		add(statusbar, BorderLayout.SOUTH);
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
		clearResults();
		if (s.length() > 0) {

			Pattern p = Pattern.compile(s);
			Matcher m = p.matcher(searchContainer.getDocumentWindow().getText());
			int finding = 0;
			while (m.find() && finding < limit) {
				try {
					listModel.addElement(new SearchResult(this.searchContainer, m.start(), m.end()));
					highlights.add(hilit.addHighlight(m.start(), m.end(), painter));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				finding++;
			}
			text_list.getSelectionModel().addListSelectionListener(tsl);
			tsl.listCondition = false;
		}
		updateLabel();
		searchContainer.pack();

	}

	@Override
	public void windowActivated(WindowEvent e) {
		textField.grabFocus();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		for (Object o : highlights)
			hilit.removeHighlight(o);

	}

	@Override
	public void windowOpened(WindowEvent e) {
		textField.grabFocus();
	}

}