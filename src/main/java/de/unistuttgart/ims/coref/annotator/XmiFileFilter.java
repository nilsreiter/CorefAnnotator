package de.unistuttgart.ims.coref.annotator;

import java.io.File;

import javax.swing.filechooser.FileFilter;

@Deprecated
public class XmiFileFilter extends FileFilter {
	public static XmiFileFilter filter = new XmiFileFilter();

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().endsWith(".xmi");
	}

	@Override
	public String getDescription() {
		return "UIMA Xmi Files";
	}
}
