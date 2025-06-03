package my.board.hotarticle.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import my.board.hotarticle.repository.ArticleCreatedTimeRepository;
import my.board.hotarticle.repository.HotArticleListRepository;
import my.board.hotarticle.service.eventhandler.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater {

	private final HotArticleListRepository hotArticleListRepository;
	private final HotArticleScoreCalculator hotArticleScoreCalculator;
	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

	private static final long HOT_ARTICLE_COUNT = 10;
	private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);

	public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {
		Long articleId = eventHandler.findArticleId(event);
		LocalDateTime createdTime = articleCreatedTimeRepository.read(articleId);

		if (!isArticleCreatedToday(createdTime)) {
			return;
		}

		eventHandler.handle(event);

		long score = hotArticleScoreCalculator.calculate(articleId);
		hotArticleListRepository.add(
			articleId,
			createdTime,
			score,
			HOT_ARTICLE_COUNT, // 최대 몇개 저장할 건지 .
			HOT_ARTICLE_TTL // 얼마나 저장할 건지
		);

	}

	private boolean isArticleCreatedToday(LocalDateTime createdTime) {
		return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now());
	}
}
