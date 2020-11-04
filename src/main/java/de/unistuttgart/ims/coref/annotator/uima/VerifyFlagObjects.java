package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class VerifyFlagObjects extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		MutableSet<Flag> flags = Sets.mutable.ofAll(JCasUtil.select(jcas, Flag.class));

		for (Entity entity : JCasUtil.select(jcas, Entity.class)) {
			if (entity.getFlags() != null)
				for (String flagKey : entity.getFlags()) {
					boolean ok = false;
					for (Flag flag : flags) {
						if (flag.getKey().equalsIgnoreCase(flagKey)
								&& flag.getTargetClass().equalsIgnoreCase(Entity.class.getName())) {
							ok = true;
						}
					}
					if (!ok) {
						Flag flag = new Flag(jcas);
						flag.setKey(flagKey);
						flag.setIcon(MaterialDesign.MDI_FLAG.name());
						flag.setTargetClass(Entity.class.getName());
						flag.setLabel(flagKey);
						flag.addToIndexes();
						flags.add(flag);
					}
				}
		}

		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			if (mention.getFlags() != null)
				for (String flagKey : mention.getFlags()) {
					boolean ok = false;
					for (Flag flag : flags) {
						if (flag.getKey().equalsIgnoreCase(flagKey)
								&& flag.getTargetClass().equalsIgnoreCase(Mention.class.getName())) {
							ok = true;
						}
					}
					if (!ok) {
						Flag flag = new Flag(jcas);
						flag.setKey(flagKey);
						flag.setIcon(MaterialDesign.MDI_FLAG.name());
						flag.setTargetClass(Mention.class.getName());
						flag.setLabel(flagKey);
						flag.addToIndexes();
						flags.add(flag);
					}
				}
		}

	}

}
