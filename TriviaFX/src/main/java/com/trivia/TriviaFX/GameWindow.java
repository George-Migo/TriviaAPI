package com.trivia.TriviaFX;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TriviaInfo;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameWindow {
	private static int highScore = 0;

	public static void display(List<TriviaInfo> questions, int higScore, Stage window) {
	//	System.out.println("🔵 GameWindow.display() Κλήθηκε!");
	//	System.out.println("🔹 Συνολικές ερωτήσεις: " + questions.size());

		AtomicInteger questionIndex = new AtomicInteger(0);
		AtomicInteger score = new AtomicInteger(0);
		AtomicInteger correctAnswers = new AtomicInteger(0);
		AtomicInteger totalQuestions = new AtomicInteger(questions.size());

		Label questionNumberLabel = new Label();	// Ετικέτα για τον αριθμό της ερώτησης
	    questionNumberLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #212121;");
	    questionNumberLabel.setFont(Font.font("Arial", 14));

		Label questionLabel = new Label();	// Ετικέτα για την ίδια την ερώτηση
		questionLabel.setWrapText(true);	// Επιτρέπει το σπάσιμο γραμμών για μεγάλες ερωτήσεις
		questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: -fx-text-fill: #333333;");

		Label warningLabel = new Label();	// Ετικέτα προειδοποίσης (για περιπτώσεις μη απάντησης)
		warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;"); // Κόκκινο μήνυμα
		warningLabel.setVisible(false); // Αρχικά κρυφό

		ToggleGroup answersGroup = new ToggleGroup();	// Ομαδοποιεί τις απαντήσεις, δίνοντας μια επιλογή
		VBox answersBox = new VBox(5);	// Τακτοποιεί τις απαντήσεις κάθετα, με 5 pixel απόσταση
		Button nextButton = new Button("Next Question");
		nextButton.setStyle("-fx-background-color: #7B4DDB; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 8px;");
		
		Label scoreLabel = new Label("Score: 0");	// Ετικέτα για το score 
        scoreLabel.setFont(Font.font("Arial", 12));
        scoreLabel.setStyle("-fx-font-weight: bold;");
        
        // Φόρτωση πρώτης ερώτησης
		loadQuestion(questions, questionIndex, questionNumberLabel, questionLabel, answersBox, answersGroup);

		// Για νέο παιχνίδι
		Button newGameButton = new Button("New Game");
		newGameButton.setStyle("-fx-background-color: #7B4DDB; -fx-text-fill: white; -fx-font-size: 14px;");
		newGameButton.setVisible(false); 	// Αρχικά θα είναι κρυφό, θα εμφανίζεται μόνο στο τέλος του παιχνιδιού
		
		// Για επιστροφή στο μενού
		Button returnToMenuButton = new Button("Return to Menu");
		returnToMenuButton.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: black; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
		returnToMenuButton.setOnAction(ev -> MainMenu.showMainMenu(window)); // Θα εμφανίζεται κατά την διάρκεια

		// Εκτελείται όταν κάνουμε κλικ 
		nextButton.setOnAction(e -> {
			if (answersGroup.getSelectedToggle() == null) {
	//			System.out.println("⚠️ Ο χρήστης δεν επέλεξε απάντηση!");
				warningLabel.setText("⚠ Please select an answer before proceeding!");
				warningLabel.setVisible(true); // Εμφάνιση του μηνύματος
				return;
			}
			warningLabel.setVisible(false); // Αν έχει επιλεγεί απάντηση, κρύβουμε το μήνυμα
	//		System.out.println("🟢 Ο χρήστης απάντησε!");

			// Έλεγχος αν η απάντηση είναι σωστή
			RadioButton selectedAnswer = (RadioButton) answersGroup.getSelectedToggle();
			String userAnswer = selectedAnswer.getText();
			String correctAnswer = questions.get(questionIndex.get()).getCorrectAnswer();
			boolean isCorrect = userAnswer.equals(correctAnswer);

	//		System.out.println("🔍 Απάντηση χρήστη: " + userAnswer);
	//		System.out.println("✅ Σωστή απάντηση: " + correctAnswer);

			if (isCorrect) {
				score.addAndGet(10);
				correctAnswers.incrementAndGet();
				selectedAnswer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Πράσινο κουμπί - Σωστή Απάντηση
	//			System.out.println("🎉 Σωστή απάντηση! Νέο σκορ: " + score.get());
			} else {
				score.addAndGet(-5);
				selectedAnswer.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white;"); // Κόκκινο κουμπί - Λάθος Απάντηση
	//			System.out.println("❌ Λάθος απάντηση. Νέο σκορ: " + score.get());
			}

			scoreLabel.setText("Score: " + score.get()); // Ενημέρωση σκορ στο UI

			// Μετά από 1 δευτερόλεπτο, πάμε στην επόμενη ερώτηση
			new Thread(() -> {
				try {
					Thread.sleep(1000); // 1 δευτερόλεπτο καθυστέρηση
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				javafx.application.Platform.runLater(() -> {
					if (questionIndex.incrementAndGet() < questions.size()) {
	//					System.out.println("🔄 Φόρτωση επόμενης ερώτησης...");
						loadQuestion(questions, questionIndex, questionNumberLabel, questionLabel, answersBox,
								answersGroup);
					} else {
	//					System.out.println("🏁 Το παιχνίδι τελείωσε!");

						// Υπολογισμός ποσοστού επιτυχίας
						double successRate = ((double) correctAnswers.get() / totalQuestions.get()) * 100;					
						boolean isNewHighScore = score.get() > highScore; // Έλεγχος αν το σκορ είναι νέο ρεκόρ

						if (isNewHighScore) {
							highScore = score.get();
	//						System.out.println("🏆 Νέο ρεκόρ! Νέο High Score: " + highScore);
						}
						// Δημιουργία τελικού μηνύματος με σκορ και ποσοστό επιτυχίας
						String resultMessage = "Game Over!\n" + "Final Score: " + score.get() + "\n" + "Success Rate: "
								+ String.format("%.2f", successRate) + "%\n" + "High Score: " + highScore
								+ (isNewHighScore ? "\n🏆 Congratulations! You set a new High Score!" : ""); // Εμφάνιση
																												// μηνύματος
																												// αν
																												// υπάρχει
																												// νέο
																												// ρεκόρ
						questionNumberLabel.setText("Game Over!");
						questionLabel.setText(resultMessage);
						answersBox.getChildren().clear();
						scoreLabel.setVisible(false); // Απόκρυψη του "Score" label στο τέλος
						nextButton.setDisable(true);
						
						// Κρύβουμε το "Return to Menu" και εμφανίζουμε το "New Game"
						returnToMenuButton.setVisible(false); 
						newGameButton.setVisible(true); 
						newGameButton.setOnAction(ev -> {
						        MainMenu.showMainMenu(window); // Ξεκινά νέο παιχνίδι από το μενού
						    });
					}
				});
			}).start();
		});

	//	newGameButton.setOnAction(e -> MainMenu.showMainMenu(window));

		VBox layout = new VBox(15, questionNumberLabel, questionLabel, answersBox, warningLabel, nextButton, scoreLabel,
				returnToMenuButton, newGameButton);
		layout.setAlignment(Pos.CENTER);	// Κεντρική στοίχιση για όλα τα στοιχεία
		layout.setStyle("-fx-padding: 20px;");

		Scene gameScene = new Scene(layout, 500, 400);
		window.setScene(gameScene);
		window.show();
	}

	private static void loadQuestion(List<TriviaInfo> questions, AtomicInteger questionIndex, Label questionNumberLabel,
			Label questionLabel, VBox answersBox, ToggleGroup answersGroup) {
		if (questionIndex.get() >= questions.size()) {
			System.out.println("Δεν υπάρχουν άλλες ερωτήσεις.");
			return;
		}
		TriviaInfo currentQuestion = questions.get(questionIndex.get());

		// Debug print
	//	System.out.println("✅ Φόρτωση ερώτησης: " + currentQuestion.getQuestion());
	//	System.out.println("🔹 Τύπος ερώτησης: " + currentQuestion.getType());

		// Ορισμός αριθμού ερώτησης
		questionNumberLabel.setText("Question " + (questionIndex.get() + 1));
		questionLabel.setText(currentQuestion.getQuestion());

		answersBox.getChildren().clear();
		answersGroup.getToggles().clear();

		List<String> allAnswers = currentQuestion.getIncorrectAnswers();
		allAnswers.add(currentQuestion.getCorrectAnswer());
		java.util.Collections.shuffle(allAnswers);

		for (String answer : allAnswers) {
			RadioButton radioButton = new RadioButton(answer);
			radioButton.setToggleGroup(answersGroup);
			answersBox.getChildren().add(radioButton);
			radioButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-background-color: #F8F8F8; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
		}

	//	System.out.println("Απαντήσεις: " + allAnswers);

	//	System.out.println("✅ Η ερώτηση εμφανίστηκε σωστά!");
	}
}
