package my.board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {

	private final StringRedisTemplate redisTemplate;

	// view::article::{article_id}:view_count
	private static final String KEY_FORMAT = "view::article::%s::view_count";

	public Long read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		return result == null ? 0L : Long.parseLong(result);
	}

	public Long increase(Long articleId) {
		// 키가 존재 하지 않으면 그 키값을 0으로 세팅하고 1 증가 시킴 -> 1이 반환.
		// 키가 존재하면 키값을 1 증가시키고 해당 값을 반환함 .
		return redisTemplate.opsForValue().increment(generateKey(articleId));
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
