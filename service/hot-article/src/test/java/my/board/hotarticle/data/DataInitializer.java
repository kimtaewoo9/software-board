package my.board.hotarticle.data;

import java.util.random.RandomGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class DataInitializer {

	RestClient articleServiceClient = RestClient.create("http://localhost:9000");
	RestClient commentServiceClient = RestClient.create("http://localhost:9001");
	RestClient likeServiceClient = RestClient.create("http://localhost:9002");
	RestClient viewServiceClient = RestClient.create("http://localhost:9003");

	@Test
	void initialize() {
		for (int cnt_i = 0; cnt_i < 30; cnt_i++) {
			Long articleId = createArticle();

			long commentCount = RandomGenerator.getDefault().nextLong(10);
			long likeCount = RandomGenerator.getDefault().nextLong(10);
			long viewCount = RandomGenerator.getDefault().nextLong(200);

			createComment(articleId, commentCount);
			like(articleId, likeCount);
			view(articleId, viewCount);
		}
	}

	private void view(Long articleId, long viewCount) {
		long count = viewCount;
		while (count-- > 0) {
			viewServiceClient.post()
				.uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, 1L)
				.retrieve();
		}
	}

	private void like(Long articleId, long likeCount) {
		long count = likeCount;
		while (count-- > 0) {
			likeServiceClient.post()
				.uri("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1",
					articleId, 1L)
				.retrieve();
		}
	}

	private void createComment(Long articleId, long commentCount) {
		long count = commentCount;
		while (count-- > 0) {
			commentServiceClient.post()
				.uri("/v1/comments")
				.body(new CommentCreateRequest(articleId, "content", 1L))
				.retrieve();
		}
	}

	Long createArticle() {
		return articleServiceClient.post()
			.uri("/v1/articles")
			.body(new ArticleCreateRequest("title", "content", 1L, 1L))
			.retrieve()
			.body(ArticleResponse.class)
			.getArticleId();
	}

	@Data
	@AllArgsConstructor
	static class CommentCreateRequest {

		private Long articleId;
		private String content;
		private Long writerId;
	}

	@Data
	@AllArgsConstructor
	static class CommentResponse {

		private Long articleId;
	}

	@Data
	@AllArgsConstructor
	static class ArticleCreateRequest {

		private String title;
		private String content;
		private Long writerId;
		private Long boardId;
	}

	@Getter
	static class ArticleResponse {

		private Long articleId;
	}
}
