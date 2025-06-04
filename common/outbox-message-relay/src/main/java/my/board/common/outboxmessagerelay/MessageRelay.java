package my.board.common.outboxmessagerelay;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageRelay {

	private final OutboxRepository outboxRepository;
	private final MessageRelayCoordinator messageRelayCoordinator;
	private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	// publisher 에게 받은 이벤트를 .. outbox 로 만든다 .
	// 이벤트 발생 알림을 받아서 outbox 를 만든다 .
	public void createOutbox(OutboxEvent outboxEvent) {
		log.info("✅ [MessageRelay.createOutbox] outboxEvent={}", outboxEvent);
		outboxRepository.save(outboxEvent.getOutbox());
	}

	@Async("messageRelayPublishEventExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	// event 발생 알림을 받아서 .event 를 kafka 에게 전송한다.
	// outbox 에서 10초 마다 가져오기도 하지만 .. 직접 전송을 함.
	public void publishEvent(OutboxEvent outboxEvent) {
		publishEvent(outboxEvent.getOutbox());
	}

	private void publishEvent(Outbox outbox) {
		try {
			// 1. 카프카로 메시지 전송 . 최대 1초대기
			messageRelayKafkaTemplate.send(
				outbox.getEventType().getTopic(),
				String.valueOf(outbox.getShardKey()),
				outbox.getPayload()
			).get(1, TimeUnit.SECONDS);

			// 2. 전송 성공시 Outbox 에서 해당 이벤트 삭제 ..
			outboxRepository.delete(outbox);
		} catch (Exception e) {
			log.error("[MessageRelay.publishEvent] outbox={}", outbox, e);
		}
	}

	@Scheduled(
		fixedDelay = 60, // TODO test 후 10초 마다로 변경
		initialDelay = 5,
		timeUnit = TimeUnit.SECONDS,
		scheduler = "messageRelayPublishPendingEventExecutor"
	)
	public void publishPendingEvent() {
		AssignedShard assignedShard = messageRelayCoordinator.assignShards();
		log.info("[MessageRelay.publishPendingEvent] assignedShard size={}",
			assignedShard.getShards().size());
		// 처리할 샤드를 지정해주어야함 .
		for (Long shard : assignedShard.getShards()) {
			List<Outbox> outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
				shard,
				LocalDateTime.now().minusSeconds(10),
				Pageable.ofSize(100)
			);
			for (Outbox outbox : outboxes) {
				publishEvent(outbox);
			}
		}
	}
}
