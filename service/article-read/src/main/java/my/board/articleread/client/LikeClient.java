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
public class LikeClient {

	private RestClient restClient;

	@Value("${endpoints.software-board-like-service.url}")
	private String likeServiceUrl;

	@PostConstruct
	public void initRestClient() {
		restClient = RestClient.create(likeServiceUrl);
	}

	public Long count(Long articleId) {
		try {
			return restClient.get()
				.uri("/v1/article-likes/articles/{articleId}/count", articleId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[LikeClient.read] articleId={}", articleId);
			return 0L;
		}
	}
}
