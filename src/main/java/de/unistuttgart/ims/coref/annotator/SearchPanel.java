package de.unistuttgart.ims.coref.annotator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.apache.commons.configuration2.Configuration;

import de.unistuttgart.ims.commons.Counter;

public class SearchPanel extends JFrame implements DocumentListener, ListSelectionListener, WindowListener {
	final static Color HILIT_COLOR = Color.black;

	private static final long serialVersionUID = 1L;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	DocumentWindow documentWindow;
	String text;
	DefaultListModel<SearchResult> lm;
	JList<SearchResult> list;
	JTextField textField;
	JPanel statusbar;
	int contexts = 50;
	boolean showBarChart = true;
	JFrame chartFrame;

	public SearchPanel(DocumentWindow xdw, Configuration configuration) {
		setTitle("Search");
		documentWindow = xdw;
		text = xdw.getViewer().getTextPane().getText();

		hilit = xdw.getViewer().getTextPane().getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		xdw.getViewer().getTextPane().setHighlighter(hilit);

		lm = new DefaultListModel<SearchResult>();
		getContentPane().add(createSearchPanel(), BorderLayout.PAGE_START);
		list = new JList<SearchResult>(lm);
		list.getSelectionModel().addListSelectionListener(this);
		list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new SearchResultRenderer());
		JScrollPane listScroller = new JScrollPane(list);
		// listScroller.setPreferredSize(new Dimension(300, 500));
		getContentPane().add(listScroller, BorderLayout.CENTER);
		setLocation(xdw.getLocation().x + xdw.getWidth(), xdw.getLocation().y);

		contexts = configuration.getInt("General.resultContext", 50);

		statusbar = new JPanel();
		getContentPane().add(statusbar, BorderLayout.SOUTH);

		addWindowListener(this);
		pack();
	}

	public JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		textField = new JTextField(20);
		textField.setToolTipText("Search");
		textField.getDocument().addDocumentListener(this);

		searchPanel.add(textField);
		return searchPanel;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			Pattern.compile(textField.getText());
			search(textField.getText());
		} catch (PatternSyntaxException ex) {
			ex.printStackTrace();
			// logger.trace(ex.getMessage());
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			Pattern.compile(textField.getText());
			search(textField.getText());
		} catch (PatternSyntaxException ex) {
			ex.printStackTrace();
			// SimpleXmiViewer.logger.trace(ex.getMessage());
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			Pattern.compile(textField.getText());
			search(textField.getText());
		} catch (PatternSyntaxException ex) {
			ex.printStackTrace();
			// SimpleXmiViewer.logger.trace(ex.getMessage());
		}
	}

	public void search(String s) {
		list.getSelectionModel().removeListSelectionListener(this);
		list.clearSelection();
		statusbar.removeAll();
		lm.clear();
		hilit.removeAllHighlights();
		if (chartFrame != null)
			chartFrame.dispose();
		Counter<String> counter = new Counter<String>();
		if (s.length() > 0) {

			Pattern p = Pattern.compile(s);
			Matcher m = p.matcher(text);
			while (m.find()) {
				try {
					lm.addElement(new SearchResult(m.start(), m.end()));
					hilit.addHighlight(m.start(), m.end(), painter);
					if (showBarChart)
						counter.add(text.substring(m.start(), m.end()));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			list.getSelectionModel().addListSelectionListener(this);
			statusbar.add(new JLabel(lm.size() + " search results."));

		}

		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		SearchResult result = lm.getElementAt(((ListSelectionModel) e.getSource()).getMinSelectionIndex());
		documentWindow.getViewer().getTextPane().setCaretPosition(result.getEnd());
	}

	class SearchResult {
		public SearchResult(int begin, int end) {
			super();
			this.begin = begin;
			this.end = end;
		}

		int begin, end;

		public int getBegin() {
			return begin;
		}

		public int getEnd() {
			return end;
		}

		@Override
		public String toString() {
			return text.substring(Integer.max(begin - contexts, 0), Integer.min(end + contexts, text.length() - 1));
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
			JLabel left = new JLabel(text.substring(Integer.max(value.begin - contexts, 0), value.begin));
			JLabel right = new JLabel(text.substring(value.end, Integer.min(value.end + contexts, text.length() - 1)));
			left.setFont(contextFont);
			right.setFont(contextFont);

			JLabel center = new JLabel(text.substring(value.begin, value.end));
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
		if (chartFrame != null) {
			chartFrame.setVisible(false);
			chartFrame.dispose();
		}
		hilit.removeAllHighlights();
		dispose();

	}

	@Override
	public void windowClosed(WindowEvent e) {

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

	class UnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
		public UnderlinePainter(Color color) {
			super(color);
		}

		/**
		 * Paints a portion of a highlight.
		 *
		 * @param g
		 *            the graphics context
		 * @param offs0
		 *            the starting model offset >= 0
		 * @param offs1
		 *            the ending model offset >= offs1
		 * @param bounds
		 *            the bounding box of the view, which is not necessarily the
		 *            region to paint.
		 * @param c
		 *            the editor
		 * @param view
		 *            View painting for
		 * @return region drawing occured in
		 */
		@Override
		public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
			Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

			Graphics2D g2 = (Graphics2D) g;

			if (r == null)
				return null;

			// Do your custom painting
			Color color = getColor();
			g.setColor(color == null ? c.getSelectionColor() : color);

			g2.setStroke(new BasicStroke(3));
			g2.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);

			return r;
		}

		private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
			// Contained in view, can just use bounds.

			if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
				Rectangle alloc;

				if (bounds instanceof Rectangle) {
					alloc = (Rectangle) bounds;
				} else {
					alloc = bounds.getBounds();
				}

				return alloc;
			} else {
				// Should only render part of View.
				try {
					// --- determine locations ---
					Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
					Rectangle r = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();

					return r;
				} catch (BadLocationException e) {
					// can't render
				}
			}

			// Can't render

			return null;
		}
	}
}
