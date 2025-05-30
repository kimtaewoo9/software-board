package my.board.like.controller;

import lombok.RequiredArgsConstructor;
import my.board.like.service.ArticleLikeService;
import my.board.like.service.response.ArticleLikeResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {

	private final ArticleLikeService articleLikeService;

	@GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
	public ArticleLikeResponse read(
		@PathVariable("articleId") Long articleId,
		@PathVariable("userId") Long userId
	) {
		return articleLikeService.read(articleId, userId);
	}

	@PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
	public ArticleLikeResponse like(
		@PathVariable("articleId") Long articleId,
		@PathVariable("userId") Long userId
	) {
		return articleLikeService.like(articleId, userId);
	}

	@DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
	public void unlike(
		@PathVariable("articleId") Long articleId,
		@PathVariable("userId") Long userId
	) {
		articleLikeService.unlike(articleId, userId);
	}
}
