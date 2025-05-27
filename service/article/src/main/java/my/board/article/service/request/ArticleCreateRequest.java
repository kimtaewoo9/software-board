package my.board.article.service.request;

import lombok.Data;

@Data
public class ArticleCreateRequest {

    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
}
