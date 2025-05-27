package my.board.comment.service.response;

import java.time.LocalDateTime;
import lombok.Data;
import my.board.comment.comment.Comment;

@Data
public class CommentResponse {

    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.commentId = comment.getCommentId();
        commentResponse.content = comment.getContent();
        commentResponse.parentCommentId = comment.getParentCommentId();
        commentResponse.articleId = comment.getArticleId();
        commentResponse.writerId = comment.getWriterId();
        commentResponse.deleted = comment.getDelete();
        commentResponse.createdAt = comment.getCreatedAt();
        
        return commentResponse;
    }
}
