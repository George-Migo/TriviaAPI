package services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.databind.ObjectMapper;
import Exception.TriviaAPIException;
import model.TriviaInfo;
import model.triviadb.ErrorResponse;
import model.triviadb.Trivia;
import model.triviadb.TriviaResult;

public class TriviaAPIService {
	private final String API_URL;

	public TriviaAPIService(String API_URL) {
		this.API_URL = API_URL;
	}

	public List<TriviaInfo> getTriviaQuestions(int amount, int category, String difficulty, String type)
			throws Exception {
		List<TriviaInfo> triviaQuestions = new ArrayList<TriviaInfo>();
		
		try {
			URIBuilder uriBuilder = new URIBuilder(API_URL)
					.addParameter("amount", String.valueOf(amount));
			if (difficulty != null) {
					uriBuilder.addParameter("category", String.valueOf(category)).addParameter("difficulty", difficulty);
			}
			if (type != null && !type.equals("Any Type")) { // Μόνο αν δεν είναι "Any Type"
			    uriBuilder.addParameter("type", type);
			}

			URI urii = uriBuilder.build();
			System.out.println("API Call: " + urii.toString()); // DEBUG - Δες το URL

			// Χρήση Base64 για καλύτερη κωδικοποίηση χαρακτήρων στα αποτελέσματα (quot, amp)
			uriBuilder.addParameter("encode", "base64");
			
			URI uri = uriBuilder.build();
			HttpGet getRequest = new HttpGet(uri);	// Δημιουργία αιτήματος στον server
			try (CloseableHttpClient httpclient = HttpClients.createDefault();	// Δημιουργία ενός client για την υποβολή του αιτήματος (μέσα στο try ώστε να γίνεται close και να μην έχουμε many leaks)
					CloseableHttpResponse response = httpclient.execute(getRequest)) {	// εντολή για να καλέσουμε το συγκεκριμένο αίτημα με αποθήκευση της απάντησης στη response μεταβλητή (δηλ. για το status code)
																						// status code = 200 ok, 400 page not found, 500 πρόβλημα στον server 
				HttpEntity entity = response.getEntity();	// αντιστοιχεί στο response που λαμβάνουμε από τον httpclient που είναι jason
				ObjectMapper mapper = new ObjectMapper();	// κάνει mapping με το response του jason για deserialization με χρήση readValue
	
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					ErrorResponse errorResponse = mapper.readValue(entity.getContent(), ErrorResponse.class);	// για την αποκωδικοποίηση τυχόν error του httpclient 
					if (errorResponse.getStatusCode() != null) {
						System.out.println(errorResponse.getStatusCode());
						throw new TriviaAPIException("Error occured on API call" + errorResponse.getStatusMessage());
					}
				}
	
				TriviaResult generatedTriviaResultObject = mapper.readValue(entity.getContent(), TriviaResult.class); 	// γίνεται το mapping και αποθηκεύεται στο αντικείμενο generatedTriviaResultObject της κλάσης
				
				switch (generatedTriviaResultObject.getTriviaResponseCode()) { // για να ελέγξουμε τα response code του API 
			    case 0:
			        break; // OK, συνεχίζουμε κανονικά
			    case 1:
			        throw new TriviaAPIException("No results. The API does not have enough questions for the query.");
			    case 2:
			        throw new TriviaAPIException("Invalid Parameter. One or more parameters are invalid.");
			    case 3:
			        throw new TriviaAPIException("Token Not Found. The session token does not exist.");
			    case 4:
			        throw new TriviaAPIException("Token Empty. The session token has exhausted all possible questions for the specified query. Resetting the token is required.");
			    case 5:
			        throw new TriviaAPIException("Rate Limit. Too many requests. Please, try again later.");
			    default:
					throw new TriviaAPIException("Unexpected response code from Trivia API: ");
	
				}
	
				List<Trivia> tResults = generatedTriviaResultObject.getResults(); // εμφανίζει μια λίστα αντικειμένων της Result με τη χρήση της μεθόδου getResult 
	
				for (Trivia triviaItem : tResults) {
					TriviaInfo triviaInfo = new TriviaInfo(decodeBase64(triviaItem.getCategory()), decodeBase64(triviaItem.getType()), decodeBase64(triviaItem.getDifficulty()),
							decodeBase64(triviaItem.getQuestion()), decodeBase64(triviaItem.getCorrectAnswer()), triviaItem.getIncorrectAnswers().stream().map(this::decodeBase64).collect(Collectors.toList()));
					triviaQuestions.add(triviaInfo);
					System.out.println("Category: " + triviaInfo.getCategory() + "\nType: " + triviaInfo.getType() + "\nDifficulty: "
							+ triviaInfo.getDifficulty() + "\nQuestion: " + triviaInfo.getQuestion() + "\nCorrect answer: "
							+ triviaInfo.getCorrectAnswer() + "\nIncorrect answers: " + triviaInfo.getIncorrectAnswers());
					System.out.println("-----------------------------------------------------");
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TriviaAPIException("Problem with URI", e);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TriviaAPIException("Problem with Client Protocol", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TriviaAPIException("Error requesting data from Trivia API", e);
		}

		return triviaQuestions;
	}
    // Μέθοδος για αποκωδικοποίηση Base64 (για μορφοποίηση της εμφάνισης χαρακτήρων)
    private String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
