package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class FlagComboBoxModel extends AbstractComboBoxModel<Flag> implements FlagModelListener {

	private static final long serialVersionUID = 1L;
	Class<? extends FeatureStructure> targetClass;

	public FlagComboBoxModel(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		switch (event.getType()) {
		case Update:
		case Add:
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Flag) {
					Flag flag = (Flag) event.getArgument(i);
					try {
						@SuppressWarnings("unchecked")
						Class<? extends FeatureStructure> cl = (Class<? extends FeatureStructure>) Class
								.forName(flag.getTargetClass());
						if (cl.isAssignableFrom(targetClass)) {
							entityList.add((Flag) event.getArgument(i));
							fireIntervalAdded(this, entityList.size(), entityList.size() + 1);
						}
					} catch (ClassNotFoundException e) {
						Annotator.logger.catching(e);
					}
				}
			}
			break;
		case Merge:
			break;
		case Remove:
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity) {
					int index = entityList.indexOf(event.getArgument(i));
					fireIntervalRemoved(this, index, index);
				}
			}
			break;
		case Init:
			entityList = Lists.mutable.withAll(((FlagModel) event.getSource()).getFlags(targetClass));
			selectedItem = null;
			break;
		default:
			break;

		}
	}

}
