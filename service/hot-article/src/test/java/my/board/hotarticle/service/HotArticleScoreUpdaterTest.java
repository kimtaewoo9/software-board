package my.board.hotarticle.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;
import my.board.common.event.Event;
import my.board.hotarticle.repository.ArticleCreatedTimeRepository;
import my.board.hotarticle.repository.HotArticleListRepository;
import my.board.hotarticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotArticleScoreUpdaterTest {

	@InjectMocks
	HotArticleScoreUpdater hotArticleScoreUpdater;
	@Mock
	HotArticleListRepository hotArticleListRepository;
	@Mock
	HotArticleScoreCalculator hotArticleScoreCalculator;
	@Mock
	ArticleCreatedTimeRepository articleCreatedTimeRepository;

	@Test
	@DisplayName("article 이 오늘 생성 된 것이 아니면 update 하지 않아야함 .")
	void updateIfArticleNotCreatedTodayTest() {
		// given
		Long articleId = 1L;
		Event event = mock(Event.class);
		EventHandler eventHandler = mock(EventHandler.class);

		given(eventHandler.findArticleId(event)).willReturn(articleId);

		// 좋아요, 조회수, 댓글에 대한 이벤트고 .. article 이 생성 된 시간이 어제라면 .update 하지 않음 .
		LocalDateTime createdAt = LocalDateTime.now().minusDays(1); // 어제 생성된 게시글
		given(articleCreatedTimeRepository.read(articleId)).willReturn(createdAt);

		// when
		hotArticleScoreUpdater.update(event, eventHandler);

		// then
		verify(eventHandler, never()).handle(event);
		verify(hotArticleListRepository, never())
			.add(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
	}

	@Test
	void updateTest() {

		// given
		Long articleId = 1L;
		Event event = mock(Event.class);
		EventHandler eventHandler = mock(EventHandler.class);

		given(eventHandler.findArticleId(event)).willReturn(articleId);

		LocalDateTime createdAt = LocalDateTime.now(); // 현재 시간 .
		given(articleCreatedTimeRepository.read(articleId)).willReturn(createdAt);

		// when
		hotArticleScoreUpdater.update(event, eventHandler);

		// then
		verify(eventHandler).handle(event);
		verify(hotArticleListRepository)
			.add(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
	}
}
