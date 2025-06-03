package my.board.hotarticle.repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleCreatedTimeRepository {

	private final StringRedisTemplate redisTemplate;

	private static final String KEY_FORMAT = "hot-article::article::%s::created-time";

	public void create(Long articleId, LocalDateTime createdAt, Duration ttl) {
		redisTemplate.opsForValue().set(
			generateKey(articleId),
			// LocalDateTime 은 시간대 정보가 없기 때문에, Instant 로 변환하려면 시간대를 알려줘야함 .
			// + 1970-01-01 0시 부터 몇 밀리초가 흘렀는지 .. 나타내는 숫자와 바꾸는 과정임 .
			String.valueOf(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()),
			ttl
		);
	}

	public void update(Long articleId, LocalDateTime createdAt, Duration ttl) {
		redisTemplate.opsForValue().set(
			generateKey(articleId),
			String.valueOf(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()),
			ttl
		);
	}

	public void delete(Long articleId) {
		redisTemplate.delete(generateKey(articleId));
	}

	public LocalDateTime read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		if (result == null) {
			return null;
		}

		return LocalDateTime.ofInstant(
			Instant.ofEpochMilli(Long.parseLong(result)), ZoneOffset.UTC
		);
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
