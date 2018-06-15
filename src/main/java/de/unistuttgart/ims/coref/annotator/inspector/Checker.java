package de.unistuttgart.ims.coref.annotator.inspector;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.inspector.detector.BeginBoundary;
import de.unistuttgart.ims.coref.annotator.inspector.detector.EndBoundary;
import de.unistuttgart.ims.coref.annotator.inspector.detector.MentionOfZeroLength;

public class Checker extends SwingWorker<ListModel<Issue>, Object> {

	JCas jcas;
	DocumentModel documentModel;
	Inspector caller;
	DefaultListModel<Issue> listModel;
	ImmutableList<Detector<Mention>> mentionDetectors = Lists.immutable.of(new BeginBoundary(), new EndBoundary(),
			new MentionOfZeroLength());

	public static char[] whitespace = new char[] { ' ', '\n', '\t', '\r', '\f' };

	public Checker(DocumentModel documentModel, Inspector caller, DefaultListModel<Issue> listModel) {
		this.jcas = documentModel.getJcas();
		this.documentModel = documentModel;
		this.caller = caller;
		this.listModel = listModel;
	}

	@Override
	protected ListModel<Issue> doInBackground() throws Exception {
		String textString = jcas.getDocumentText();
		char[] text = textString.toCharArray();

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			int b = m.getBegin(), e = m.getEnd();
			String surface1 = textString.substring(b - 1, e + 1);
			Pattern p = Pattern.compile(".\\b\\Q" + m.getCoveredText() + "\\E.",
					Pattern.DOTALL | Pattern.UNICODE_CHARACTER_CLASS);

			boolean ok = true;
			for (Detector<Mention> det : mentionDetectors) {
				if (ok && det.detect(m, text)) {
					listModel.addElement(det.getIssue(getDocumentModel(), m));
					ok = false;
				}
			}

			// if (!p.matcher(surface1).matches())
			// listModel.addElement(new Issue3(getDocumentModel(), m));

		}
		return listModel;
	}

	@Override
	protected void done() {
		try {
			caller.setListModel(get());
		} catch (InterruptedException | ExecutionException e) {
			Annotator.logger.catching(e);
		}
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

}
