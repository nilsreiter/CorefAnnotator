package de.unistuttgart.ims.coref.annotator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class AnnotatorController {
	@FXML
	FlowPane importPane;

	@FXML
	FlowPane recentPane;

	@FXML
	public void fileOpenDialog() {
		Annotator.app.fileOpenDialog(null, Annotator.app.getPluginManager().getDefaultIOPlugin());
	}

	@FXML
	public void close() {
		Platform.exit();
	}

}
