package my.board.articleread.api;


import my.board.articleread.service.response.ArticleReadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleReadApiTest {

	RestClient restClient = RestClient.create("http://localhost:9005");

	@Test
	void readTest() {
		ArticleReadResponse articleReadResponse = restClient.get()
			.uri("/v1/articles/{articleId}", 188657256838496256L)
			.retrieve()
			.body(ArticleReadResponse.class);

		System.out.println("response = " + articleReadResponse);
	}
}
