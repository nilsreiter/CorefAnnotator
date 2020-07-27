package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
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

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			Entity e = m.getEntity();
			String xid = toXmlId(e);
			boolean first = true;
			String mentionId = UUID.randomUUID().toString();
			for (MentionSurface ms : m.getSurface()) {
				XMLElement newElement = AnnotationFactory.createAnnotation(jcas, ms.getBegin(), ms.getEnd(),
						XMLElement.class);
				newElement.setTag("rs");

				StringBuilder b = new StringBuilder();
				b.append(' ').append("ref=\"#").append(xid).append('"');
				if (first) {
					if (m.getSurface().size() > 1)
						b.append(" id=\"").append(mentionId).append('"');
					if (m.getFlags() != null && !m.getFlags().isEmpty())
						b.append(" ana=\"")
								.append(StringUtils
										.join(Lists.immutable.withAll(m.getFlags()).collect(f -> f.getUuid()), ","))
								.append('"');
					first = false;
				} else {
					b.append(' ').append("prev=\"").append(mentionId).append("\"");
				}
				newElement.setAttributes(b.toString());
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
				// TODO: Really check whether this is a legal NCNAME
				baseId = entity.getLabel().replaceAll("\\s", "-");
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
