package JUnit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import model.TriviaInfo;
import services.TriviaAPIService;

class JTest {

    @Test
    public void testAPI() throws Exception {
        TriviaAPIService trivia = new TriviaAPIService("https://opentdb.com/api.php");

            List<TriviaInfo> results = trivia.getTriviaQuestions(10, 0, null, null);
            assertFalse(results.isEmpty());
    }
}
