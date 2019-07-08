package de.unistuttgart.ims.coref.annotator.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import com.lexicalscope.jewel.cli.CliFactory;

import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;

public class Fixer {
	static FixerOptions options;

	public static enum Fix {
		None, RemoveCarriageReturn
	}

	public static void main(String[] args)
			throws ResourceInitializationException, InvocationTargetException, InterruptedException {
		options = CliFactory.parseArguments(FixerOptions.class, args);

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				switch (options.getFix()) {
				case RemoveCarriageReturn:
					JCasLoader worker = new JCasLoader(options.getInput(), new DefaultIOPlugin(), options.getLanguage(),
							new RemoveCarriageReturn(), null);
					worker.execute();
					break;
				default:
				}
			}

		});

	}

	static class RemoveCarriageReturn implements Consumer<JCas> {

		@Override
		public void accept(JCas jcas1) {
			try {

				MutableMultimap<Integer, Annotation> beginMap = Multimaps.mutable.set.empty();
				MutableMultimap<Integer, Annotation> endMap = Multimaps.mutable.set.empty();

				for (Annotation a : JCasUtil.select(jcas1, Annotation.class)) {
					beginMap.put(a.getBegin(), a);
					endMap.put(a.getEnd(), a);
				}

				String text1 = jcas1.getDocumentText();
				String text2 = text1.replaceAll("\r", "");

				JCas jcas2 = JCasFactory.createJCas();
				jcas2.setDocumentText(text2);

				char[] text1c = text1.toCharArray();
				for (int i = 0; i < text1c.length; i++) {
					if (text1c[i] == '\r') {
						for (int j = i; j < text1c.length; j++) {
							for (Annotation a : beginMap.get(j))
								a.setBegin(a.getBegin() - 1);
							for (Annotation a : endMap.get(j))
								a.setEnd(a.getEnd() - 1);
						}
					}
				}
				CasCopier.copyCas(jcas1.getCas(), jcas2.getCas(), false);

				SaveJCasWorker worker = new SaveJCasWorker(options.getOutput(), jcas2, (f, j) -> {
				});
				worker.execute();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
