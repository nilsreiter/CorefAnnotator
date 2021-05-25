package de.unistuttgart.ims.coref.annotator;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilters {
	static public FileFilter ca2 = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".ca2") || f.getName().endsWith(".ca2z");
		}

		@Override
		public String getDescription() {
			return "CorefAnnotator";
		}
	};
	static public FileFilter xmi = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xmi");
		}

		@Override
		public String getDescription() {
			return "UIMA Xmi Files";
		}
	};

	static public FileFilter xmi_gz = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xmi") || f.getName().endsWith(".xmi.gz");
		}

		@Override
		public String getDescription() {
			return "UIMA Xmi Files (Compressed/Uncompressed)";
		}
	};

	static public FileFilter txt = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".txt");
		}

		@Override
		public String getDescription() {
			return "Plain text files";
		}

	};

	static public FileFilter xml = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "XML files";
		}

	};

	static public FileFilter tei = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".tei") || f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "TEI/XML files";
		}

	};

	static public FileFilter csv = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".csv");
		}

		@Override
		public String getDescription() {
			return "CSV files";
		}

	};

	static public FileFilter xmi_zip = new FileFilter() {

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xmi.zip");
		}

		@Override
		public String getDescription() {
			return "Zipped UIMA XMI files";
		}

	};
}
