package de.unistuttgart.ims.coref.annotator.tools;

import java.io.File;
import java.util.List;

import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.ExportWorker;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class ExportMultipleFiles {
	static Options options;

	static int doneCounter;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ResourceInitializationException, ClassNotFoundException {
		options = CliFactory.parseArguments(Options.class, args);

		doneCounter = options.getInput().size();
		Annotator a = new Annotator();

		// de.unistuttgart.ims.coref.annotator.plugin.tei
		IOPlugin plugin = a.getPluginManager().getIOPlugin((Class<? extends IOPlugin>) Class
				.forName("de.unistuttgart.ims.coref.annotator.plugin." + options.getOutputFormat().name() + ".Plugin"));

		for (File file : options.getInput()) {

			JCasLoader loader = new JCasLoader(file, a.getPluginManager().getDefaultIOPlugin(), "xx-unspecified",
					jcas -> {
						File targetFile = new File(options.getOutputDirectory(),
								DocumentMetaData.get(jcas).getDocumentTitle() + ".xml");
						ExportWorker w = new ExportWorker(targetFile, jcas, plugin, (f, j) -> {
							System.out.println(f.getAbsolutePath() + ": done.");
							doneCounter--;
							exit();
						});
						w.execute();
					}, ex -> {
						ex.printStackTrace();
					});
			loader.execute();
		}
	}

	public static void exit() {
		if (doneCounter == 0)
			System.exit(0);
	}

	public enum OutputFormat {
		tei, plaintext
	}

	public interface Options {
		@Option
		List<File> getInput();

		@Option
		OutputFormat getOutputFormat();

		@Option(defaultValue = ".")
		File getOutputDirectory();
	}
}
