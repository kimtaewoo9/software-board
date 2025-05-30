package my.board.like.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import my.board.like.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleLikeApiTest {

	RestClient restClient = RestClient.create("http://localhost:9002");

	@Test
	void likeAndUnLikeTest() {
		Long articleId = 9999L;

		like(articleId, 1L);
		like(articleId, 2L);
		like(articleId, 3L);

		ArticleLikeResponse response1 = read(articleId, 1L);
		ArticleLikeResponse response2 = read(articleId, 2L);
		ArticleLikeResponse response3 = read(articleId, 3L);

		System.out.println("response1: " + response1);
		System.out.println("response2: " + response2);
		System.out.println("response3: " + response3);

		unlike(articleId, 1L);
		unlike(articleId, 2L);
		unlike(articleId, 3L);

	}

	void like(Long articleId, Long userId) {
		restClient.post()
			.uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
			.retrieve();
	}

	ArticleLikeResponse like(Long articleId, Long userId, String lockType) {
		return restClient.post()
			.uri("/v1/article-likes/articles/{articleId}/users/{userId}/"
				+ lockType, articleId, userId)
			.retrieve()
			.body(ArticleLikeResponse.class);
	}

	void unlike(Long articleId, Long userId) {
		restClient.delete()
			.uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
			.retrieve();

	}

	void unlike(Long articleId, Long userId, String lockType) {
		restClient.delete()
			.uri("/v1/article-likes/articles/{articleId}/users/{userId}/"
				+ lockType, articleId, userId)
			.retrieve();
	}

	ArticleLikeResponse read(Long articleId, Long userId) {
		return restClient.get()
			.uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
			.retrieve()
			.body(ArticleLikeResponse.class);
	}

	@Test
	void likePerformanceTest() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		likePerformanceTest(executorService, 1111L, "pessimistic-lock-1");
		likePerformanceTest(executorService, 2222L, "pessimistic-lock-2");
		likePerformanceTest(executorService, 3333L, "optimistic-lock");

	}

	void likePerformanceTest(ExecutorService executorService, Long articleId, String lockType)
		throws InterruptedException {

		CountDownLatch latch = new CountDownLatch(3000);
		System.out.println("Lock Type: " + lockType);

		ArticleLikeResponse articleLikeResponse = like(articleId, 1L, lockType);
		System.out.println("✅articleLikeResponse: " + articleLikeResponse);

		long start = System.nanoTime();
		for (int cnt_i = 0; cnt_i < 3000; cnt_i++) {
			long userId = cnt_i + 2;
			executorService.submit(() -> {
				like(articleId, userId, lockType);
				latch.countDown();
			});
		}

		latch.await();
		long end = System.nanoTime();
		System.out.println("lockType = " + lockType + ", time = " + (end - start) / 1000000 + "ms");
		System.out.println(lockType + " end");

		Long count = restClient.get()
			.uri("/v1/article-likes/articles/{articleId}/count", articleId)
			.retrieve()
			.body(Long.class);

		System.out.println("count = " + count);
	}
}
