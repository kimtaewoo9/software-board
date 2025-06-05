package my.board.articleread.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.articleread.client.ArticleClient;
import my.board.articleread.client.CommentClient;
import my.board.articleread.client.LikeClient;
import my.board.articleread.client.ViewClient;
import my.board.articleread.repository.ArticleQueryModel;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.articleread.service.event.handler.EventHandler;
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
			.orElseThrow();

		return ArticleReadResponse.from(
			articleQueryModel,
			viewClient.count(articleId)
		);
	}

	// 없으면 articleClient 로 데이터 가져와서 articleQueryModel 로 만들어서 저장해야함 .
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
}
