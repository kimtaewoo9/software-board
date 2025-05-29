package my.board.comment.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import my.board.comment.service.CommentService;
import my.board.comment.service.request.CommentCreateRequest;
import my.board.comment.service.response.CommentPageResponse;
import my.board.comment.service.response.CommentResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/v1/comments/{commentId}")
	public CommentResponse read(@PathVariable Long commentId) {
		return commentService.read(commentId);
	}

	@PostMapping("/v1/comments")
	public CommentResponse create(@RequestBody CommentCreateRequest commentCreateRequest) {
		return commentService.create(commentCreateRequest);
	}

	@DeleteMapping("/v1/comments/{commentId}")
	public void delete(@PathVariable Long commentId) {
		commentService.delete(commentId);
	}

	@GetMapping("/v1/comments")
	public CommentPageResponse readAll(
		@RequestParam(value = "articleId") Long articleId,
		@RequestParam(value = "page") Long page, // 페이지 번호
		@RequestParam(value = "pageSize") Long pageSize // 페이지 사이즈
	) {
		return commentService.readAll(articleId, page, pageSize);
	}

	@GetMapping("/v1/comments/infinite-scroll")
	public List<CommentResponse> readAll(
		@RequestParam("articleId") Long articleId,
		@RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
		@RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
		@RequestParam("pageSize") Long pageSize
	) {
		return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
	}
}
