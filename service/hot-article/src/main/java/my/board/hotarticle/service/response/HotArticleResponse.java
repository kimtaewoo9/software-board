package my.board.hotarticle.service.response;

import java.time.LocalDateTime;
import lombok.Getter;
import my.board.hotarticle.client.ArticleClient;

@Getter
public class HotArticleResponse {

	private Long articleId;
	private String title;
	private LocalDateTime createdAt;

	// article client 에서 .. article response 를 받아왔었음
	public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
		HotArticleResponse response = new HotArticleResponse();
		response.articleId = articleResponse.getArticleId();
		response.title = articleResponse.getTitle();
		response.createdAt = articleResponse.getCreatedAt();

		return response;
	}
}
