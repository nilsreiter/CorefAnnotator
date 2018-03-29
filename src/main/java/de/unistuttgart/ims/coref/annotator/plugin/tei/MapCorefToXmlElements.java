package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;

public class MapCorefToXmlElements extends JCasAnnotator_ImplBase {

	Pattern pattern = Pattern.compile("xml:id=\"([^\"]+)\"");

	MutableSet<String> ids = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ids = Sets.mutable.empty();
		ids.add("e");
		MutableMap<String, XMLElement> idMap = Maps.mutable.empty();

		for (XMLElement xmlElement : JCasUtil.select(jcas, XMLElement.class)) {
			Matcher m = pattern.matcher(xmlElement.getAttributes());
			if (m.find()) {
				String id = m.group(1);
				idMap.put(id, xmlElement);
			}
		}

		// TODO: Handle who= elements
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			Entity e = m.getEntity();
			String xid = toXmlId(e);
			XMLElement newElement = AnnotationFactory.createAnnotation(jcas, m.getBegin(), m.getEnd(),
					XMLElement.class);
			newElement.setTag("rs");
			if (idMap.containsKey(xid)) {
				newElement.setAttributes(" ref=\"#" + xid + "\"");
			} else {
				newElement.setAttributes(" xml:id=\"" + xid + "\"");
				idMap.put(xid, newElement);
			}
		}
	}

	String toXmlId(Entity entity) {
		String id = null;

		if (entity.getXmlId() != null) {
			id = entity.getXmlId();
			ids.add(id);
			return id;
		} else {

			String baseId;
			if (entity.getLabel() != null) {
				baseId = entity.getLabel().toLowerCase().replaceAll("[^a-z]", "-");
			} else {
				baseId = "e";
			}
			if (ids.contains(baseId)) {
				int counter = (baseId == "e" ? 0 : 1);
				do {
					counter++;
					id = baseId + String.valueOf(counter);
				} while (ids.contains(id));
				ids.add(id);
				entity.setXmlId(id);
				return id;
			} else {
				ids.add(baseId);
				entity.setXmlId(baseId);
				return baseId;
			}
		}
	}

}
