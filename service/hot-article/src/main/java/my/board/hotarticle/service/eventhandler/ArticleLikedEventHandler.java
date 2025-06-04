package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleLikedEventPayload;
import my.board.hotarticle.repository.ArticleLikeCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {

	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleLikedEventPayload> event) {
		ArticleLikedEventPayload payload = event.getPayload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight() // 자정 까지만 저장함 .
		);
	}

	@Override
	public boolean supports(Event<ArticleLikedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_LIKED;
	}

	@Override
	public Long findArticleId(Event<ArticleLikedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
