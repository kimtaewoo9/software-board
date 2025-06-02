package my.board.common.event;

import lombok.Getter;
import my.board.common.dataserializer.DataSerializer;

@Getter
public class Event<T extends EventPayload> {

	private Long eventId;
	private EventType type; // payloadClass, topic 필드를 갖는다. 어떤 데이터를 어떤 토픽으로 보낼 것인지 .
	private T payload;

	// event 객체 생성 메서드 .
	public static Event<EventPayload> of(Long eventId, EventType type, EventPayload payload) {
		Event<EventPayload> event = new Event<>();
		event.eventId = eventId;
		event.type = type;
		event.payload = payload;

		return event;
	}

	public String toJson() {
		return DataSerializer.serialize(this);
	}

	// 받아온 json 을 자바 객체로 역직렬화 .
	public static Event<EventPayload> fromJson(String json) {
		// json 을 eventRaw 로 역직렬화 .
		EventRaw eventRaw = DataSerializer.deserialize(json, EventRaw.class);
		if (eventRaw == null) {
			return null;
		}

		Event<EventPayload> event = new Event<>();
		event.eventId = eventRaw.eventId;
		event.type = EventType.from(eventRaw.getType());
		event.payload = DataSerializer.deserialize(
			eventRaw.getPayload(),
			event.type.getPayloadClass());

		return event;

	}

	@Getter
	public static class EventRaw {

		private Long eventId;
		private String type;
		private Object payload;
	}
}
