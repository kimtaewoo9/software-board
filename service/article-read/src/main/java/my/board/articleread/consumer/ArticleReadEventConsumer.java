package my.board.articleread.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.articleread.service.ArticleReadService;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import my.board.common.event.EventType.Topic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleReadEventConsumer {

	private final ArticleReadService articleReadService;

	@KafkaListener(topics = {
		Topic.SOFTWARE_BOARD_ARTICLE,
		Topic.SOFTWARE_BOARD_COMMENT,
		Topic.SOFTWARE_BOARD_LIKE,
		Topic.SOFTWARE_BOARD_VIEW
	})
	public void listen(String message, Acknowledgment ack) {
		log.info("[ArticleReadEventConsumer.listen] messagae = {}", message);
		Event<EventPayload> event = Event.fromJson(message);
		if (event != null) {
			// 카프카로 부터 수신산 이벤트를 articleReadService 에 전달함.
			articleReadService.handleEvent(event);
		}
		ack.acknowledge(); // 메시지 처리가 완료 되었음을 알림 .
	}
}
