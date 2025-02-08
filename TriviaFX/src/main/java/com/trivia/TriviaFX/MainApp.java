package com.trivia.TriviaFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Trivia Game");
		MainMenu.showMainMenu(primaryStage); // Εμφάνιση του αρχικού μενού
	}

	public static void main(String[] args) {
		launch(args);
	}
}
