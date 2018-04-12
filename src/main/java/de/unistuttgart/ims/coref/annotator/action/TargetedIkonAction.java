package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

public abstract class TargetedIkonAction<T> extends IkonAction {

	private static final long serialVersionUID = 1L;

	T target;

	public TargetedIkonAction(T dw, String stringKey, Ikon... ikon) {
		super(stringKey, ikon);
		this.target = dw;
	}

	public TargetedIkonAction(T dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(stringKey, isKey, ikon);
		this.target = dw;
	}

	public TargetedIkonAction(T dw, Ikon ikon) {
		super(ikon);
		this.target = dw;
	}

	public T getTarget() {
		return target;
	}

}
