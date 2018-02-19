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

public class ViewFontSizeIncreaseAction extends DocumentWindowAction {
	private static final long serialVersionUID = 1L;

	public ViewFontSizeIncreaseAction(DocumentWindow dw) {
		super(dw, Material.EXPOSURE_PLUS_1, Strings.ACTION_VIEW_INCREASE_FONT_SIZE);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.updateStyle(StyleConstants.FontSize,
				StyleConstants.getFontSize(documentWindow.getCurrentStyle().getBaseStyle()) + 1);
	}
}
