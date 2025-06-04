package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleViewedEventPayload;
import my.board.hotarticle.repository.ArticleViewCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewedEventHandler implements EventHandler<ArticleViewedEventPayload> {

	private final ArticleViewCountRepository articleViewCountRepository;

	@Override
	public void handle(Event<ArticleViewedEventPayload> event) {
		ArticleViewedEventPayload payload = event.getPayload();
		articleViewCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleViewCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleViewedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_VIEWED;
	}

	@Override
	public Long findArticleId(Event<ArticleViewedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
