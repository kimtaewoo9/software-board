package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleUnlikeEventPayload;
import my.board.hotarticle.repository.ArticleLikeCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUnlikeEventHandler implements EventHandler<ArticleUnlikeEventPayload> {

	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleUnlikeEventPayload> event) {
		ArticleUnlikeEventPayload payload = event.getPayload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight() // 자정까지만 저장함 .
		);
	}

	@Override
	public boolean supports(Event<ArticleUnlikeEventPayload> event) {
		return event.getType() == EventType.ARTICLE_UNLIKED;
	}

	@Override
	public Long findArticleId(Event<ArticleUnlikeEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
