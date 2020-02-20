package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.StyleConstants;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.Strings;

public class ViewFontSizeIncreaseAction extends TargetedIkonAction<AbstractTextWindow> {
	private static final long serialVersionUID = 1L;

	public ViewFontSizeIncreaseAction(AbstractTextWindow dw) {
		super(dw, Strings.ACTION_VIEW_INCREASE_FONT_SIZE, MaterialDesign.MDI_FORMAT_SIZE);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().updateStyle(StyleConstants.FontSize,
				StyleConstants.getFontSize(getTarget().getCurrentStyle().getBaseStyle()) + 1);
	}
}
