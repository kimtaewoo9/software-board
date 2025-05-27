package my.board.article.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.article.service.ArticleService;
import my.board.article.service.request.ArticleCreateRequest;
import my.board.article.service.request.ArticleUpdateRequest;
import my.board.article.service.response.ArticlePageResponse;
import my.board.article.service.response.ArticleResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(
        @PathVariable Long articleId
    ) {
        return articleService.read(articleId);
    }

    @PostMapping("/v1/articles")
    public ArticleResponse create(
        @RequestBody ArticleCreateRequest articleCreateRequest) {
        log.info("article create request");
        return articleService.create(articleCreateRequest);
    }

    @PostMapping("/v1/articles/{articleId}")
    public ArticleResponse update(
        @PathVariable Long articleId,
        @RequestBody ArticleUpdateRequest articleUpdateRequest
    ) {
        return articleService.update(articleId, articleUpdateRequest);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(
        @PathVariable Long articleId
    ) {
        articleService.delete(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse findAll(
        @RequestParam("boardId") Long boardId,
        @RequestParam("page") Long page,
        @RequestParam("pageSize") Long pageSize
    ) {
        return articleService.readAll(boardId, page, pageSize);
    }

    @GetMapping("/v1/articles/infinite-scroll")
    public List<ArticleResponse> findAllInfiniteScroll(
        @RequestParam("boardId") Long boardId,
        @RequestParam("pageSize") Long pageSize,
        @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
    ) {
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }
}
