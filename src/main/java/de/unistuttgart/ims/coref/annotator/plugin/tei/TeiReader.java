package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.jsoup.nodes.Element;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CompressionUtils;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;

public class TeiReader extends ResourceCollectionReaderBase {

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

		GenericXmlReader<DocumentMetaData> gxr = new GenericXmlReader<DocumentMetaData>(DocumentMetaData.class);
		gxr.setTextRootSelector("TEI");
		gxr.setPreserveWhitespace(true);

		gxr.addGlobalRule("bibl[type=digitalSource] > idno[type=URL]", (d, e) -> d.setDocumentId(e.text()));

		gxr.addGlobalRule("[xml:id]", Entity.class, (cf, e) -> {
			cf.setLabel(e.attr("xml:id"));
			cf.setColor(colorProvider.getNextColor().getRGB());
			entityMap.put(e.attr("xml:id"), cf);
		});

		// segmentation
		gxr.addRule("[ref]", Mention.class, (m, e) -> {
			String id = e.attr("ref").substring(1);
			Entity entity = entityMap.get(id);
			if (entity == null) {
				entity = new Entity(jcas);
				entity.addToIndexes();
				entity.setColor(colorProvider.getNextColor().getRGB());
				entity.setLabel(m.getCoveredText());
				entityMap.put(id, entity);
			}
			m.setEntity(entity);
		});
		gxr.addRule("speaker", Mention.class, (m, e) -> {
			Element parent = e.parent();
			if (parent.hasAttr("who")) {
				String id = parent.attr("who").substring(1);
				Entity entity = entityMap.get(id);
				if (entity == null) {
					entity = new Entity(jcas);
					entity.addToIndexes();
					entity.setLabel(m.getCoveredText());
					entity.setColor(colorProvider.getNextColor().getRGB());
					entityMap.put(id, entity);
				}
				m.setEntity(entity);
			}
		});

		Resource res = nextFile();

		// Read XMI file
		try (InputStream is = CompressionUtils.getInputStream(res.getLocation(), res.getInputStream())) {
			gxr.read(jcas, is);
		} catch (IOException e1) {
			Annotator.logger.catching(e1);
		}

	}

}