package my.board.articleread.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleIdListRepository {

	private final StringRedisTemplate redisTemplate;

	// article-read::board::{boardId}::article-list
	private static final String KEY_FORMAT = "article-read::board::%s::article-list";

	public void add(Long boardId, Long articleId, Long limit) {
		redisTemplate.executePipelined((RedisCallback<?>) action -> {
			StringRedisConnection connection = (StringRedisConnection) action;
			String key = generateKey(boardId);
			connection.zAdd(key, 0, toPaddedString(articleId));
			connection.zRemRange(key, 0, -limit - 1); // 상위 limit 개수만 유지 . 1000개 정도
			return null;
		});
	}

	public void delete(Long boardId, Long articleId) {
		redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId));
	}

	public List<Long> readAll(Long boardId, Long offset, Long limit) {
		return redisTemplate.opsForZSet()
			.reverseRange(generateKey(boardId), offset, offset + limit - 1)
			.stream().map(Long::valueOf).toList();
	}

	// reverseRangeByLex .. -> 역순 + 사전순으로 범위를 지정함 .
	// unbounded() -> 범위 제한 없음 .
	// leftUnbounded -> 지정한 커서 값보다 .. 사전순으로 작은 모든 멤버들 . 근데 lastArticleId는 빼고 (exclusive)
	public List<Long> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long limit) {
		return redisTemplate.opsForZSet().reverseRangeByLex(
			generateKey(boardId),
			lastArticleId == null ?
				Range.unbounded() :
				Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))),
			Limit.limit().count(limit.intValue()) // limit 개수만큼 가져와라 ..
		).stream().map(Long::valueOf).toList();
	}

	private String toPaddedString(Long articleId) {
		return "%019d".formatted(articleId);
	}

	private String generateKey(Long boardId) {
		return KEY_FORMAT.formatted(boardId);
	}
}
