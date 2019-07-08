package de.unistuttgart.ims.coref.annotator.tools;

import com.lexicalscope.jewel.cli.Option;

public interface FixerOptions extends InputOutputOptions {

	@Option
	Fixer.Fix getFix();

	@Option(defaultValue = "de")
	String getLanguage();
}
