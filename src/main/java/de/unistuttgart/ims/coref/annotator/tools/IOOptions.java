package de.unistuttgart.ims.coref.annotator.tools;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface IOOptions {
	@Option
	File getInput();

	@Option(defaultToNull = true)
	File getOutput();

}
