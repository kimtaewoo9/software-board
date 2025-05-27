package my.board.article.service.response;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticlePageResponse {

    private List<ArticleResponse> articles;
    private Long articleCount; // '필요한' 게시글의 개수

    public static ArticlePageResponse of(List<ArticleResponse> articles, Long articleCount) {
        ArticlePageResponse articleResponse = new ArticlePageResponse();
        articleResponse.articles = articles;
        articleResponse.articleCount = articleCount;
        return articleResponse;
    }
}
