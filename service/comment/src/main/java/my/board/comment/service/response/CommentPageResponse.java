package my.board.comment.service.response;

import java.util.List;
import lombok.Data;

@Data
public class CommentPageResponse {

	private List<CommentResponse> comments;
	private Long commentCount; // -> 이동 가능한 페이지 번호 활성화에 필요한 요소 .

	public static CommentPageResponse of(List<CommentResponse> comments, Long commentCount) {
		CommentPageResponse commentPageResponse = new CommentPageResponse();
		commentPageResponse.comments = comments;
		commentPageResponse.commentCount = commentCount;

		return commentPageResponse;
	}
}
