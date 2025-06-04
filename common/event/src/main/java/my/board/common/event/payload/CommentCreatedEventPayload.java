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
public class CommentCreatedEventPayload implements EventPayload {

	private Long commentId;
	private String content;
	private Long parentCommentId; // v1 버전 사용
	private Long articleId;
	private Long writerId;
	private Boolean deleted;
	private LocalDateTime createdAt;
	private Long articleCommentCount;
}
