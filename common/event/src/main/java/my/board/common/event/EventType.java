package my.board.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.event.payload.ArticleCreateEventPayload;
import my.board.common.event.payload.ArticleDeleteEventPayload;
import my.board.common.event.payload.ArticleLikeEventPayload;
import my.board.common.event.payload.ArticleUnlikeEventPayload;
import my.board.common.event.payload.ArticleUpdateEventPayload;
import my.board.common.event.payload.ArticleViewEventPayload;
import my.board.common.event.payload.CommentCreateEventPayload;
import my.board.common.event.payload.CommentDeleteEventPayload;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {

	// event type 정보를 통해 어떤 종류의 이벤트인지 구분할 수 있음 .
	ARTICLE_CREATED(ArticleCreateEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	ARTICLE_UPDATED(ArticleUpdateEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	ARTICLE_DELETED(ArticleDeleteEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	COMMENT_CREATED(CommentCreateEventPayload.class, Topic.SOFTWARE_BOARD_COMMENT),
	COMMENT_DELETED(CommentDeleteEventPayload.class, Topic.SOFTWARE_BOARD_COMMENT),
	ARTICLE_LIKED(ArticleLikeEventPayload.class, Topic.SOFTWARE_BOARD_LIKE),
	ARTICLE_UNLIKED(ArticleUnlikeEventPayload.class, Topic.SOFTWARE_BOARD_LIKE),
	ARTICLE_VIEWED(ArticleViewEventPayload.class, Topic.SOFTWARE_BOARD_VIEW);

	private final Class<? extends EventPayload> payloadClass; // payload 의 클래스의 타입 정보 저장 .
	// 카프카 메시지를 받을 때 어떤 클래스로 변환할지 결정 .
	private final String topic; // event 들이 어떤 토픽으로 전달 되어야하는지 저장 .

	// string 을 받아서 EventType 객체를 만들어주는 메서드 .
	public static EventType from(String type) {
		try {
			return valueOf(type);
		} catch (Exception e) {
			log.error("[EventType.from] type={}", type, e);
			return null;
		}
	}

	public static class Topic {

		public static final String SOFTWARE_BOARD_ARTICLE = "software-board-article";
		public static final String SOFTWARE_BOARD_COMMENT = "software-board-comment";
		public static final String SOFTWARE_BOARD_LIKE = "software-board-like";
		public static final String SOFTWARE_BOARD_VIEW = "software-board-view";
	}
}
