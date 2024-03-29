package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.dkpro.core.api.resources.CompressionUtils;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.api.format.Bold;
import de.unistuttgart.ims.coref.annotator.api.format.Head;
import de.unistuttgart.ims.coref.annotator.api.format.Italic;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Line;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.api.v2.Segment;
import de.unistuttgart.ims.coref.annotator.api.v2.tei.TEIBody;
import de.unistuttgart.ims.coref.annotator.api.v2.tei.TEIHeader;
import de.unistuttgart.ims.coref.annotator.api.v2.tei.TEIText;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;

public class TeiReader extends ResourceCollectionReaderBase {

	public static final String PARAM_DOCUMENT_ID = "Document Id";
	public static final String PARAM_TEXT_ROOT_SELECTOR = "Root Selector";

	@ConfigurationParameter(name = PARAM_DOCUMENT_ID, mandatory = true)
	String documentId = null;

	@ConfigurationParameter(name = PARAM_TEXT_ROOT_SELECTOR, mandatory = false, defaultValue = "")
	String rootSelector = null;

	@Override
	public void getNext(CAS aCAS) {
		ColorProvider colorProvider = new ColorProvider();

		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e2) {
			Annotator.logger.catching(e2);
			return;
		}

		MutableMap<String, Entity> entityMap = Maps.mutable.empty();
		MutableMap<String, Mention> mentionMap = Maps.mutable.empty();

		GenericXmlReader<DocumentMetaData> gxr = new GenericXmlReader<DocumentMetaData>(DocumentMetaData.class);
		gxr.setTextRootSelector(rootSelector.isEmpty() ? null : rootSelector);
		gxr.setPreserveWhitespace(true);

		// set the document title
		gxr.addGlobalRule("titleStmt > title", (d, e) -> d.setDocumentTitle(e.text()));

		if (jcas.getDocumentLanguage().equalsIgnoreCase(Constants.X_UNSPECIFIED))
			gxr.addGlobalRule("langUsage[usage=100]", (d, e) -> jcas.setDocumentLanguage(e.attr("ident")));

		gxr.addRule("[ref]", MentionSurface.class, (ms, e) -> {
			// retrieve mention id
			String mentionId = null;
			if (e.hasAttr("prev"))
				mentionId = e.attr("prev");
			else if (e.hasAttr("id"))
				mentionId = e.attr("id");

			// create or retrieve mention
			Mention m = null;
			if (mentionId != null)
				m = mentionMap.get(mentionId);
			if (m == null) {
				m = new Mention(jcas);
				m.addToIndexes();
				m.setSurface(new FSArray<MentionSurface>(jcas, 0));
				mentionMap.put(mentionId, m);
			}
			ms.setMention(m);
			m.setSurface(UimaUtil.addTo(jcas, m.getSurface(), ms));

			// retrieve entity id
			String entityId = e.attr("ref").substring(1);

			// create or retrieve entity
			Entity entity = entityMap.get(entityId);
			if (entity == null) {
				entity = new Entity(jcas);
				entity.addToIndexes();
				entity.setColor(colorProvider.getNextColor().getRGB());
				// TODO: read old label from XML
				entity.setLabel(UimaUtil.getCoveredText(m));
				entity.setXmlId(entityId);
				entityMap.put(entityId, entity);
			}
			m.setEntity(entity);
		});

		gxr.addRule("head", Head.class);
		gxr.addRule("emph", Italic.class);
		gxr.addRule("[rend*=bold]", Bold.class);
		gxr.addRule("[rend*=italic]", Italic.class);
		gxr.addRule("lg", Segment.class);
		gxr.addRule("div", Segment.class, (s, e) -> {
			if (e.selectFirst("head") != null)
				s.setLabel(e.selectFirst("head").text());
		});
		gxr.addRule("l", Line.class,
				(line, element) -> line.setNumber(element.hasAttr("n") ? Integer.valueOf(element.attr("n")) : -1));

		gxr.addRule("TEI > text", TEIText.class);
		gxr.addRule("TEI > teiHeader", TEIHeader.class);
		gxr.addRule("TEI > text > body", TEIBody.class);

		Resource res = nextFile();

		// Read XMI file
		try (InputStream is = CompressionUtils.getInputStream(res.getLocation(), res.getInputStream())) {
			gxr.read(jcas, is);
		} catch (IOException e1) {
			Annotator.logger.catching(e1);
		}

		if (JCasUtil.exists(jcas, DocumentMetaData.class))
			DocumentMetaData.get(jcas).setDocumentId(documentId);
		else
			DocumentMetaData.create(jcas).setDocumentId(documentId);

		if (jcas.getDocumentLanguage().equalsIgnoreCase(Constants.X_UNSPECIFIED))
			jcas.setDocumentLanguage(getLanguage());

		UimaUtil.getMeta(jcas).setStylePlugin(TeiStylePlugin.class.getName());
		UimaUtil.getMeta(jcas).setTypeSystemVersion(TypeSystemVersion.getCurrent().toString());

		for (XMLElement element : Sets.immutable.withAll(JCasUtil.select(jcas, XMLElement.class))) {
			if (element.getTag().equalsIgnoreCase("rs"))
				element.removeFromIndexes();
		}
	}

}
