package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.common.event.Event;
import my.board.common.event.payload.ArticleUnlikedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUnlikedEventHandler implements EventHandler<ArticleUnlikedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;
	
	@Override
	public void handle(Event<ArticleUnlikedEventPayload> event) {

	}

	@Override
	public boolean supports(Event<ArticleUnlikedEventPayload> event) {
		return false;
	}
}
