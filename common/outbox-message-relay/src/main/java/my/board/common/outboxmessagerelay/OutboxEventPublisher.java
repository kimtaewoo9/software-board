package my.board.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.event.Event;
import my.board.common.event.EventPayload;
import my.board.common.event.EventType;
import my.board.common.snowflake.Snowflake;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

	private final Snowflake outboxIdSnowflake = new Snowflake();
	private final Snowflake eventIdSnowflake = new Snowflake();
	private final ApplicationEventPublisher applicationEventPublisher;

	// article, comment, view, like 등 이벤트가 발생했을때 호출되는 메서드 .
	public void publish(EventType type, EventPayload payload, Long shardKey) {
		// 이벤트 객체 생성 .
		log.info("✅ [OutboxEventPublisher.publish] EventTYPE: {}", type);
		String newEvent = Event.of(
			eventIdSnowflake.nextId(),
			type,
			payload).toJson();

		// outbox 객체 생성 .
		Outbox outbox = Outbox.create(
			outboxIdSnowflake.nextId(),
			type,
			newEvent,
			shardKey % MessageRelayConstants.SHARD_COUNT
		);

		// 이건 messageRelay 라고 다른 데서 이걸 수신해서 처리할 거임 .
		// OutboxEvent 를 리스닝 하는 다른 컴포넌트가 있을 것이고, outbox 정보를 받아 데이터 베이스 아웃박스 테이블에 저장할 것임.
		// transactional event listener 에게 .. 이벤트를 전송함 .
		applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
	}
}
