package de.unistuttgart.ims.coref.annotator.plugin.quadrama.tei;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

public class TestRoundTrip {

	public void testUnedited() throws Exception {

		String tFilename = "target/Sara-generated.xml";

		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(TeiReader.class,
				TeiReader.PARAM_SOURCE_LOCATION, getClass().getResource("/tei/Sara.xml").toString(),
				TeiReader.PARAM_DOCUMENT_ID, "test");

		SimplePipeline.runPipeline(crd, AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class),
				AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE, tFilename));

		String given = IOUtils.toString(getClass().getResourceAsStream("/tei/Sara.xml"), "UTF-8");
		@SuppressWarnings("resource")
		String generated = IOUtils.toString(new FileInputStream(new File(tFilename)), "UTF-8");

		// assertEquals(given, generated);

	}

}
