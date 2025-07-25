package my.board.common.event.payload;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import my.board.common.event.EventPayload;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDeletedEventPayload implements EventPayload {

	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long boardArticleCount;
}
