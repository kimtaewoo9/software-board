package my.board.hotarticle.service.eventhandler;

import my.board.common.event.Event;
import my.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {

	// 어떤 event pay load 에 대한 .. handler 인지
	void handle(Event<T> event);

	boolean supports(Event<T> event);

	Long findArticleId(Event<T> event);
}
