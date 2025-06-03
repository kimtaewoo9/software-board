package my.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.CommentDeleteEventPayload;
import my.board.hotarticle.repository.ArticleCommentCountRepository;
import my.board.hotarticle.util.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeleteEventHandler implements EventHandler<CommentDeleteEventPayload> {

	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentDeleteEventPayload> event) {
		CommentDeleteEventPayload payload = event.getPayload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentDeleteEventPayload> event) {
		return event.getType() == EventType.COMMENT_DELETED;
	}

	@Override
	public Long findArticleId(Event<CommentDeleteEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
