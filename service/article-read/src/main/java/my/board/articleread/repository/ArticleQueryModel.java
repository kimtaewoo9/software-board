package my.board.articleread.repository;

import java.time.LocalDateTime;
import lombok.Getter;
import my.board.articleread.client.ArticleClient;
import my.board.common.event.payload.ArticleCreatedEventPayload;
import my.board.common.event.payload.ArticleLikedEventPayload;
import my.board.common.event.payload.ArticleUnlikedEventPayload;
import my.board.common.event.payload.ArticleUpdatedEventPayload;
import my.board.common.event.payload.CommentCreatedEventPayload;
import my.board.common.event.payload.CommentDeletedEventPayload;

@Getter
public class ArticleQueryModel {

	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// 댓글 수와 .. 좋아요 수도 비정규화해서 하나로 가지고 있음 .
	private Long articleCommentCount;
	private Long articleLikeCount;

	public static ArticleQueryModel create(ArticleCreatedEventPayload payload) {
		ArticleQueryModel articleQueryModel = new ArticleQueryModel();
		articleQueryModel.articleId = payload.getArticleId();
		articleQueryModel.title = payload.getTitle();
		articleQueryModel.content = payload.getContent();
		articleQueryModel.boardId = payload.getBoardId();
		articleQueryModel.writerId = payload.getWriterId();
		articleQueryModel.createdAt = payload.getCreatedAt();
		articleQueryModel.updatedAt = payload.getUpdatedAt();

		articleQueryModel.articleCommentCount = 0L;
		articleQueryModel.articleLikeCount = 0L;

		return articleQueryModel;
	}

	// redis 에 데이터가 없으면 .. 클라이언트 클래스들로 command 에 요청을 해야함 .
	// command server 에서 받아온 데이터로 article query model 을 만들어야함.

	public static ArticleQueryModel create(ArticleClient.ArticleResponse articleResponse,
		Long articleCommentCount, Long articleLikeCount) {
		ArticleQueryModel articleQueryModel = new ArticleQueryModel();
		articleQueryModel.articleId = articleResponse.getArticleId();
		articleQueryModel.content = articleResponse.getContent();
		articleQueryModel.title = articleResponse.getTitle();
		articleQueryModel.boardId = articleResponse.getBoardId();
		articleQueryModel.writerId = articleResponse.getWriterId();
		articleQueryModel.createdAt = articleResponse.getCreatedAt();
		articleQueryModel.updatedAt = articleResponse.getUpdatedAt();

		articleQueryModel.articleCommentCount = articleCommentCount;
		articleQueryModel.articleLikeCount = articleLikeCount;

		return articleQueryModel;
	}

	public void updateBy(CommentCreatedEventPayload payload) {
		this.articleCommentCount = payload.getArticleCommentCount();
	}

	public void updateBy(CommentDeletedEventPayload payload) {
		this.articleCommentCount = payload.getArticleCommentCount();
	}

	public void updateBy(ArticleLikedEventPayload payload) {
		this.articleLikeCount = payload.getArticleLikeCount();
	}

	public void updateBy(ArticleUnlikedEventPayload payload) {
		this.articleLikeCount = payload.getArticleLikeCount();
	}

	public void updateBy(ArticleUpdatedEventPayload payload) {
		this.title = payload.getTitle();
		this.content = payload.getContent();
		this.boardId = payload.getBoardId();
		this.writerId = payload.getWriterId();
		this.createdAt = payload.getCreatedAt();
		this.updatedAt = payload.getUpdatedAt();
	}
}
