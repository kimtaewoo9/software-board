package my.board.articleread.service.event.handler;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import my.board.articleread.repository.ArticleIdListRepository;
import my.board.articleread.repository.ArticleQueryModel;
import my.board.articleread.repository.ArticleQueryModelRepository;
import my.board.articleread.repository.BoardArticleCountRepository;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {

	private final ArticleQueryModelRepository articleQueryModelRepository;

	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Override
	public void handle(Event<ArticleCreatedEventPayload> event) {
		ArticleCreatedEventPayload payload = event.getPayload();
		// 해당 event 를 받아서 .. ArticleQueryModel 객체로 변환 후 Redis에 저장 .
		articleQueryModelRepository.create(
			ArticleQueryModel.create(payload),
			Duration.ofDays(1)
		);
		articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
		boardArticleCountRepository.createOrUpdate(payload.getBoardId(),
			payload.getBoardArticleCount());
	}

	@Override
	public boolean supports(Event<ArticleCreatedEventPayload> event) {
		return event.getType() == EventType.ARTICLE_CREATED;
	}
}
