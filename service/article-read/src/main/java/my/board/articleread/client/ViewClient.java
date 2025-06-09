package my.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewClient {

	private RestClient restClient;

	@Value("${endpoints.software-board-view-service.url}")
	private String viewServiceUrl;

	@PostConstruct
	public void initRestClient() {
		restClient = RestClient.create(viewServiceUrl);
	}

	@Cacheable(key = "#articleId", value = "articleViewCount") // 파라미터가 캐시의 키로 사용됨 .
	public Long count(Long articleId) {
		log.info("✅ [ViewClient.count] articleId = {}", articleId);
		try {
			return restClient.get()
				.uri("/v1/article-views/articles/{articleId}/count", articleId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[Like.Client] articleId={}", articleId);
			return 0L;
		}
	}
}
