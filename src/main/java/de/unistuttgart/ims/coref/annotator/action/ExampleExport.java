package de.unistuttgart.ims.coref.annotator.action;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.uima.io.xml.GenericInlineWriter;
import de.unistuttgart.ims.uima.io.xml.InlineTagFactory;

public class ExampleExport extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public static enum Format {
		MARKDOWN, PLAINTEXT
	};

	Format format;

	public ExampleExport(DocumentWindow dw, Format format) {
		super(dw, Annotator.getString("format." + format.toString()), false);
		this.format = format;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Span selection = getTarget().getSelection();
		JCas jcas = getTarget().getDocumentModel().getJcas();

		GenericInlineWriter<Mention> giw = new GenericInlineWriter<Mention>(Mention.class);
		switch (format) {
		case PLAINTEXT:
			giw.setTagFactory(new PlainTextTagFactory());
			break;
		default:
			giw.setTagFactory(new MarkdownTagFactory());
		}

		ByteArrayOutputStream boas = new ByteArrayOutputStream();

		giw.write(jcas, boas, selection.begin, selection.end);

		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(boas.toString("UTF-8")),
					null);
		} catch (HeadlessException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

	}

	static class Position {
		enum PositionType {
			begin, end
		};

		int position;

		Mention source;
	}

	class PlainTextTagFactory implements InlineTagFactory<Mention> {
		MutableList<Entity> entityList = Lists.mutable.empty();

		@Override
		public String getBeginTag(Mention anno) {
			return "[";
		}

		@Override
		public String getEndTag(Mention anno) {
			if (!entityList.contains(anno.getEntity()))
				entityList.add(anno.getEntity());
			return "]" + entityList.indexOf(anno.getEntity());
		}

		@Override
		public String getEmptyTag(Mention anno) {
			return "";
		}

	}

	class MarkdownTagFactory implements InlineTagFactory<Mention> {

		MutableList<Entity> entityList = Lists.mutable.empty();

		@Override
		public String getBeginTag(Mention anno) {
			return "[";
		}

		@Override
		public String getEndTag(Mention anno) {
			if (!entityList.contains(anno.getEntity()))
				entityList.add(anno.getEntity());
			return "]<sub>" + entityList.indexOf(anno.getEntity()) + "</sub>";
		}

		@Override
		public String getEmptyTag(Mention anno) {
			return "";
		}

	}
}
