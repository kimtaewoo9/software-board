package my.board.view.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {

	// 조회수 어뷰징 방지 정책 구현을 위한 분산락 구현 .
	private final StringRedisTemplate redisTemplate;

	private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";

	public boolean lock(Long articleId, Long userId, Duration ttl) {
		String key = generateKey(articleId, userId);
		return redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
	}

	private String generateKey(Long articleId, Long userId) {
		return KEY_FORMAT.formatted(articleId, userId);
	}
}
