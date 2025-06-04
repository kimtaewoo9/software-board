package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.CommentDeletedEventPayload;
import my.board.hotarticle.repository.ArticleCommentCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {

	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentDeletedEventPayload> event) {
		CommentDeletedEventPayload payload = event.getPayload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentDeletedEventPayload> event) {
		return event.getType() == EventType.COMMENT_DELETED;
	}

	@Override
	public Long findArticleId(Event<CommentDeletedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
