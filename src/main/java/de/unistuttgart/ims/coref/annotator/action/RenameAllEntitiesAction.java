package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.op.RenameAllEntities;

public class RenameAllEntitiesAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;
	RenameAllEntities.Strategy strategy = null;

	public RenameAllEntitiesAction(DocumentWindow dw, RenameAllEntities.Strategy strategy) {
		super(dw, Strings.ACTION_RENAME_ALL, MaterialDesign.MDI_RENAME_BOX);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_RENAME_ALL_TOOLTIP));
		this.strategy = strategy;
	}

	public RenameAllEntitiesAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_RENAME_ALL, MaterialDesign.MDI_RENAME_BOX);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		RenameAllEntities.Strategy thisStrategy = null;

		if (strategy == null) {
			Object[] options = Lists.immutable.of(RenameAllEntities.Strategy.values())
					.collect(s -> Annotator.getString("dialog.rename_all.options." + s.name())).toArray();

			int ret = JOptionPane.showOptionDialog(getTarget(),
					Annotator.getString(Strings.DIALOG_RENAME_ALL_SELECT_STRATEGY_PROMPT),
					Annotator.getString(Strings.DIALOG_RENAME_ALL_SELECT_STRATEGY_TITLE), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, FontIcon.of(MaterialDesign.MDI_RENAME_BOX), options, options[0]);
			if (ret != JOptionPane.CLOSED_OPTION) {
				thisStrategy = RenameAllEntities.Strategy.values()[ret];
			}
		} else {
			thisStrategy = strategy;
		}
		if (thisStrategy != null)
			getTarget().getDocumentModel().edit(new RenameAllEntities(thisStrategy));
	}

}
