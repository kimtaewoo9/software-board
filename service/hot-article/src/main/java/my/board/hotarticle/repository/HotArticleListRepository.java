package my.board.hotarticle.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HotArticleListRepository {

	private final StringRedisTemplate redisTemplate;

	// key hot-article::list::{yyyyMMdd}
	private static final String KEY_FORMAT = "hot-article::list::%s";

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	public void add(Long articleId, LocalDateTime time, Long score, Long limit, Duration ttl) {
		// pipeline 을 사용해서 .. 3개의 명령을 한번에 실행함 .
		redisTemplate.executePipelined((RedisCallback<?>) action -> {
			StringRedisConnection connection = (StringRedisConnection) action; // redis 서버와 실제 네트워크 연결을 나타냄 . + 문자열 기반 connection
			String key = generateKey(time);
			connection.zAdd(key, score, String.valueOf(articleId)); // score + articleId 를 저장함 .
			// score -> 정렬 기준, articleId -> 실제 저장될 값 .
			connection.zRemRange(key, 0, -limit - 1); // 상위 limit 개만 남기고 삭제.
			connection.expire(key, ttl.toSeconds()); // 지정 시간 후 메모리에서 자동 삭제 .
			return null;
		});
	}

	public void remove(Long articleId, LocalDateTime time) {
		// 매개변수가 두개 필요한 이유, redis 안에 여러개의 sorted set 이 서로 다른 이름으로 저장되어 있음 .
		// sorted set 을 찾기 위한 키 + 어떤 멤버를 삭제할 지 .. (해당 날짜의 sorted set 으로 가서 해당 멤버를 삭제함 .)
		redisTemplate.opsForZSet().remove(generateKey(time), String.valueOf(articleId));
	}

	private String generateKey(LocalDateTime time) {
		// LocalDateTime 을 yyyyMMdd 형식의 String 으로 바꿈 .
		return generateKey(TIME_FORMATTER.format(time));
	}

	private String generateKey(String dateStr) {
		return KEY_FORMAT.formatted(dateStr);
	}

	// dateStr -> "yyyyMMdd" 형식을 파라미터로 받아서 . 해당 날짜의 인기글 id를 반환 .
	public List<Long> readAll(String dateStr) {
		return redisTemplate.opsForZSet()
			.reverseRangeWithScores(generateKey(dateStr), 0, -1).stream()
			.peek(tuple ->
				log.info("[HotArticleListRepository.readAll] articleId={}, score={}",
					tuple.getValue(), tuple.getScore()))
			.map(ZSetOperations.TypedTuple::getValue)
			.map(Long::valueOf)
			.toList();
	}
}
