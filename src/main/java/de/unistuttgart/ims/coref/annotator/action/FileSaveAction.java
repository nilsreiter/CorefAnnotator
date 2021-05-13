package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.uima.jcas.JCas;
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

	boolean closeAfterSaving = false;

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
		SaveTimerTask tt = new SaveTimerTask();
		getTarget().addWindowListener(tt);
		if (closeAfterSaving) {
			SwingUtilities.invokeLater(tt);
		} else
			timer.schedule(tt, when, period);
	}

	@Override
	public void documentStateEvent(DocumentState state) {
		setEnabled(state.isSavable() && target.getFile() != null);
		save(period);
	}

	class SaveTimerTask extends TimerTask implements WindowListener {

		@Override
		public void run() {
			target.setIndeterminateProgress();

			BiConsumer<File, JCas> bicons;
			if (closeAfterSaving) {
				bicons = (file, jcas) -> {
					SaveJCasWorker.getConsumer(getTarget()).accept(file, jcas);
					getTarget().closeWindow(false);
				};
			} else {
				bicons = SaveJCasWorker.getConsumer(getTarget());
			}
			SaveJCasWorker worker = new SaveJCasWorker(target.getFile(), target.getDocumentModel().getJcas(), bicons);
			worker.execute();
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			Annotator.logger.debug("Cancelling auto-save timer.");
			this.cancel();
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

	}

	/**
	 * @return the closeAfterSaving
	 */
	public boolean isCloseAfterSaving() {
		return closeAfterSaving;
	}

	/**
	 * @param closeAfterSaving the closeAfterSaving to set
	 */
	public void setCloseAfterSaving(boolean closeAfterSaving) {
		this.closeAfterSaving = closeAfterSaving;
	}

}
