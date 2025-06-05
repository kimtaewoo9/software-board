package my.board.articleread.service.event.handler;


import my.board.common.event.Event;
import my.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {

	void handle(Event<T> event);

	boolean supports(Event<T> event);
}
