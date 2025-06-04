package my.board.hotarticle.api;


import java.util.List;
import my.board.hotarticle.service.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class HotArticleApiTest {

	RestClient restClient = RestClient.create("http://localhost:9004");

	@Test
	void readAllTest() {
		List<HotArticleResponse> responses = restClient.get()
			.uri("/v1/hot-articles/articles/date/{dateStr}")
			.retrieve()
			.body(new ParameterizedTypeReference<List<HotArticleResponse>>() {
			});

		for (HotArticleResponse response : responses) {
			System.out.println("respones = " + response);
		}
	}
}
