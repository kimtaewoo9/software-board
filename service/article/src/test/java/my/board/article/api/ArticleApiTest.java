package my.board.article.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import my.board.article.repository.ArticleRepository;
import my.board.article.service.response.ArticlePageResponse;
import my.board.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleCreateRequest articleCreateRequest =
            new ArticleCreateRequest("title", "content", 1L, 1L);

        ArticleResponse articleResponse = create(articleCreateRequest);
        System.out.println("articleResponse = " + articleResponse);
    }

    @Test
    void readTest() {
        ArticleResponse articleResponse = read(185618548293066752L);
        System.out.println("articleResponse = " + articleResponse);

    }

    @Test
    void updateTest() {
        ArticleUpdateRequest articleUpdateRequest
            = new ArticleUpdateRequest("newTitle", "newContent");
        ArticleResponse articleResponse = update(185618548293066752L, articleUpdateRequest);

        System.out.println("articleResponse = " + articleResponse);
    }

    @Test
    void deleteTest() {
        delete(185618548293066752L);
    }

    ArticleResponse create(ArticleCreateRequest articleCreateRequest) {
        return restClient.post()
            .uri("/v1/articles")
            .body(articleCreateRequest)
            .retrieve()
            .body(ArticleResponse.class);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse.class);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequest articleUpdateRequest) {
        return restClient.post()
            .uri("/v1/articles/{articleId}", articleId)
            .body(articleUpdateRequest)
            .retrieve()
            .body(ArticleResponse.class);
    }

    void delete(Long articleId) {
        restClient.delete()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleRepository.class);
    }

    @Test
    void readAllTest() {
        ArticlePageResponse articlePageResponse =
            restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve() // retrieve 를 통해 🔥 실제 HTTP 요청 실행
                .body(ArticlePageResponse.class);

        System.out.println(
            "articlePageResponse.getArticleCount(): " + articlePageResponse.getArticleCount());
        List<ArticleResponse> articles = articlePageResponse.getArticles();
        for (ArticleResponse article : articles) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> articleResponses = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
            });

        System.out.println("[first page]");
        for (ArticleResponse articleResponse : articleResponses) {
            System.out.println("article_id: " + articleResponse.getArticleId());
        }

        Long lastArticleId = articleResponses.getLast().getArticleId();
        List<ArticleResponse> articleResponse2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30&lastArticleId=%s".formatted(
                lastArticleId))
            .retrieve()
            .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
            });

        System.out.println("[second page]");
        for (ArticleResponse articleResponse : articleResponse2) {
            System.out.println("article_id: " + articleResponse.getArticleId());
        }
    }

    @Data
    @AllArgsConstructor
    static class ArticleCreateRequest {

        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Data
    @AllArgsConstructor
    static class ArticleUpdateRequest {

        private String title;
        private String content;
    }
}
