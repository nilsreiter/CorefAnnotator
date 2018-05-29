package de.unistuttgart.ims.coref.annotator.plugin.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.plugins.SingleFileWriter;

public class JSONWriter extends SingleFileWriter {

	List<Entity> entityList = new LinkedList<Entity>();

	@Override
	public void write(JCas jcas, Writer os) throws IOException {
		JSONArray array = new JSONArray();

		Map<Token, Collection<Mention>> indexed = JCasUtil.indexCovering(jcas, Token.class, Mention.class);

		for (Token token : JCasUtil.select(jcas, Token.class)) {
			JSONObject o = new JSONObject();
			o.put("s", token.getCoveredText());
			if (indexed.containsKey(token)) {
				JSONArray entities = new JSONArray();
				for (Mention mention : indexed.get(token)) {
					entities.put(getEntityId(mention));
				}
				o.put("e", entities);
			}
			array.put(o);
		}
		os.write(array.toString());
	}

	protected int getEntityId(Mention m) {
		if (!entityList.contains(m.getEntity()))
			entityList.add(m.getEntity());
		return entityList.indexOf(m.getEntity());
	}

}
