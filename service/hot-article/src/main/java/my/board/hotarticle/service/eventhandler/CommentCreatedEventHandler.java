package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.CommentCreatedEventPayload;
import my.board.hotarticle.repository.ArticleCommentCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {

	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentCreatedEventPayload> event) {
		CommentCreatedEventPayload payload = event.getPayload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentCreatedEventPayload> event) {
		return event.getType() == EventType.COMMENT_CREATED;
	}

	@Override
	public Long findArticleId(Event<CommentCreatedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
