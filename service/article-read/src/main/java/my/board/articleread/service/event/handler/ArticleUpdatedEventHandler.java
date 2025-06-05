package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleUpdatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUpdatedEventHandler implements EventHandler<ArticleUpdatedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleUpdatedEventPayload> event) {
		articleQueryModelRepository.read(event.getPayload().getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(event.getPayload()); // 객체 업데이트
				articleQueryModelRepository.update(articleQueryModel); // repository 업데이트
			});
	}

	@Override
	public boolean supports(Event<ArticleUpdatedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_UPDATED;
	}
}
