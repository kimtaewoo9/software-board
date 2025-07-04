package my.board.article.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import my.board.article.document.ArticleDocument;
import my.board.article.service.ArticleSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleSearchController {

	private final ArticleSearchService articleSearchService;

	@GetMapping("/v1/articles/search")
	public List<ArticleDocument> search(
		@RequestParam("query") String query,
		@RequestParam("boardId") Long boardId,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "pageSize", defaultValue = "10") int pageSize
	) {
		return articleSearchService.searchArticles(query, boardId, page, pageSize);
	}

	
}
