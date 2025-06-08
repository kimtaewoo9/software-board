package my.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleIdListRepository;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.articleread.repository.BoardArticleCountRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleDeletedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.getPayload();
		// 목록에서 먼저 삭제 해주고 .. 그 다음 데이터 삭제
		articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
		articleQueryModelRepository.delete(payload.getArticleId());
		boardArticleCountRepository.createOrUpdate(payload.getBoardId(),
			payload.getBoardArticleCount());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_DELETED;
	}
}
