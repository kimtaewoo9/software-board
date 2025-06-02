package my.board.common.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import my.board.common.event.payload.ArticleCreateEventPayload;
import org.junit.jupiter.api.Test;

public class eventTest {

	@Test
	void eventTest() {

		Long articleId = 1L;
		Long boardId = 1L;
		Long writerID = 1L;

		ArticleCreateEventPayload payload = ArticleCreateEventPayload.builder()
			.articleId(articleId)
			.title("title")
			.content("conent")
			.boardId(boardId)
			.writerId(writerID)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.boardArticleCount(23L)
			.build();

		Event<EventPayload> event = Event.of(
			1234L,
			EventType.ARTICLE_CREATED, // 어느 topic 으로 갈지 이 event type 에 저장 되어 있음 .
			payload
		);

		String json = event.toJson(); // event 객체를 byte array로 바꿔서 전송 .(직렬화)
		System.out.println("json = " + json);

		// when .. 받은 json 을 event 객체로 변환 .
		Event<EventPayload> result = Event.fromJson(json);

		// then
		assertThat(result.getEventId()).isEqualTo(event.getEventId());
		assertThat(result.getType()).isEqualTo(event.getType());
		assertThat(result.getPayload()).isInstanceOf(payload.getClass());

		ArticleCreateEventPayload resultPayload = (ArticleCreateEventPayload) result.getPayload();

		assertThat(resultPayload.getArticleId()).isEqualTo(payload.getArticleId());
		assertThat(resultPayload.getTitle()).isEqualTo(payload.getTitle());
		assertThat(resultPayload.getContent()).isEqualTo(payload.getContent());
		assertThat(resultPayload.getCreatedAt()).isEqualTo(payload.getCreatedAt());
	}
}
