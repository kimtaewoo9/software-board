package my.board.articleread.repository;

import static java.util.function.Function.identity;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import my.board.common.dataserializer.DataSerializer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {

	private final StringRedisTemplate redisTemplate;

	// article-read::article::%s
	private static final String KEY_FORMAT = "article-read::article::%s";

	public void create(ArticleQueryModel articleQueryModel, Duration ttl) {
		redisTemplate.opsForValue()
			.set(
				generateKey(articleQueryModel.getArticleId()),
				DataSerializer.serialize(articleQueryModel),
				ttl);
	}

	// 게시글 update 이벤트가 발생하면 .. update 해줘야함
	public void update(ArticleQueryModel articleQueryModel) {
		redisTemplate.opsForValue()
			.setIfPresent(
				generateKey(articleQueryModel.getArticleId()),
				DataSerializer.serialize(articleQueryModel)
			);
	}

	// 게시글이 삭제되는 이벤트가 생기면 .. delete 해줘야됨
	public void delete(Long articleId) {
		redisTemplate.delete(generateKey(articleId));
	}

	public Optional<ArticleQueryModel> read(Long articleId) {
		return Optional.ofNullable(
			redisTemplate.opsForValue().get(generateKey(articleId))
		).map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class));
	}

	public Map<Long, ArticleQueryModel> readAll(List<Long> articleIds) {
		List<String> keyList = articleIds.stream().map(this::generateKey).toList();

		return redisTemplate.opsForValue().multiGet(keyList).stream()
			.filter(Objects::nonNull)
			.map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class))
			.collect(Collectors.toMap(ArticleQueryModel::getArticleId, identity()));
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
