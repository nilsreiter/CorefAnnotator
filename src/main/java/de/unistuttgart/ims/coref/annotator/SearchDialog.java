package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class SearchDialog extends JDialog implements DocumentListener, WindowListener, SearchContainer {

	final static Color HILIT_COLOR = Color.black;
	private static final long serialVersionUID = 1L;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;
	DocumentWindow documentWindow;
	String text;
	DefaultListModel<SearchResult> text_lm = new DefaultListModel<SearchResult>();
	DefaultListModel<SearchResultMention> struct_lm = new DefaultListModel<SearchResultMention>();
	JList<SearchResult> text_list;
	JTextField textField;

	JLabel searchResultsLabel = new JLabel(), selectedEntityLabel = new JLabel();

	int contexts = Defaults.CFG_SEARCH_RESULTS_CONTEXT;

	Set<Object> highlights = new HashSet<Object>();

	int limit = 1000;

	JList<SearchResultMention> struct_list;

	public SearchDialog(DocumentWindow xdw, Preferences configuration) {
		documentWindow = xdw;
		text = xdw.textPane.getText();
		contexts = configuration.getInt(Constants.CFG_SEARCH_RESULTS_CONTEXT, Defaults.CFG_SEARCH_RESULTS_CONTEXT);

		this.initialiseWindow();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Deprecated
	protected JPanel initialiseStructuredSearchPanel() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(new JLabel(Annotator.getString(Constants.Strings.ACTION_SEARCH_MENTION)));

		JPanel searchPanel = new JPanel();
		searchPanel.add(bar);

		struct_list = new JList<SearchResultMention>(struct_lm);
		struct_list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		struct_list.setCellRenderer(new SearchResultRenderer(text, contexts));
		struct_list.setVisibleRowCount(10);
		struct_list.setDragEnabled(false);

		JScrollPane listScroller = new JScrollPane(struct_list);

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
		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_TEXT), new SearchTextPanel(this));
		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_STRUCTURE),
				new SearchAnnotationPanel(this));

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

	}

	@Override
	public void removeUpdate(DocumentEvent e) {

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

	@Override
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public int getContexts() {
		return contexts;
	}

	public void setContexts(int contexts) {
		this.contexts = contexts;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public DocumentWindow getDocumentWindow() {
		return documentWindow;
	}

}
