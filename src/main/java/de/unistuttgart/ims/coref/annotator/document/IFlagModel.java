package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public interface IFlagModel {

	ImmutableList<Flag> getFlags();

	Class<?> getTargetClass(Flag f) throws ClassNotFoundException;

	String getLocalizedLabel(Flag f);

	String getLabel(Flag f);

	Ikon getIkon(Flag f);

	boolean addFlagModelListener(FlagModelListener e);

	boolean removeFlagModelListener(Object o);

	void updateFlag(Flag flag);

	Flag getFlag(String key);

}