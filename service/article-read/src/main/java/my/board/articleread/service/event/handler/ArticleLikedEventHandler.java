package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleLikedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleLikedEventPayload> event) {
		ArticleLikedEventPayload payload = event.getPayload();
		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<ArticleLikedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_LIKED;
	}
}
