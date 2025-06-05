package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.payload.CommentDeletedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<CommentDeletedEventPayload> event) {
		CommentDeletedEventPayload payload = event.getPayload();
		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<CommentDeletedEventPayload> event) {
		return false;
	}
}
