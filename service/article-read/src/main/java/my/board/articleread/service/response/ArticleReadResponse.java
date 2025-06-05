package my.board.articleread.service.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;
import my.board.articleread.repository.ArticleQueryModel;

@Getter
@ToString
public class ArticleReadResponse {

	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Long articleCommentCount;
	private Long articleLikeCount;
	private Long articleViewCount;

	// articleQueryModel이랑 .조회수 서비스에서 가져온 view count
	public static ArticleReadResponse from(
		ArticleQueryModel articleQueryModel,
		Long viewCount) {
		ArticleReadResponse articleReadResponse = new ArticleReadResponse();
		articleReadResponse.articleId = articleQueryModel.getArticleId();
		articleReadResponse.title = articleQueryModel.getTitle();
		articleReadResponse.content = articleQueryModel.getContent();
		articleReadResponse.boardId = articleQueryModel.getBoardId();
		articleReadResponse.writerId = articleQueryModel.getWriterId();
		articleReadResponse.createdAt = articleQueryModel.getCreatedAt();
		articleReadResponse.updatedAt = articleQueryModel.getUpdatedAt();

		articleReadResponse.articleCommentCount = articleQueryModel.getArticleCommentCount();
		articleReadResponse.articleLikeCount = articleQueryModel.getArticleLikeCount();
		articleReadResponse.articleViewCount = viewCount;

		return articleReadResponse;
	}
}
