package services;

// import java.util.List;
// import model.TriviaInfo;

import Exception.TriviaAPIException;


public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TriviaAPIService trivia = new TriviaAPIService("https://opentdb.com/api.php");
		try {

			//List<TriviaInfo> questions = trivia.getTriviaQuestions(10, 0, null, null);
			trivia.getTriviaQuestions(10, 0, null, null);

		} catch (TriviaAPIException e) {
			e.printStackTrace();
		}
	}
}
