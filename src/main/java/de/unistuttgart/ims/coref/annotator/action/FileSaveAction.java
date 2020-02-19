package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.DocumentState;
import de.unistuttgart.ims.coref.annotator.document.DocumentStateListener;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;

public class FileSaveAction extends TargetedIkonAction<DocumentWindow> implements DocumentStateListener {

	private static final long serialVersionUID = 1L;

	Timer timer = null;

	int period = Annotator.app.getPreferences().getInt(Constants.CFG_AUTOSAVE_TIMER, Defaults.CFG_AUTOSAVE_TIMER);

	public FileSaveAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_CONTENT_SAVE);
		putValue(Action.NAME, Annotator.getString("action.save"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		save(0);
	}

	void save(long when) {
		Annotator.logger.trace("saving in {} ms.", when);
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = new Timer();
		TimerTask tt = new SaveTimerTask();
		timer.schedule(tt, when, period);
	}

	@Override
	public void documentStateEvent(DocumentState state) {
		setEnabled(state.isSavable() && target.getFile() != null);
		save(period);
	}

	class SaveTimerTask extends TimerTask {

		@Override
		public void run() {
			target.setIndeterminateProgress();

			SaveJCasWorker worker = new SaveJCasWorker(target.getFile(), target.getDocumentModel().getJcas(),
					(file, jcas) -> {
						target.getDocumentModel().saved();
						target.setWindowTitle();
						target.stopIndeterminateProgress();
					});
			worker.execute();
		}

	}

}
