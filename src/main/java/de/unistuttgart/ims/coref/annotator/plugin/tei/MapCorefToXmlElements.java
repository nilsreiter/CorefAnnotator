package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;

public class MapCorefToXmlElements extends JCasAnnotator_ImplBase {

	Pattern pattern = Pattern.compile("xml:id=\"([^\"]+)\"");

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

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
			String xid = e.getXmlId();
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

}
