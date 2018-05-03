package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class ExampleExport extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public ExampleExport(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_EXPORT_EXAMPLE, MaterialDesign.MDI_CODE_TAGS);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Span selection = getTarget().getSelection();
		JCas jcas = getTarget().getDocumentModel().getJcas();

		StringBuilder b = new StringBuilder();

		b.append(jcas.getDocumentText().substring(selection.begin, selection.end));

		Annotation spanAnnotation = new Annotation(jcas);
		spanAnnotation.setBegin(selection.begin);
		spanAnnotation.setEnd(selection.end);
		ImmutableList<Mention> mentions = Lists.immutable
				.withAll(JCasUtil.selectCovered(Mention.class, spanAnnotation));

		AnnotationComparator annoComp = new AnnotationComparator();
		annoComp.setDescending(true);
		annoComp.setUseEnd(true);
		mentions = mentions.toSortedList(annoComp).toImmutable();
		for (Mention m : mentions) {
			b.insert(m.getEnd() - selection.begin, "]");
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);

	}

	static class Position {
		enum PositionType {
			begin, end
		};

		int position;

		Mention source;
	}
}
