package de.unistuttgart.ims.coref.annotator.tools;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface InputOutputOptions {
	@Option
	File getInput();

	@Option
	File getOutput();
}
