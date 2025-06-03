package my.board.hotarticle.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleCommentCountRepository {

	private final StringRedisTemplate redisTemplate;

	private static final String KEY_FORMAT = "hot-article::article::%s::comment-count";

	public void createOrUpdate(Long articleId, Long commentCount, Duration ttl) {
		// key + commentCount(String) + ttl
		redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(commentCount), ttl);
	}

	public Long read(Long articleId) {
		// redisTemplate 는 String 을 반환함 .
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		return result == null ? 0L : Long.parseLong(result);
	}

	// articleId를 가지고 hot-article::article::%s::comment_count 형식의 키를 만듦.
	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
