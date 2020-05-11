package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.action.HelpAction;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.action.TargetedOperationIkonAction;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;

public class SearchTextPanel extends SearchPanel<SearchResult>
		implements DocumentListener, WindowListener, HasDocumentModel {

	class AnnotateSelectedFindings extends TargetedOperationIkonAction<SearchTextPanel> {

		private static final long serialVersionUID = 1L;

		public AnnotateSelectedFindings() {
			super(SearchTextPanel.this, Strings.ACTION_ADD_FINDINGS_TO_ENTITY, MaterialDesign.MDI_ACCOUNT);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FINDINGS_TO_ENTITY_TOOLTIP));
			// this.addIkon(MaterialDesign.MDI_ACCOUNT);
			operationClass = AddMentionsToEntity.class;
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

	class AnnotateSelectedFindingsAsNewEntity extends TargetedOperationIkonAction<SearchTextPanel> {
		private static final long serialVersionUID = 1L;

		public AnnotateSelectedFindingsAsNewEntity() {
			super(SearchTextPanel.this, Strings.ACTION_ADD_FINDINGS_TO_NEW_ENTITY, MaterialDesign.MDI_ACCOUNT_PLUS);
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_FINDINGS_TO_NEW_ENTITY_TOOLTIP));
			operationClass = AddMentionsToNewEntity.class;
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

			if (Annotator.app.getPreferences().getBoolean(Constants.CFG_REPLACE_MENTION, false)) {
				return new AnnotationTransfer(Lists.immutable.ofAll(list.getSelectedValuesList())
						.collect(sr -> sr.getSpan()).flatCollect(span -> searchContainer.getDocumentWindow()
								.getDocumentModel().getCoreferenceModel().getMentionsBetween(span.begin, span.end)));

			} else
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
			super(Strings.ACTION_SEARCH, MaterialDesign.MDI_FILE_FIND);
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			search(textField.getText());
		}

	}

	class TSL implements ListSelectionListener, TreeSelectionListener {

		boolean treeCondition = false;

		boolean listCondition = false;

		public TSL(JTree tree) {
			super();
			tree.addTreeSelectionListener(this);
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
			TreeSelectionUtil tsu = new TreeSelectionUtil(e);
			treeCondition = (tsu.isSingle() && tsu.isEntity());
			Annotator.logger.debug("Setting treeCondition to {}", treeCondition);
			annotateSelectedFindings.setEnabled(treeCondition && listCondition);
			if (treeCondition)
				selectedEntityLabel.setText(Annotator.getString(Strings.STATUS_SEARCH_SELECTED_ENTITY) + ": "
						+ tsu.getEntity(0).getLabel());
			else
				selectedEntityLabel.setText("");
		}

		public void setTreeCondition(boolean treeCondition) {
			this.treeCondition = treeCondition;
		}

	}

	private static final long serialVersionUID = 1L;
	JTextField textField;
	JCheckBox restrictToMentions;
	JList<SearchResult> text_list;
	AbstractAction annotateSelectedFindings = new AnnotateSelectedFindings(), runSearch = new RunSearch(),
			annotateSelectedFindingsAsNew = new AnnotateSelectedFindingsAsNewEntity();
	TSL tsl = null;
	JLabel selectedEntityLabel = new JLabel();
	int limit = 1000;
	SearchThread thread = null;

	public SearchTextPanel(SearchContainer sd) {
		super(sd);

		tsl = new TSL(searchContainer.getDocumentWindow().getTree());
		tsl.setTreeCondition(!searchContainer.getDocumentWindow().getTree().isSelectionEmpty());
		annotateSelectedFindings.setEnabled(false);
		annotateSelectedFindingsAsNew.setEnabled(false);

		textField = new JTextField(20);
		textField.setToolTipText(Annotator.getString(Strings.SEARCH_WINDOW_TEXT_TOOLTIP));
		textField.getDocument().addDocumentListener(this);

		restrictToMentions = new JCheckBox(Annotator.getString(Strings.SEARCH_WINDOW_RESTRICT_TO_MENTIONS));
		restrictToMentions.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				search(textField.getText());
			}

		});
		JToolBar behaviourBar = new JToolBar();
		behaviourBar.setFloatable(false);
		behaviourBar.add(runSearch);
		behaviourBar.add(new HelpAction(HelpWindow.Topic.SEARCH));
		behaviourBar.add(restrictToMentions);

		JToolBar actionBar = new JToolBar();
		actionBar.setLayout(new BoxLayout(actionBar, BoxLayout.Y_AXIS));
		actionBar.setFloatable(false);
		actionBar.add(annotateSelectedFindings);
		actionBar.add(annotateSelectedFindingsAsNew);
		actionBar.add(clearFindings);

		JPanel searchBehaviourPanel = new JPanel();
		searchBehaviourPanel.add(textField);
		searchBehaviourPanel.add(behaviourBar);

		JPanel searchActionPanel = new JPanel();
		searchActionPanel.add(actionBar);

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
		searchPanel.add(searchBehaviourPanel);
		searchPanel.add(searchActionPanel);

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
		add(actionBar, BorderLayout.WEST);
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

	public void search(String s) {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
		thread = new SearchThread(s);
		thread.run();
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

	class SearchThread extends Thread implements Runnable {

		String searchString;

		public SearchThread(String searchString) {
			this.searchString = searchString;
		}

		@Override
		public void run() {
			text_list.getSelectionModel().removeListSelectionListener(tsl);
			text_list.clearSelection();
			annotateSelectedFindings.setEnabled(false);
			annotateSelectedFindingsAsNew.setEnabled(false);
			clearResults();
			if (searchString.length() > 0) {
				Pattern p = Pattern.compile(searchString);

				if (restrictToMentions.isSelected()) {
					for (Mention m : searchContainer.getDocumentWindow().getDocumentModel().getCoreferenceModel()
							.getMentions()) {
						Matcher matcher = p.matcher(m.getCoveredText());
						if (matcher.find()) {
							try {
								listModel.addElement(new SearchResult(searchContainer, m.getBegin(), m.getEnd()));
								highlights.add(hilit.addHighlight(m.getBegin(), m.getEnd(), painter));
							} catch (BadLocationException e) {
								Annotator.logger.catching(e);
							}

						}
					}
				} else {
					Matcher m = p.matcher(searchContainer.getDocumentWindow().getText());
					int finding = 0;
					while (m.find() && finding < limit) {
						try {
							listModel.addElement(new SearchResult(searchContainer, m.start(), m.end()));
							highlights.add(hilit.addHighlight(m.start(), m.end(), painter));
						} catch (BadLocationException e) {
							Annotator.logger.catching(e);
						}
						finding++;
					}
				}
				text_list.getSelectionModel().addListSelectionListener(tsl);
				tsl.listCondition = false;
			}
			updateLabel();
			searchContainer.pack();
		}

	}

	@Override
	public DocumentModel getDocumentModel() {
		return searchContainer.getDocumentWindow().getDocumentModel();
	}

}