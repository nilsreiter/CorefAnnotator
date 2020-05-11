package de.unistuttgart.ims.coref.annotator.uima;

import java.util.Comparator;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class MentionComparator implements Comparator<Mention> {

	boolean useEnd = false;
	boolean descending = false;

	public MentionComparator() {
	}

	public MentionComparator(boolean useEnd) {
		this.useEnd = useEnd;
	}

	@Override
	public int compare(Mention o1, Mention o2) {
		if (useEnd)
			return (descending ? -1 : 1) * Integer.compare(UimaUtil.getEnd(o1), UimaUtil.getEnd(o2));
		else {
			int returnValue = UimaUtil.compare(o1, o2);
			return (descending ? -1 : 1) * returnValue;
		}
	}

	public boolean isUseEnd() {
		return useEnd;
	}

	public void setUseEnd(boolean useEnd) {
		this.useEnd = useEnd;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}

}
