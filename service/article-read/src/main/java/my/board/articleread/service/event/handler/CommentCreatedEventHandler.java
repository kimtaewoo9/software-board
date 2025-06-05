package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.CommentCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<CommentCreatedEventPayload> event) {
		CommentCreatedEventPayload payload = event.getPayload();
		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<CommentCreatedEventPayload> event) {
		return event.getType() == EventType.COMMENT_CREATED;
	}
}
