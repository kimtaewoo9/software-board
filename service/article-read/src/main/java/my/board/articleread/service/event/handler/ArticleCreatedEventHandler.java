package my.board.articleread.service.event.handler;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModel;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleCreatedEventPayload> event) {
		ArticleCreatedEventPayload payload = event.getPayload();
		articleQueryModelRepository.create(
			ArticleQueryModel.create(payload),
			Duration.ofDays(1)
		);
	}

	@Override
	public boolean supports(Event<ArticleCreatedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_CREATED;
	}
}
