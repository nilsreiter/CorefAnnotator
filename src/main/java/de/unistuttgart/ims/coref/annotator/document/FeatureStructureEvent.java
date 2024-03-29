package de.unistuttgart.ims.coref.annotator.document;

import java.util.Iterator;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.document.op.Operation;

public class FeatureStructureEvent implements Event {
	ImmutableList<FeatureStructure> arguments;
	Type eventType;
	Object source;

	public FeatureStructureEvent(Object source, Type eventType, FeatureStructure... args) {
		this.eventType = eventType;
		this.source = source;
		this.arguments = Lists.immutable.of(args);
	}

	public FeatureStructureEvent(Object source, Type eventType, Iterable<? extends FeatureStructure> args) {
		this.eventType = eventType;
		this.source = source;
		this.arguments = Lists.immutable.withAll(args);

	}

	@Override
	public Type getType() {
		return eventType;
	}

	public FeatureStructure getArgument1() {
		return arguments.get(0);
	}

	public void setType(Type eventType) {
		this.eventType = eventType;
	}

	@Override
	public Operation getOp() {
		return null;
	}

	public FeatureStructure getArgument2() {
		return arguments.get(1);
	}

	public FeatureStructure getArgument(int i) {
		return this.arguments.get(i);
	}

	@Override
	public int getArity() {
		return arguments.size();

	}

	@Override
	public Iterator<FeatureStructure> iterator() {
		return arguments.iterator();
	}

	public Iterator<FeatureStructure> iterator(int start) {
		return iterable(start).iterator();
	}

	public Iterable<FeatureStructure> iterable(int start) {
		return arguments.subList(1, arguments.size());
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return getSource().toString() + " " + eventType + ": " + arguments.toString();
	}

	public ImmutableList<FeatureStructure> getArguments() {
		return arguments;
	}
}
