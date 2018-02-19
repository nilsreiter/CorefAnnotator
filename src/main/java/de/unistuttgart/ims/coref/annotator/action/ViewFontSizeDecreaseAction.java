package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.StyleConstants;

import org.kordamp.ikonli.material.Material;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class ViewFontSizeDecreaseAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public ViewFontSizeDecreaseAction(DocumentWindow documentWindow) {
		super(documentWindow, Material.EXPOSURE_NEG_1, Strings.ACTION_VIEW_DECREASE_FONT_SIZE);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.updateStyle(StyleConstants.FontSize,
				StyleConstants.getFontSize(documentWindow.getCurrentStyle().getBaseStyle()) - 1);
	}

}