package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationTreeNode;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.ViewFontFamilySelectAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeDecreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeIncreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineNumberStyle;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineSpacingAction;
import de.unistuttgart.ims.coref.annotator.action.ViewStyleSelectAction;
import de.unistuttgart.ims.coref.annotator.api.v2.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.v2.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.Segment;
import de.unistuttgart.ims.coref.annotator.comp.FixedTextLineNumber;
import de.unistuttgart.ims.coref.annotator.comp.TextLineNumber;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public abstract class AbstractTextWindow extends AbstractWindow implements HasTextView, CoreferenceModelListener {

	public class TOCSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			Object src = e.getSource();

			if (src != AbstractTextWindow.this) {
				TreePath tp = e.getNewLeadSelectionPath();
				if (tp == null)
					return;
				@SuppressWarnings("unchecked")
				AnnotationTreeNode<Segment> tn = (AnnotationTreeNode<Segment>) tp.getLastPathComponent();
				try {
					int position = UimaUtil.nextCharacter(getJCas(), tn.get().getBegin(),
							ch -> !ArrayUtils.contains(new char[] { ' ', '\n', '\t', '\f', '\r' }, ch));

					Rectangle rect = textPane.modelToView2D(position).getBounds();

					// this is a bit experimental
					rect.height = rect.height * 2;// (textPane.getParent().getHeight() / 2);

					textScrollPane.getViewport().setViewPosition(rect.getLocation());

				} catch (BadLocationException e1) {
					Annotator.logger.catching(e1);
				}
			}
		}

	}

	public class TOCRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (value instanceof AnnotationTreeNode) {
				@SuppressWarnings("unchecked")
				AnnotationTreeNode<Segment> atn = (AnnotationTreeNode<Segment>) value;
				setText(StringUtils.abbreviate(atn.get().getLabel(), 20));
			}
			if (leaf)
				setIcon(FontIcon.of(MaterialDesign.MDI_MINUS));
			else if (expanded)
				setIcon(FontIcon.of(FontAwesome.FOLDER_OPEN_O));
			else
				setIcon(FontIcon.of(FontAwesome.FOLDER_O));
			return this;
		}

	}

	private static final long serialVersionUID = 1L;

	MutableList<DocumentModel> documentModels;

	HighlightManager highlightManager;
	JTextPane textPane;
	JTree tableOfContents;
	JScrollPane tocScrollPane;
	JScrollPane textScrollPane;
	JPanel textPanel;

	LineNumberStyle lineNumberStyle;

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	ViewSetLineNumberStyle lineNumberStyleNone = new ViewSetLineNumberStyle(this, LineNumberStyle.NONE);
	ViewSetLineNumberStyle lineNumberStyleFixed = new ViewSetLineNumberStyle(this, LineNumberStyle.FIXED);
	ViewSetLineNumberStyle lineNumberStyleDynamic = new ViewSetLineNumberStyle(this, LineNumberStyle.DYNAMIC);

	Map<StylePlugin, JRadioButtonMenuItem> styleMenuItem = new HashMap<StylePlugin, JRadioButtonMenuItem>();

	// Settings
	StylePlugin currentStyle;
	StyleContext styleContext = new StyleContext();

	public void addStyleChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removeStyleChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public enum LineNumberStyle {
		NONE, FIXED, DYNAMIC
	}

	@Override
	public String getText() {
		return getJCas().getDocumentText();
	}

	@Override
	public JCas getJCas() {
		return getDocumentModel().getJcas();
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		switch (eventType) {
		case Add:
			entityEventAdd(event);
			break;
		case Remove:
			entityEventRemove(event);
			break;
		case Update:
			entityEventUpdate(event);
			break;
		case Move:
			entityEventMove(event);
			break;
		case Merge:
			entityEventMerge(event);
			break;
		case Op:
			entityEventOp(event);
			break;
		case Init:
			entityEventInit(event);
		default:
		}
	}

	protected void entityEventAdd(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention) {
				highlightManager.underline((Mention) fs);
			} else if (fs instanceof DetachedMentionPart) {
				highlightManager.underline((Annotation) fs);
			} else if (fs instanceof CommentAnchor) {
				highlightManager.highlight((Annotation) fs);
			}
		}
	}

	protected void entityEventRemove(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention) {
				if (((Mention) fs).getDiscontinuous() != null)
					highlightManager.unUnderline(((Mention) fs).getDiscontinuous());
				highlightManager.unUnderline((Mention) fs);
			} else if (fs instanceof Annotation)
				highlightManager.unUnderline((Annotation) fs);

		}
	}

	protected void entityEventUpdate(FeatureStructureEvent event) {
		for (FeatureStructure fs : event) {
			if (fs instanceof Mention) {
				if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
					highlightManager.unUnderline((Annotation) fs);
				else
					highlightManager.underline((Annotation) fs);
			}
		}
	}

	protected void entityEventMove(FeatureStructureEvent event) {
		for (FeatureStructure fs : event) {
			if (fs instanceof Mention) {
				if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
					highlightManager.unUnderline((Annotation) fs);
				else
					highlightManager.underline((Annotation) fs);
			}
		}
	}

	protected void entityEventMerge(FeatureStructureEvent event) {

	}

	protected void entityEventOp(FeatureStructureEvent event) {

	}

	protected void entityEventInit(FeatureStructureEvent event) {
		CoreferenceModel cm = (CoreferenceModel) event.getSource();
		for (Mention m : cm.getMentions()) {
			highlightManager.underline(m);
			if (m.getDiscontinuous() != null)
				highlightManager.underline(m.getDiscontinuous());
		}
	}

	public <T extends Annotation> MutableSet<T> getSelectedAnnotations(Class<T> clazz) {
		MutableSet<Mention> annotations = getDocumentModel().getCoreferenceModel()
				.getMentions(getTextPane().getSelectionStart())
				.select(a -> UimaUtil.getBegin(a) == getTextPane().getSelectionStart()
						&& UimaUtil.getEnd(a) == getTextPane().getSelectionEnd());
		return annotations.selectInstancesOf(clazz);
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public DocumentModel getDocumentModel() {
		if (documentModels == null || documentModels.isEmpty())
			return null;
		return documentModels.getFirst();
	}

	public LineNumberStyle getLineNumberStyle() {
		return lineNumberStyle;
	}

	public void setLineNumberStyle(LineNumberStyle lns) {
		lineNumberStyle = lns;
		TextLineNumber tln;
		switch (lns) {
		case FIXED:
			tln = new FixedTextLineNumber(this, 5);
			pcs.addPropertyChangeListener(tln);
			break;
		case DYNAMIC:
			tln = new TextLineNumber(this, 5);
			pcs.addPropertyChangeListener(tln);
			break;
		default:
			tln = null;
		}
		textScrollPane.setRowHeaderView(tln);
	}

	protected JMenu initialiseMenuView() {
		JRadioButtonMenuItem radio;
		JMenu viewMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW));
		viewMenu.add(new ViewFontSizeDecreaseAction(this));
		viewMenu.add(new ViewFontSizeIncreaseAction(this));

		ButtonGroup grp = new ButtonGroup();

		JMenu lineSpacingMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_LINE_SPACING));
		lineSpacingMenu.setIcon(FontIcon.of(MaterialDesign.MDI_FORMAT_LINE_SPACING));
		for (int i = 0; i < 10; i++) {
			ViewSetLineSpacingAction action = new ViewSetLineSpacingAction(this, i * 0.5f);
			radio = new JRadioButtonMenuItem(action);
			grp.add(radio);
			lineSpacingMenu.add(radio);
			this.addStyleChangeListener(action);
		}

		viewMenu.add(lineSpacingMenu);

		JMenu fontFamilyMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_FONTFAMILY));
		String[] fontFamilies = new String[] { Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED };
		grp = new ButtonGroup();
		for (String s : fontFamilies) {
			AbstractAction a = new ViewFontFamilySelectAction(this, s);
			radio = new JRadioButtonMenuItem(a);
			fontFamilyMenu.add(radio);
			grp.add(radio);
		}
		// TODO: Disabled for the moment
		// viewMenu.add(fontFamilyMenu);

		grp = new ButtonGroup();
		JMenu lineNumbersMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_LINE_NUMBERS));
		radio = new JRadioButtonMenuItem(lineNumberStyleNone);
		radio.setSelected(true);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		radio = new JRadioButtonMenuItem(lineNumberStyleFixed);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		radio = new JRadioButtonMenuItem(lineNumberStyleDynamic);
		grp.add(radio);
		lineNumbersMenu.add(radio);

		viewMenu.add(lineNumbersMenu);
		viewMenu.addSeparator();

		PluginManager pm = Annotator.app.getPluginManager();

		JMenu viewStyleMenu = new JMenu(Annotator.getString(Strings.MENU_VIEW_STYLE));
		grp = new ButtonGroup();
		StylePlugin pl = pm.getDefaultStylePlugin();
		JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(
				new ViewStyleSelectAction(this, pm.getDefaultStylePlugin()));
		radio1.setSelected(true);
		viewStyleMenu.add(radio1);
		styleMenuItem.put(pl, radio1);
		grp.add(radio1);
		for (Class<? extends StylePlugin> plugin : pm.getStylePlugins()) {
			pl = pm.getStylePlugin(plugin);
			radio1 = new JRadioButtonMenuItem(new ViewStyleSelectAction(this, pl));
			viewStyleMenu.add(radio1);
			styleMenuItem.put(pl, radio1);
			grp.add(radio1);

		}
		viewMenu.add(viewStyleMenu);
		return viewMenu;

	}

	public StylePlugin getCurrentStyle() {
		return currentStyle;
	}

	public void switchStyle(StylePlugin sv) {
		switchStyle(sv, sv.getBaseStyle());
	}

	public void switchStyle(StylePlugin sv, AttributeSet baseStyle) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				getProgressBar().setValue(0);
				getProgressBar().setVisible(true);
				Annotator.logger.debug("Activating style {}", sv.getClass().getName());

				getProgressBar().setValue(20);

				Map<AttributeSet, org.apache.uima.cas.Type> styles = sv
						.getSpanStyles(getDocumentModel().getJcas().getTypeSystem(), styleContext, baseStyle);
				StyleManager.styleCharacter(textPane.getStyledDocument(), baseStyle);

				for (Enumeration<?> e = baseStyle.getAttributeNames(); e.hasMoreElements();) {
					Object aName = e.nextElement();
					pcs.firePropertyChange(aName.toString(), null, baseStyle.getAttribute(aName));
				}
				textPane.getStyledDocument().setParagraphAttributes(0, textPane.getDocument().getLength(), baseStyle,
						true);

				if (styles != null)
					for (AttributeSet style : styles.keySet()) {
						StyleManager.style(getDocumentModel().getJcas(), textPane.getStyledDocument(), style,
								styles.get(style));
						getProgressBar().setValue(getProgressBar().getValue() + 10);
					}
				Util.getMeta(getDocumentModel().getJcas()).setStylePlugin(sv.getClass().getName());
				currentStyle = sv;
				styleMenuItem.get(sv).setSelected(true);
				getMiscLabel().setText(Annotator.getString(Strings.STATUS_STYLE) + ": " + sv.getName());
				getMiscLabel().setToolTipText(sv.getDescription());
				getMiscLabel().repaint();
				progressBar.setValue(100);
				progressBar.setVisible(false);
			}

		});

	}

	public void updateStyle(Object constant, Object value) {
		MutableAttributeSet baseStyle = currentStyle.getBaseStyle();
		Object oldValue = baseStyle.getAttribute(constant);
		baseStyle.addAttribute(constant, value);
		pcs.firePropertyChange(constant.toString(), oldValue, value);
		switchStyle(currentStyle);
	}

	public void setDocumentModel(DocumentModel documentModel) {
		if (this.documentModels == null)
			this.documentModels = Lists.mutable.with(documentModel);
		else
			this.documentModels.set(0, documentModel);
	}

	protected void highlightSegmentInTOC(int textPosition) {
		if (getDocumentModel() != null && textPosition >= 0 && tableOfContents != null) {
			Segment segment = getDocumentModel().getSegmentModel().getSegmentAt(textPosition);
			if (segment != null) {
				TreePath tp = getDocumentModel().getSegmentModel().getPathTo(segment);
				TreeSelectionListener[] tsls = tableOfContents.getTreeSelectionListeners();
				for (TreeSelectionListener tsl : tsls) {
					tableOfContents.removeTreeSelectionListener(tsl);
				}
				tableOfContents.scrollPathToVisible(tp);
				tableOfContents.setSelectionPath(tp);
				for (TreeSelectionListener tsl : tsls) {
					tableOfContents.addTreeSelectionListener(tsl);
				}
			}
		}

	}

	protected void initializeTOC() {
		tableOfContents = new JTree(new Object[] {});
		tableOfContents.setRootVisible(false);
		tableOfContents.setToggleClickCount(2);
		tableOfContents.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 6));
		tableOfContents.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tableOfContents.setCellRenderer(new TOCRenderer());
		tableOfContents.addTreeSelectionListener(new TOCSelectionListener());

		tocScrollPane = new JScrollPane(tableOfContents);
	}

	@Override
	protected void initializeWindow() {
		super.initializeWindow();

		if (Annotator.app.getPreferences().getBoolean(Constants.CFG_SHOW_TOC, Defaults.CFG_SHOW_TOC))
			initializeTOC();

		textPane = new JTextPane();
		textPane.setDragEnabled(true);
		textPane.setEditable(false);

		textScrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JViewport vp = (JViewport) e.getSource();
				Point point = vp.getViewPosition();

				int textPosition = textPane.viewToModel2D(point);
				highlightSegmentInTOC(textPosition);
			}

		});

		textPanel = new JPanel(new BorderLayout());
		textPanel.add(textScrollPane, BorderLayout.CENTER);
		if (tableOfContents != null)
			textPanel.add(tocScrollPane, BorderLayout.WEST);

	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equalsIgnoreCase(Constants.CFG_SHOW_TOC)) {
			boolean b = Boolean.valueOf(evt.getNewValue());
			if (b) {
				initializeTOC();
				tableOfContents.setModel(getDocumentModel().getSegmentModel());
				textPanel.add(tocScrollPane, BorderLayout.WEST);
				textPanel.revalidate();
				textPanel.repaint();
			} else {
				textPanel.remove(tocScrollPane);
				tocScrollPane.removeAll();
				tocScrollPane = null;
				tableOfContents = null;
				textPanel.revalidate();
				textPanel.repaint();
			}
		} else {
			super.preferenceChange(evt);
		}
	};

}
