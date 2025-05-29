package my.board.comment.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreateRequest {

	private String content;
	private Long parentCommentId;
	private Long articleId;
	private Long writerId;
}
