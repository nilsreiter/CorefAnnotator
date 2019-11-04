package de.unistuttgart.ims.coref.annotator.plugin.quadrama.tei;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;

public class MapCorefToXmlElements extends JCasAnnotator_ImplBase {

	/**
	 * Pattern to extract xml ids from a string representation of the xml attributes
	 */
	Pattern xmlIdPattern = Pattern.compile("xml:id=\"([^\"]+)\"");

	/**
	 * This will be used to ensure that ids are unique within a document
	 */
	MutableSet<String> ids = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ids = Sets.mutable.empty();
		ids.add("e");

		// map from xml ids to elements
		MutableMap<String, XMLElement> idMap = Maps.mutable.empty();

		// contains all covering XMLElement annotations for each mention
		Map<Mention, Collection<XMLElement>> coveringXMLElement = JCasUtil.indexCovering(jcas, Mention.class,
				XMLElement.class);

		// contains covering Speaker annotations for each mention
		Map<Mention, Collection<Speaker>> coveringSpeaker = JCasUtil.indexCovering(jcas, Mention.class, Speaker.class);

		// the <text>-element
		XMLElement textElement = null;
		XMLElement teiHeaderElement = null;

		MutableList<XMLElement> spElements = Lists.mutable.empty();

		for (XMLElement xmlElement : JCasUtil.select(jcas, XMLElement.class)) {

			// if the xmlElement has an xml:id attribute
			Matcher m = xmlIdPattern.matcher(xmlElement.getAttributes());
			if (m.find()) {
				String id = m.group(1);

				// store mapping of ids to elements
				idMap.put(id, xmlElement);
			}

			// identify <text>-element
			// we assume it's unique
			if (xmlElement.getTag().equalsIgnoreCase("text"))
				textElement = xmlElement;

			// identify <teiHeader>-element
			if (xmlElement.getTag().equalsIgnoreCase("teiHeader"))
				teiHeaderElement = xmlElement;

			// scrub all who= attributes in <sp>-elements
			if (xmlElement.getTag().equalsIgnoreCase("sp") && xmlElement.getAttributes().contains("who=")) {
				xmlElement.setAttributes(
						Pattern.compile("who=\"[^\"]*\"").matcher(xmlElement.getAttributes()).replaceFirst(""));
				spElements.add(xmlElement);
			}

		}

		// handle <sp>-elements separately
		for (XMLElement spElement : spElements) {

			// we first collect all mentions that designate speaker tags
			MutableSet<Mention> speakerMentions = Sets.mutable.empty();
			for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, spElement)) {
				speakerMentions.addAll(JCasUtil.selectCovered(Mention.class, speaker));
			}
			if (speakerMentions.isEmpty())
				continue;

			// generate the new string for the who attribute
			String newAttributeString = spElement.getAttributes();
			if (!(newAttributeString.isEmpty() || newAttributeString.endsWith(" ")))
				newAttributeString += " ";
			newAttributeString += "who=\"" + speakerMentions.collect(m -> "#" + toXmlId(m.getEntity())).makeString(" ")
					+ "\"";

			// add id to the xml element
			spElement.setAttributes(newAttributeString);
		}

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {

			// we skip all mentions not in the text
			if (!coveringXMLElement.get(m).contains(textElement))
				continue;

			// we skip mentions that are covered by speaker annotations
			if (coveringSpeaker.containsKey(m))
				continue;

			// get entity
			Entity e = m.getEntity();

			// create xml id
			String xid = toXmlId(e);

			// create new element annotation
			XMLElement newElement = AnnotationFactory.createAnnotation(jcas, m.getBegin(), m.getEnd(),
					XMLElement.class);
			newElement.setTag("rs");
			newElement.setAttributes(" ref=\"#" + xid + "\"");
		}
	}

	/**
	 * This method takes an entity object and creates a valid XML id from the
	 * entity's label
	 * 
	 * @param entity The entity
	 * @return A string containing a valid XML id
	 */
	String toXmlId(Entity entity) {
		String id = null;

		if (entity.getXmlId() != null) {
			id = entity.getXmlId();
			ids.add(id);
			return id;
		} else {

			String baseId;
			if (entity.getLabel() != null) {
				baseId = entity.getLabel().toLowerCase().replaceAll("[^a-z]", "_");
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
