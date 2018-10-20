package de.unistuttgart.ims.coref.annotator.tools;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface Options {
	@Option
	File getInput();

	@Option
	File getOutput();

	@Option
	Fixer.Fix getFix();

	@Option(defaultValue = "de")
	String getLanguage();
}
