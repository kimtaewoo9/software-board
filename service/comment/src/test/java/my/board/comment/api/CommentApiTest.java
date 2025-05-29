package my.board.comment.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import my.board.comment.service.response.CommentPageResponse;
import my.board.comment.service.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@Slf4j
public class CommentApiTest {

	RestClient restClient = RestClient.create("http://localhost:9001");

	@Test
	void create() {
		CommentCreateRequest request1 =
			new CommentCreateRequest("댓글 1", null, 1L, 1L);
		CommentResponse response1 = createComment(request1);

		CommentCreateRequest request2 =
			new CommentCreateRequest("댓글 2", response1.getCommentId(), 1L, 1L);
		CommentCreateRequest request3 =
			new CommentCreateRequest("댓글 3", response1.getCommentId(), 1L, 1L);

		log.info("response1: {}", response1.getCommentId());

		CommentResponse response2 = createComment(request2);
		CommentResponse response3 = createComment(request3);
	}

	CommentResponse createComment(CommentCreateRequest commentCreateRequest) {
		return restClient.post()
			.uri("/v1/comments")
			.body(commentCreateRequest)
			.retrieve()
			.body(CommentResponse.class);
	}

	@Test
	@DisplayName("API 테스트 엔드포인트: /v1/comments/{commentId}")
	void read() {
		CommentResponse commentResponse = restClient.get()
			.uri("/v1/comments/{commentId}", 186397623446294528L)
			.retrieve()
			.body(CommentResponse.class);

		log.info("response: {}", commentResponse);
	}

	@Test
	void delete() {
		restClient.delete()
			.uri("/v1/comments/{commentId}", 186397623446294528L)
			.retrieve();
	}

	@Test
	void readAll() {
		CommentPageResponse commentPageResponse = restClient.get()
			.uri("/v1/comments?articleId=1&page=1&pageSize=10")
			.retrieve()
			.body(CommentPageResponse.class);

		System.out.println("response.getCommentCount() = " + commentPageResponse.getCommentCount());
		for (CommentResponse commentResponse : commentPageResponse.getComments()) {
			if (!commentResponse.getCommentId().equals(commentResponse.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + commentResponse.getCommentId());
		}

		/*
		comment.getCommentId() = 186405922121302016
			comment.getCommentId() = 186405922154856450
		comment.getCommentId() = 186405922121302017
			comment.getCommentId() = 186405922163245078
		comment.getCommentId() = 186405922121302018
			comment.getCommentId() = 186405922154856451
		comment.getCommentId() = 186405922125496320
			comment.getCommentId() = 186405922163245082
		comment.getCommentId() = 186405922125496321
			comment.getCommentId() = 186405922163245081
		*/
	}

	@Test
	void readAllInfiniteScroll() {
		List<CommentResponse> response1 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("firstPage");
		for (CommentResponse comment : response1) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		Long lastParentCommentId = response1.getLast().getParentCommentId();
		Long lastCommentId = response1.getLast().getCommentId();

		List<CommentResponse> response2 = restClient.get()
			.uri(
				"/v1/comments/infinite-scroll?articleId=1&lastParentCommentId=%s&lastCommentId=%s&pageSize=5"
					.formatted(lastParentCommentId, lastCommentId))
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		for (CommentResponse comment : response2) {
			if (!comment.getParentCommentId().equals(comment.getCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}
	}


	@Data
	@AllArgsConstructor
	public static class CommentCreateRequest {

		private String content;
		private Long parentCommentId;
		private Long articleId;
		private Long writerId;
	}

}
