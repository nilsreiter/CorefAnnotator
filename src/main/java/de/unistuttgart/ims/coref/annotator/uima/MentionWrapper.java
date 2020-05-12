package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import org.jsoup.internal.StringUtil;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class MentionWrapper implements DiscontinuousAnnotation<MentionSurface, Mention> {

	Mention mention;

	public MentionWrapper(Mention mention) {
		this.mention = mention;
	}

	@Override
	public int getBegin() {
		return getFirst().getBegin();
	}

	@Override
	public int getEnd() {
		return getLast().getEnd();
	}

	@Override
	public FSArray<MentionSurface> getSurface() {
		return mention.getSurface();
	}

	@Override
	public MentionSurface getSurface(int i) {
		return mention.getSurface(i);
	}

	@Override
	public MentionSurface getFirst() {
		return mention.getSurface(0);
	}

	@Override
	public MentionSurface getLast() {
		return mention.getSurface(mention.getSurface().size() - 1);
	}

	@Override
	public String getCoveredText() {
		return StringUtil.join(JCasUtil.toText(mention.getSurface()), ",");
	}

}
