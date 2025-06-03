package my.board.hotarticle.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleLikeCountRepository {

	private final StringRedisTemplate redisTemplate;

	// hot-article::article::{article_id}::like-count
	private static final String KEY_FORMAT = "hot-article::article::%s::like-count";

	public void create(Long articleId, Long articleLikeCount, Duration ttl) {
		redisTemplate.opsForValue()
			.set(generateKey(articleId), String.valueOf(articleLikeCount), ttl);
	}

	public void update(Long articleId, Long articleLikeCount, Duration ttl) {
		redisTemplate.opsForValue()
			.set(generateKey(articleId), String.valueOf(articleLikeCount), ttl);
	}

	public Long read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		return result == null ? 0L : Long.parseLong(result);
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
