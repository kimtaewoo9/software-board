package my.board.articleread.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.articleread.client.ArticleClient;
import my.board.articleread.client.CommentClient;
import my.board.articleread.client.LikeClient;
import my.board.articleread.client.ViewClient;
import my.board.articleread.repository.ArticleIdListRepository;
import my.board.articleread.repository.ArticleQueryModel;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.articleread.repository.BoardArticleCountRepository;
import my.board.articleread.service.event.handler.EventHandler;
import my.board.articleread.service.response.ArticleReadPageResponse;
import my.board.articleread.service.response.ArticleReadResponse;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleReadService {

	// 데이터 없을때 클라이언트에게 요청해야함 . 클라이언트에게 요청 or redis 에서 바로 가져오기 .
	private final ArticleClient articleClient;
	private final CommentClient commentClient;
	private final LikeClient likeClient;
	private final ViewClient viewClient;

	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	private final List<EventHandler> eventHandlers;
	private final RedisTemplate redisTemplate;

	// consumer 를 통해서 handleEvent 메서드가 호출됨 . event 받아서 handler로 처리
	public void handleEvent(Event<EventPayload> event) {
		for (EventHandler eventHandler : eventHandlers) {
			if (eventHandler.supports(event)) {
				eventHandler.handle(event); // support 하는지 확인 후 handle
			}
		}
	}

	public ArticleReadResponse read(Long articleId) {
		ArticleQueryModel articleQueryModel = articleQueryModelRepository.read(articleId)
			.or(() -> fetch(articleId))
			.orElseThrow(
				() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. articleId: " + articleId)
			);

		return ArticleReadResponse.from(
			articleQueryModel,
			viewClient.count(articleId) // 실시간으로 viewClient를 통해 조회수를 가져옴 .
		);
	}

	public Optional<ArticleQueryModel> fetch(Long articleId) {
		Optional<ArticleQueryModel> articleQueryModelOptional = articleClient.read(articleId)
			.map(article -> ArticleQueryModel.create(
				article,
				commentClient.count(articleId),
				likeClient.count(articleId)
			));

		articleQueryModelOptional
			.ifPresent(articleQueryModel ->
				articleQueryModelRepository.create(
					articleQueryModel,
					Duration.ofDays(1)
				));
		log.info("[ArticleReadService.fetch] fetch data, articleId={}, isPresent={}", articleId,
			articleQueryModelOptional.isPresent());

		return articleQueryModelOptional;
	}

	public ArticleReadPageResponse readAll(Long boardId, Long page, Long pageSize) {
		return ArticleReadPageResponse.of(
			readAll(
				readAllArticleIds(boardId, page, pageSize)
			),
			count(boardId)
		);
	}

	private List<ArticleReadResponse> readAll(List<Long> articleIds) {
		Map<Long, ArticleQueryModel> articleQueryModelMap =
			articleQueryModelRepository.readAll(articleIds);

		return articleIds.stream()
			.map(articleId -> articleQueryModelMap.containsKey(articleId) ?
				articleQueryModelMap.get(articleId) :
				fetch(articleId).orElse(null))
			.filter(Objects::nonNull)
			.map(articleQueryModel ->
				ArticleReadResponse.from(
					articleQueryModel,
					viewClient.count(articleQueryModel.getArticleId())
				))
			.toList();
	}

	private List<Long> readAllArticleIds(Long boardId, Long page, Long pageSize) {
		List<Long> articleIds = articleIdListRepository
			.readAll(boardId, (page - 1) * pageSize, pageSize);
		if (pageSize == articleIds.size()) {
			log.info("[ArticleReadService.readAllArticleIds] return redis data.");
			return articleIds;
		}
		return articleClient.readAll(boardId, page, pageSize).getArticles().stream()
			.map(ArticleClient.ArticleResponse::getArticleId)
			.toList();
	}

	private Long count(Long boardId) {
		Long result = boardArticleCountRepository.read(boardId);
		if (result != null) {
			return result;
		}
		long count = articleClient.count(boardId);
		boardArticleCountRepository.createOrUpdate(boardId, count);
		return count;
	}

	public List<ArticleReadResponse> readAllInfiniteScroll(
		Long boardId,
		Long lastArticleId,
		Long pageSize) {
		return readAll(
			readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize)
		);
	}

	private List<Long> readAllInfiniteScrollArticleIds(Long boardId, Long lastArticleId,
		Long pageSize) {
		List<Long> articleIds = articleIdListRepository
			.readAllInfiniteScroll(boardId, lastArticleId, pageSize);
		if (pageSize == articleIds.size()) {
			log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data.");
			return articleIds;
		}
		log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data.");
		return articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize).stream()
			.map(ArticleClient.ArticleResponse::getArticleId)
			.toList();
	}
}
