package my.board.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "comment")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

	@Id
	private Long commentId;
	private String content;
	private Long parentCommentId;
	private Long articleId; // shard key
	private Long writerId;
	private Boolean deleted;
	private LocalDateTime createdAt;

	public static Comment create(Long commentId, String content, Long parentCommentId,
		Long articleId, Long writerId) {

		Comment comment = new Comment();
		comment.commentId = commentId;
		comment.content = content;
		comment.parentCommentId = parentCommentId == null ? commentId : parentCommentId;
		comment.articleId = articleId;
		comment.writerId = writerId;
		comment.deleted = false;
		comment.createdAt = LocalDateTime.now();

		return comment;
	}

	public Boolean isRoot() {
		return parentCommentId.longValue() == commentId;
	}

	public void delete() {
		deleted = true;
	}
}
