package de.unistuttgart.ims.coref.annotator;

import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.MutableSet;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.ViewFontFamilySelectAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeDecreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewFontSizeIncreaseAction;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineNumberStyle;
import de.unistuttgart.ims.coref.annotator.action.ViewSetLineSpacingAction;
import de.unistuttgart.ims.coref.annotator.action.ViewStyleSelectAction;
import de.unistuttgart.ims.coref.annotator.api.v1.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.FixedTextLineNumber;
import de.unistuttgart.ims.coref.annotator.comp.TextLineNumber;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public abstract class AbstractTextWindow extends AbstractWindow implements HasTextView, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	HighlightManager highlightManager;
	JTextPane textPane;
	JScrollPane textScrollPane;

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
		return documentModel.getJcas();
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
			if (fs instanceof Mention || fs instanceof DetachedMentionPart) {
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
				highlightManager.unUnderline((Annotation) fs);
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
		MutableSet<Annotation> annotations = getDocumentModel().getCoreferenceModel()
				.getMentions(getTextPane().getSelectionStart())
				.select(a -> a.getBegin() == getTextPane().getSelectionStart()
						&& a.getEnd() == getTextPane().getSelectionEnd());
		return annotations.selectInstancesOf(clazz);
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
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
			break;
		case DYNAMIC:
			tln = new TextLineNumber(this, 5);
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
						.getSpanStyles(documentModel.getJcas().getTypeSystem(), styleContext, baseStyle);
				StyleManager.styleCharacter(textPane.getStyledDocument(), baseStyle);

				for (Enumeration<?> e = baseStyle.getAttributeNames(); e.hasMoreElements();) {
					Object aName = e.nextElement();
					pcs.firePropertyChange(aName.toString(), null, baseStyle.getAttribute(aName));
				}
				textPane.getStyledDocument().setParagraphAttributes(0, textPane.getDocument().getLength(), baseStyle,
						true);

				if (styles != null)
					for (AttributeSet style : styles.keySet()) {
						StyleManager.style(documentModel.getJcas(), textPane.getStyledDocument(), style,
								styles.get(style));
						getProgressBar().setValue(getProgressBar().getValue() + 10);
					}
				Util.getMeta(documentModel.getJcas()).setStylePlugin(sv.getClass().getName());
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
}
