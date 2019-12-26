package de.unistuttgart.ims.coref.annotator.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.coref.annotator.PluginManager;
import de.unistuttgart.ims.coref.annotator.plugins.UimaIOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.ExportWorker;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

/**
 * This class can be used to export multiple files into export formats. It does
 * not show a GUI. It can be launched on the command line with <code>
 * java -cp CorefAnnotator-VERSION-full.jar de.unistuttgart.ims.coref.annotator.tools.ExportMultipleFiles
 * </code>. Options are displayed using the <code>--help</code> key.
 * 
 * @author reiterns
 * @since 1.12
 *
 */
public class ExportMultipleFiles {
	static Options options;

	static UimaIOPlugin outputPlugin;

	static Pattern filenamePattern = Pattern.compile("^(.*)\\.xmi(\\.gz)?");

	static PluginManager pluginManager;

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
			throws ResourceInitializationException, ClassNotFoundException, InterruptedException, ExecutionException {
		// no GUI here
		System.setProperty("java.awt.headless", "true");

		// parse options
		try {
			options = CliFactory.parseArguments(Options.class, args);
		} catch (ArgumentValidationException e) {
			System.out.println(e.getLocalizedMessage());
			System.exit(1);
		}

		// set some static properties
		pluginManager = new PluginManager();
		outputPlugin = pluginManager.getIOPlugin((Class<? extends UimaIOPlugin>) Class
				.forName("de.unistuttgart.ims.coref.annotator.plugin." + options.getOutputFormat().name() + ".Plugin"));

		// iterate over the input files or directories
		for (File file : options.getInput()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pluginManager.getDefaultIOPlugin().getFileFilter().accept(pathname);
					}

				})) {
					convertFile(f, options);
				}
			} else
				convertFile(file, options);
		}
	}

	/**
	 * This function processes a single file.
	 * 
	 * @param file    The file to process.
	 * @param options The options class.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void convertFile(File file, Options options) throws InterruptedException, ExecutionException {

		// extract core file name
		String inputFilename = file.getName();
		Matcher m = filenamePattern.matcher(inputFilename);
		String namePart = null;
		if (m.find()) {
			namePart = m.group(1);
		}

		// load jcas from file
		JCasLoader loader = new JCasLoader(file, pluginManager.getDefaultIOPlugin(), "xx-unspecified", null, ex -> {
			ex.printStackTrace();
		});
		loader.execute();
		JCas jcas = loader.get();

		// generate output file
		File targetFile = new File(options.getOutputDirectory(),
				(namePart == null ? DocumentMetaData.get(jcas).getDocumentTitle() : namePart)
						+ outputPlugin.getSuffix());

		// write jcas to output file
		ExportWorker w = new ExportWorker(targetFile, jcas, outputPlugin, (f, j) -> {
			System.out.println(f.getAbsolutePath() + ": done.");
		});
		w.execute();
		w.get();
	}

	public enum OutputFormat {
		tei, conll2012, json
	}

	public enum OutputFilename {
		input, documentId
	}

	@CommandLineInterface(application = "ExportMultipleFiles")
	public interface Options {
		@Option(description = "Input file or directory.", shortName = "i")
		List<File> getInput();

		@Option(defaultValue = "tei", description = "Target format. One of [tei, conll2012, json].")
		OutputFormat getOutputFormat();

		@Option(defaultValue = ".", description = "Output directory. Defaults to current.", shortName = "o")
		File getOutputDirectory();

		@Option(defaultValue = "input", description = "Output filename origin. One of [input, documentId].")
		OutputFilename getOutputFilename();

		@Option(helpRequest = true, shortName = "h", description = "Show help")
		boolean getHelp();
	}
}
