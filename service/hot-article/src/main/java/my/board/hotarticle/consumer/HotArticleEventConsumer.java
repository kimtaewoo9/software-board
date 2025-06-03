package my.board.hotarticle.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import my.board.common.event.EventType.Topic;
import my.board.hotarticle.service.HotArticleService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotArticleEventConsumer {

	private final HotArticleService hotArticleService;

	@KafkaListener(topics = {
		Topic.SOFTWARE_BOARD_ARTICLE, // software-board-article
		Topic.SOFTWARE_BOARD_COMMENT, // software-board-comment
		Topic.SOFTWARE_BOARD_VIEW, // software-board-like
		Topic.SOFTWARE_BOARD_LIKE // software-board-view
	})
	public void listen(String message, Acknowledgment ack) {
		Event<EventPayload> event = Event.fromJson(message); // 전달된 메시지가 json 이므로  파싱함.
		if (event != null) {
			hotArticleService.handleEvent(event);
		}
		ack.acknowledge(); // 메시지 처리가 성공적으 완료 되었음을 카프카에게 알림 .
	}
}
