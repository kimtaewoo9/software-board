package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleDeletedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.getPayload();
		articleQueryModelRepository.delete(payload.getArticleId());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_DELETED;
	}
}
