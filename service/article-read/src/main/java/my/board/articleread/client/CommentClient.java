package my.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentClient {

	private RestClient restClient;

	@Value("${endpoints.software-board-comment-service.url}")
	private String commentServiceUrl;

	@PostConstruct
	public void initRestClient() {
		restClient = RestClient.create(commentServiceUrl);
	}

	public long count(Long articleId) {
		try {
			return restClient.get()
				.uri("/v1/comments/articles/{articleId}/count", articleId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[CommentClient.count] articleId={}", articleId);
			return 0L;
		}
	}
}
