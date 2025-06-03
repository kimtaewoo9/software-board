package my.board.hotarticle.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.stream.Stream;
import my.board.common.event.Event;
import my.board.common.event.EventType;
import my.board.hotarticle.repository.ArticleCreatedTimeRepository;
import my.board.hotarticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotArticleServiceTest {

	@InjectMocks
	HotArticleService hotArticleService;
	@Mock
	List<EventHandler> eventHandlers;
	@Mock
	HotArticleScoreUpdater hotArticleScoreUpdater;
	@Mock
	ArticleCreatedTimeRepository articleCreatedTimeRepository;

	@Test
	void handleEventIfEventHandlerNotFountTest() {
		Event event = mock(Event.class);
		EventHandler eventHandler = mock(EventHandler.class);

		given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));
		given(eventHandler.supports(event)).willReturn(false);

		// when
		hotArticleService.handleEvent(event);

		// then
		verify(eventHandler, never()).handle(event);
		verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
	}

	@Test
	@DisplayName("event type이 ARTICLE_CREATED")
	void handleEventIfArticleCreated() {
		Event event = mock(Event.class);
		given(event.getType()).willReturn(EventType.ARTICLE_CREATED);

		// 이 event handler 가 event type 을 지원한다고 해야함 (true)
		EventHandler eventHandler = mock(EventHandler.class);
		given(eventHandler.supports(event)).willReturn(true);
		given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

		// when
		hotArticleService.handleEvent(event);

		// then
		verify(eventHandler).handle(event);
		verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
	}

	@Test
	@DisplayName("event type이 ARTICLE_DELETED")
	void handleEventIfArticleDeleteEvent() {
		Event event = mock(Event.class);
		given(event.getType()).willReturn(EventType.ARTICLE_DELETED);

		EventHandler eventHandler = mock(EventHandler.class);
		given(eventHandler.supports(event)).willReturn(true); // 해당 이벤트를 support 한다고 응답.
		given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

		// when
		hotArticleService.handleEvent(event);

		// then
		verify(eventHandler).handle(event);
		verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
	}

	@Test
	@DisplayName("")
	void handleEventIfScoreUpdatableEventTest() {
		Event event = mock(Event.class);
		given(event.getType()).willReturn(mock(EventType.class));

		EventHandler eventHandler = mock(EventHandler.class);
		given(eventHandler.supports(event)).willReturn(true);
		given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

		// when
		hotArticleService.handleEvent(event);

		// then
		verify(eventHandler, never()).handle(event);
		verify(hotArticleScoreUpdater).update(event, eventHandler);
	}
}
