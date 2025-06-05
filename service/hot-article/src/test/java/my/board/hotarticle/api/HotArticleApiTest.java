package my.board.hotarticle.api;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import my.board.hotarticle.service.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class HotArticleApiTest {

	RestClient restClient = RestClient.create("http://localhost:9004");

	@Test
	void readAllTest() {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		String dateStr = now.format(formatter);
		System.out.println("✅ dateStr: " + dateStr);

		List<HotArticleResponse> responses = restClient.get()
			.uri("/v1/hot-articles/articles/date/{dateStr}", dateStr)
			.retrieve()
			.body(new ParameterizedTypeReference<List<HotArticleResponse>>() {
			});

		// response 가 null 로 반환됨 ..
		for (HotArticleResponse response : responses) {
			System.out.println("[response.getArticleId] = " + response.getArticleId());
		}
	}
}
