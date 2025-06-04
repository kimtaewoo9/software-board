package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleUnlikedEventPayload;
import my.board.hotarticle.repository.ArticleLikeCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUnlikeEventHandler implements EventHandler<ArticleUnlikedEventPayload> {

	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleUnlikedEventPayload> event) {
		ArticleUnlikedEventPayload payload = event.getPayload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight() // 자정까지만 저장함 .
		);
	}

	@Override
	public boolean supports(Event<ArticleUnlikedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_UNLIKED;
	}

	@Override
	public Long findArticleId(Event<ArticleUnlikedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
