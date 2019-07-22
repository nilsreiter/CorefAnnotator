package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityKey;

public class ChangeKeyForEntityAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ChangeKeyForEntityAction(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_SET_SHORTCUT, MaterialDesign.MDI_KEYBOARD);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SET_SHORTCUT_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Entity entity = getTarget().getSelectedEntities().getOnly();
		String s = "";
		if (entity.getKey() != null)
			s = entity.getKey();

		JPanel panel = new JPanel();
		panel.add(new JLabel(Annotator.getString(Strings.DIALOG_CHANGE_KEY_PROMPT)));
		JTextField textField = new JTextField(1);
		textField.setText(s);
		panel.add(textField);

		int result = JOptionPane.showOptionDialog(getTarget(), panel, "", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, FontIcon.of(MaterialDesign.MDI_KEYBOARD),
				new String[] { Annotator.getString(Strings.DIALOG_CHANGE_KEY_CLEAR),
						Annotator.getString(Strings.DIALOG_CHANGE_KEY_CANCEL),
						Annotator.getString(Strings.DIALOG_CHANGE_KEY_OK) },
				s);
		String newKey = textField.getText();
		switch (result) {
		case 2:
			// for setting a new key
			if (newKey.length() == 1) {
				Character newChar = newKey.charAt(0);
				getTarget().getDocumentModel().edit(new UpdateEntityKey(newChar, entity));
			} else {
				JOptionPane.showMessageDialog(getTarget(),
						Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_MESSAGE),
						Annotator.getString(Strings.DIALOG_CHANGE_KEY_INVALID_STRING_TITLE),
						JOptionPane.INFORMATION_MESSAGE);
			}
			break;
		default:
			// for clearing the key
			getTarget().getDocumentModel().edit(new UpdateEntityKey(entity));
			break;
		}

	}

}