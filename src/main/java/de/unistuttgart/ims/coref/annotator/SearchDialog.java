package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SearchDialog extends JDialog implements DocumentListener, WindowListener, SearchContainer {

	private static final String ACTION_CLOSE = "close";

	private static final long serialVersionUID = 1L;
	DocumentWindow documentWindow;
	String text;

	int contexts = Defaults.CFG_SEARCH_RESULTS_CONTEXT;

	public SearchDialog(DocumentWindow xdw, Preferences configuration) {
		documentWindow = xdw;
		text = xdw.textPane.getText();
		contexts = configuration.getInt(Constants.CFG_SEARCH_RESULTS_CONTEXT, Defaults.CFG_SEARCH_RESULTS_CONTEXT);

		documentWindow.addWindowListener(new DocumentWindowWindowListener());

		// this allows closing by hitting command-w
		InputMap inputMap = ((JPanel) this.getContentPane()).getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				ACTION_CLOSE);
		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();
		actionMap.put(ACTION_CLOSE, new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				SearchDialog.this.dispose();
			}
		});
		this.initialiseWindow();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Override
	public int getContexts() {
		return contexts;
	}

	@Override
	public DocumentWindow getDocumentWindow() {
		return documentWindow;
	}

	@Override
	public String getText() {
		return text;
	}

	protected void initialiseWindow() {

		JTabbedPane tabbedPane = new JTabbedPane();

		SearchTextPanel pane1 = new SearchTextPanel(this);
		SearchAnnotationPanel pane2 = new SearchAnnotationPanel(this);
		addWindowListener(pane1);
		addWindowListener(pane2);

		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_TEXT), pane1);
		tabbedPane.addTab(Annotator.getString(Constants.Strings.SEARCH_WINDOW_TAB_STRUCTURE), pane2);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);
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

	public void setContexts(int contexts) {
		this.contexts = contexts;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
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

	class DocumentWindowWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
			SearchDialog.this.setVisible(false);
			SearchDialog.this.dispose();
		}

		@Override
		public void windowIconified(WindowEvent e) {
			SearchDialog.this.setVisible(false);
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			SearchDialog.this.setVisible(true);
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

	}

}
