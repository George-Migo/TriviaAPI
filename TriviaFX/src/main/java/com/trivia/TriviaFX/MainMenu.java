package com.trivia.TriviaFX;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.TriviaCategory;
import services.TriviaAPIService;
import java.util.List;

public class MainMenu {
	private static final TriviaAPIService triviaService = new TriviaAPIService("https://opentdb.com/api.php");
	private static TriviaCategory lastCategory = TriviaCategory.ANY_CATEGORY;
	private static String lastDifficulty = "Any Difficulty";
	private static String lastType = "Any Type";
	private static int lastAmount = 10;
	private static int highScore = 0; // Το μέγιστο σκορ διατηρείται αν δεν αλλάξουν οι ρυθμίσεις

	public static void showMainMenu(Stage window) {
		// Δημιουργία Welcome Message
		Label welcomeLabel = new Label("Welcome to the new trivia game!\n");
		welcomeLabel.setFont(Font.font("Arial", 20));		// Μεγαλύτερη γραμματοσειρά μόνο για την πρώτη φράση
		welcomeLabel.setStyle("-fx-font-weight: bold;");	// Bold μόνο στο πρώτο κομμάτι

		Label welcomeLabel1 = new Label(
				"Choose a category, difficulty level, type, and number of questions, \n" + "and start the challenge!");
		welcomeLabel1.setFont(Font.font("Arial", 14));		// Κανονική γραμματοσειρά για το υπόλοιπο κείμενο
		welcomeLabel1.setTextAlignment(TextAlignment.CENTER);

        // Δημιουργία του TextFlow για να συνδυάσει τα δύο κομμάτια κειμένου
		TextFlow welcomeTextFlow = new TextFlow(welcomeLabel, welcomeLabel1);
		welcomeTextFlow.setTextAlignment(TextAlignment.CENTER);		// Κεντρική στοίχιση του TextFlow
		welcomeTextFlow.setMaxWidth(400);	// Περιορισμός πλάτους για καλύτερη στοίχιση

        // Dropdowns για επιλογές χρήστη
        // Κατηγορίες
		Label categoryLabel = new Label("Select Category:");
		ComboBox<TriviaCategory> categoryBox = new ComboBox<>();
		categoryBox.getItems().addAll(TriviaCategory.values());
		categoryBox.setValue(TriviaCategory.ANY_CATEGORY);

    	// Επίπεδο Δυσκολίας
		Label difficultyLabel = new Label("Select Difficulty:");
		ComboBox<String> difficultyBox = new ComboBox<>();
		difficultyBox.getItems().addAll("Any Difficulty", "easy", "medium", "hard");
		difficultyBox.setValue("Any Difficulty");	// Default τιμή

        // Τύπος Ερώτησης
		Label typeLabel = new Label("Select Type:");
		ComboBox<String> typeBox = new ComboBox<>();
		typeBox.getItems().addAll("Any Type", "multiple", "boolean");
		typeBox.setValue("Any Type");	// Default τιμή

        // Πεδίο αριθμού ερωτήσεων
		TextField amountField = new TextField();
		amountField.setPromptText("Number of Questions");
		HBox amountBox = new HBox(5, amountField);
		amountBox.setAlignment(Pos.CENTER);
        amountField.setMaxWidth(150); // Μικρότερο πλάτος

		// Κουμπί Start Game
		Button startButton = new Button("Start Game");
		startButton.setStyle(
				"-fx-background-color: #7B4DDB; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 8px;");
		
	    // Label για εμφάνιση μηνυμάτων
		Label resultLabel = new Label();
		
	    // Όταν ο χρήστης πατήσει το κουμπί, γίνεται το API call
		startButton.setOnAction(event -> {
			try {
				TriviaCategory selectedCategory = categoryBox.getValue();
				String difficulty = difficultyBox.getValue();
				String type = typeBox.getValue();
				int amount;
				
				 // Έλεγχος αριθμού ερωτήσεων
				try {
					amount = Integer.parseInt(amountField.getText());
				} catch (NumberFormatException e) {
					resultLabel.setText("Please enter a valid number of questions.");
					return;
				}
				// Αν ο χρήστης επιλέξει "Any Difficulty", στέλνουμε null στο API
				if ("Any Difficulty".equals(difficulty)) {
					difficulty = null;
				}
				if ("Any Type".equals(type)) {
					type = null;
				}

				// Αν οι παράμετροι είναι διαφορετικές από το προηγούμενο παιχνίδι, μηδενίζουμε το highScore
				if (!selectedCategory.equals(lastCategory) || (difficulty != null && !difficulty.equals(lastDifficulty))
						|| (type != null && !type.equals(lastType)) || amount != lastAmount) {
					highScore = 0;
		//			System.out.println("🔄 Οι ρυθμίσεις άλλαξαν! Το High Score μηδενίστηκε.");

					// Ενημέρωση των προηγούμενων ρυθμίσεων
					lastCategory = selectedCategory;
					lastDifficulty = difficulty;
					lastType = type;
					lastAmount = amount;
				}
				// Κλήση API για ερωτήσεις
				List<model.TriviaInfo> questions = triviaService.getTriviaQuestions(amount, selectedCategory.getId(),
						difficulty, type);

				// Άνοιγμα του παραθύρου παιχνιδιού
				GameWindow.display(questions, highScore, window);

			} catch (Exception e) {
				resultLabel.setText("Error fetching questions: " + e.getMessage());
			}
		});

		VBox layout = new VBox(10, welcomeTextFlow, categoryLabel, categoryBox, difficultyLabel, difficultyBox,
				typeLabel, typeBox, amountBox, new Label(""), startButton, resultLabel);	// προσθήκη new Label για απόσταση
		layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");
		layout.setAlignment(Pos.CENTER);

		Scene mainScene = new Scene(layout, 600, 450);
		window.setScene(mainScene);
		window.show();
	}
}
