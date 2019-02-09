package de.unistuttgart.ims.coref.annotator.document.adapter;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;
import de.unistuttgart.ims.coref.annotator.document.FlagModelListener;

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
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Flag) {
					Flag flag = (Flag) event.getArgument(i);
					try {
						@SuppressWarnings("unchecked")
						Class<? extends FeatureStructure> cl = (Class<? extends FeatureStructure>) Class
								.forName(flag.getTargetClass());
						if (cl.isAssignableFrom(targetClass)) {
							if (!entityList.contains(flag)) {
								entityList.add(flag);
								fireIntervalAdded(this, entityList.size(), entityList.size() + 1);
							} else {
								fireContentsChanged(this, entityList.indexOf(flag), entityList.indexOf(flag) + 1);
							}
						} else {
							int index = entityList.indexOf(event.getArgument(i));
							if (index >= 0) {
								entityList.remove(index);
								fireIntervalRemoved(this, index, index);
							}
						}
					} catch (ClassNotFoundException e) {
						Annotator.logger.catching(e);
					}

				}
			}
			break;
		case Add:
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Flag) {
					Flag flag = (Flag) event.getArgument(i);
					try {
						@SuppressWarnings("unchecked")
						Class<? extends FeatureStructure> cl = (Class<? extends FeatureStructure>) Class
								.forName(flag.getTargetClass());
						if (cl.isAssignableFrom(targetClass) && !entityList.contains(flag)) {
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
				if (event.getArgument(i) instanceof Flag) {
					int index = entityList.indexOf(event.getArgument(i));
					if (index != 0) {
						entityList.remove(index);
						fireIntervalRemoved(this, index, index + 1);
					}
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
