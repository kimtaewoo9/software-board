package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleDeleteEventPayload;
import my.board.hotarticle.repository.ArticleCreatedTimeRepository;
import my.board.hotarticle.repository.HotArticleListRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeleteEventPayload> {

	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;
	private final HotArticleListRepository hotArticleListRepository;

	@Override
	public void handle(Event<ArticleDeleteEventPayload> event) {
		ArticleDeleteEventPayload payload = event.getPayload();
		articleCreatedTimeRepository.delete(payload.getArticleId());
		hotArticleListRepository.remove(payload.getArticleId(), payload.getCreatedAt());
	}

	@Override
	public boolean supports(Event<ArticleDeleteEventPayload> event) {
		return event.getType() == EventType.ARTICLE_DELETED;
	}

	@Override
	public Long findArticleId(Event<ArticleDeleteEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
