package de.unistuttgart.ims.coref.annotator;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Annotator2 extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("/Annotator2.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Annotator2.launch(args);
	}

	@FXML
	protected void openButtonPressed() {
		Annotator.app.fileOpenDialog(null, Annotator.app.getPluginManager().getDefaultIOPlugin());
	}
}
