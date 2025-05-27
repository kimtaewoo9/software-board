package my.board.article.service.request;

import lombok.Data;

@Data
public class ArticleUpdateRequest {
    private String title;
    private String content;
}
