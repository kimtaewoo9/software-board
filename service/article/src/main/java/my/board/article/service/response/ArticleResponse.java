package my.board.article.service.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import my.board.article.entity.Article;

@Data
@Builder
public class ArticleResponse {

    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleResponse from(Article article) {
        return ArticleResponse.builder()
            .articleId(article.getArticleId())
            .title(article.getTitle())
            .content(article.getContent())
            .boardId(article.getBoardId())
            .writerId(article.getWriterId())
            .createdAt(article.getCreatedAt())
            .updatedAt(article.getUpdatedAt())
            .build();
    }
}
