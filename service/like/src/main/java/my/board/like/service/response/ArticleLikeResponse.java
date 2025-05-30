package my.board.like.service.response;

import java.time.LocalDateTime;
import lombok.Data;
import my.board.like.entity.ArticleLike;

@Data
public class ArticleLikeResponse {

	private Long articleLikeId;
	private Long articleId;
	private Long userId;
	private LocalDateTime createdAt;

	public static ArticleLikeResponse from(ArticleLike articleLike) {
		ArticleLikeResponse articleLikeResponse = new ArticleLikeResponse();
		articleLikeResponse.articleLikeId = articleLike.getArticleLikeId();
		articleLikeResponse.articleId = articleLike.getArticleId();
		articleLikeResponse.userId = articleLike.getUserId();
		articleLikeResponse.createdAt = articleLike.getCreatedAt();

		return articleLikeResponse;
	}
}
