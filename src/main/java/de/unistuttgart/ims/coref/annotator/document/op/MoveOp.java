package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public abstract class MoveOp<M extends FeatureStructure, C extends FeatureStructure>
		implements CoreferenceModelOperation {
	ImmutableList<M> objects;
	C source;
	C target;

	public MoveOp(C source, C target, Iterable<M> mention) {
		this.objects = Lists.immutable.withAll(mention);
		this.source = source;
		this.target = target;
	}

	@SafeVarargs
	public MoveOp(C source, C target, M... mention) {
		this.objects = Lists.immutable.of(mention);
		this.source = source;
		this.target = target;
	}

	public ImmutableList<M> getObjects() {
		return objects;
	}

	public void setObjects(ImmutableList<M> objects) {
		this.objects = objects;
	}

	public C getSource() {
		return source;
	}

	public void setSource(C source) {
		this.source = source;
	}

	public C getTarget() {
		return target;
	}

	public void setTarget(C target) {
		this.target = target;
	}

	public FeatureStructureEvent toEvent() {
		return Event.get(Event.Type.Move, getSource(), getTarget(), getObjects());
	}

	public FeatureStructureEvent toReversedEvent() {
		return Event.get(Event.Type.Move, getTarget(), getSource(), getObjects());
	}
}