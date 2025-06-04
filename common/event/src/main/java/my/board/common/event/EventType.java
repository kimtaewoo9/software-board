package my.board.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.event.payload.ArticleCreatedEventPayload;
import my.board.common.event.payload.ArticleDeletedEventPayload;
import my.board.common.event.payload.ArticleLikedEventPayload;
import my.board.common.event.payload.ArticleUnlikedEventPayload;
import my.board.common.event.payload.ArticleUpdatedEventPayload;
import my.board.common.event.payload.ArticleViewedEventPayload;
import my.board.common.event.payload.CommentCreatedEventPayload;
import my.board.common.event.payload.CommentDeletedEventPayload;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {

	// event type 정보를 통해 어떤 종류의 이벤트인지 구분할 수 있음 .
	ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.SOFTWARE_BOARD_ARTICLE),
	COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.SOFTWARE_BOARD_COMMENT),
	COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.SOFTWARE_BOARD_COMMENT),
	ARTICLE_LIKED(ArticleLikedEventPayload.class, Topic.SOFTWARE_BOARD_LIKE),
	ARTICLE_UNLIKED(ArticleUnlikedEventPayload.class, Topic.SOFTWARE_BOARD_LIKE),
	ARTICLE_VIEWED(ArticleViewedEventPayload.class, Topic.SOFTWARE_BOARD_VIEW);

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
