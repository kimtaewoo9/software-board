package my.board.comment.service.request;

import lombok.Data;

@Data
public class CommentCreateRequest {

    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
}
