package de.unistuttgart.ims.coref.annotator.analyzer;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Mention extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	Iterable<Entity> entities = null;

	public AnalyzerActionPanel_Mention(DocumentModel documentModel, Iterable<Entity> entities) {
		super(documentModel, entities);

		init();

		setEntities(entities);
	}

	@Override
	public AnalyzerActionPanel.ACTION getType() {
		return AnalyzerActionPanel.ACTION.MENTION;
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {
		this.entities = entities;
		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));

		MutableMapIterable<String, Integer> cts = mentions.countBy(m -> m.getCoveredText()).toMapOfItemToCount();
		setFullData(cts);
	}

}
