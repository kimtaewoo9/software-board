package my.board.articleread.api;


import java.util.List;
import my.board.articleread.service.response.ArticleReadPageResponse;
import my.board.articleread.service.response.ArticleReadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class ArticleReadApiTest {

	RestClient articleReadClient = RestClient.create("http://localhost:9005");
	RestClient articleClient = RestClient.create("http://localhost:9000");

	@Test
	void readTest() {
		ArticleReadResponse articleReadResponse = articleClient.get()
			.uri("/v1/articles/{articleId}", 188657256838496256L)
			.retrieve()
			.body(ArticleReadResponse.class);

		System.out.println("response = " + articleReadResponse);
	}

	@Test
	void readAllTest() {
		ArticleReadPageResponse articleReadPageResponse =
			articleReadClient.get()
				.uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L, 2L, 5L))
				.retrieve()
				.body(ArticleReadPageResponse.class);

		System.out.println("[ArticleReadClient]");
		List<ArticleReadResponse> articles = articleReadPageResponse.getArticles();
		for (ArticleReadResponse article : articles) {
			System.out.println("article.getArticleId() = " + article.getArticleId());
		}

		ArticleReadPageResponse articleReadPageResponse2 =
			articleClient.get()
				.uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L, 2L, 5L))
				.retrieve()
				.body(ArticleReadPageResponse.class);

		System.out.println("[ArticleClient]");
		List<ArticleReadResponse> articles2 = articleReadPageResponse2.getArticles();
		for (ArticleReadResponse article : articles2) {
			System.out.println("article.getArticleId() = " + article.getArticleId());
		}
	}

	@Test
	void readAllInfiniteScroll() {
		List<ArticleReadResponse> articleReadResponseList = articleReadClient.get()
			.uri(
				"/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s"
					.formatted(1L, 5L, 189956226731487232L))
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
			});

		System.out.println("[ArticleReadClient]");
		for (ArticleReadResponse articleReadResponse : articleReadResponseList) {
			System.out.println(
				"articleReadResponse.getArticleId() = " + articleReadResponse.getArticleId());
		}

		List<ArticleReadResponse> articleReadResponseList2 = articleClient.get()
			.uri("/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s"
				.formatted(1L, 5L, 189956226731487232L))
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
			});

		System.out.println("[ArticleClient]");
		for (ArticleReadResponse articleReadResponse : articleReadResponseList2) {
			System.out.println(
				"articleReadResponse.getArticleId() = " + articleReadResponse.getArticleId());
		}
	}
}
