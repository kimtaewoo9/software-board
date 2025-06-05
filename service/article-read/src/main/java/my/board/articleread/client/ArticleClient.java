package my.board.articleread.client;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Optional;
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

	// 이 4개의 client 클래스로 redis에 저장된 데이터가 없을때 command 서버로 요청해서 데이터를 가져옴 .
	private RestClient restClient;

	@Value("${endpoints.software-board-article-service.url}")
	private String articleServiceUrl;

	@PostConstruct
	public void initRestClient() {
		restClient = RestClient.create(articleServiceUrl);
	}

	public Optional<ArticleResponse> read(Long articleId) {
		try {
			ArticleResponse articleResponse = restClient.get()
				.uri("/v1/articles/{articleId}", articleId)
				.retrieve()
				.body(ArticleResponse.class);

			return Optional.ofNullable(articleResponse);
		} catch (Exception e) {
			log.error("[ArticleClient.read] articleId={}", articleId);
			return Optional.empty();
		}
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
	}
}
