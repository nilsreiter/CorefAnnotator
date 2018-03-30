package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JPanel;

import org.junit.Test;

public class TestWizard {

	@Test
	public void testWizard() {
		Wizard wiz = new Wizard();
		wiz.addPage(new JPanel());
		wiz.addPage(new JPanel());
		wiz.start();
		wiz.setVisible(true);
		wiz.pack();
	}
}
