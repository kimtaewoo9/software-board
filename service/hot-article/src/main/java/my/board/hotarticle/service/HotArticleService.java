package my.board.hotarticle.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import my.board.common.event.EventType;
import my.board.hotarticle.client.ArticleClient;
import my.board.hotarticle.client.ArticleClient.ArticleResponse;
import my.board.hotarticle.repository.HotArticleListRepository;
import my.board.hotarticle.service.eventhandler.EventHandler;
import my.board.hotarticle.service.response.HotArticleResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotArticleService {

	private final ArticleClient articleClient;
	private final List<EventHandler> eventHandlers; // EventHandler 를 구현한 모든 빈을 여기에 넣어줌 ,.
	private final HotArticleScoreUpdater hotArticleScoreUpdater;
	private final HotArticleListRepository hotArticleListRepository;

	public void handleEvent(Event<EventPayload> event) {
		EventHandler<EventPayload> eventHandler = findEventHandler(event);
		if (eventHandler == null) {
			return;
		}

		if (isArticleCreateOrDeleted(event)) {
			eventHandler.handle(event);
		} else {
			hotArticleScoreUpdater.update(event, eventHandler);
		}
	}

	private boolean isArticleCreateOrDeleted(Event<EventPayload> event) {
		return EventType.ARTICLE_CREATED == event.getType()
			|| EventType.ARTICLE_DELETED == event.getType();
	}

	private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
		return eventHandlers.stream()
			.filter(eventHandler -> eventHandler.supports(event))
			.findAny()
			.orElse(null);
	}

	public List<HotArticleResponse> readAll(String dateStr) {
		List<ArticleResponse> articleResponses = hotArticleListRepository.readAll(dateStr)
			.stream()
			.map(articleClient::read)
			.toList();

		for (ArticleResponse articleResponse : articleResponses) {
			System.out.println("✅ " + articleResponse);
		}

		return hotArticleListRepository.readAll(dateStr).stream()
			.map(articleClient::read)
			.filter(Objects::nonNull)
			.map(HotArticleResponse::from)
			.toList();
	}
}
