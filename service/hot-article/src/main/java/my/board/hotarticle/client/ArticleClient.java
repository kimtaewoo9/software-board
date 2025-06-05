package my.board.hotarticle.client;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleClient {

	private RestClient restClient;

	@Value("${endpoints.software-board-article-service.url}")
	private String articleServiceUrl;

	@PostConstruct
	void initRestClient() {
		restClient = RestClient.create(articleServiceUrl);
	}

	public ArticleResponse read(Long articleId) {
		try {
			return restClient.get()
				.uri("/v1/articles/{articleId}", articleId)
				.retrieve()
				.body(ArticleResponse.class);
		} catch (Exception e) {
			log.error("[ArticleClient.read] articleId ={}", articleId, e);
		}

		return null;
	}

	@Getter
	public static class ArticleResponse {

		private Long articleId;
		private String title;
		private String content;
		private Long boardId;
		private Long writerId;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private Long boardArticleCount;
	}
}
