package my.board.comment.service;

import static java.util.function.Predicate.not;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.comment.entity.ArticleCommentCount;
import my.board.comment.entity.Comment;
import my.board.comment.repository.ArticleCommentCountRepository;
import my.board.comment.repository.CommentRepository;
import my.board.comment.service.request.CommentCreateRequest;
import my.board.comment.service.response.CommentPageResponse;
import my.board.comment.service.response.CommentResponse;
import my.board.common.event.EventType;
import my.board.common.event.payload.CommentCreatedEventPayload;
import my.board.common.event.payload.CommentDeletedEventPayload;
import my.board.common.outboxmessagerelay.OutboxEventPublisher;
import my.board.common.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final Snowflake snowflake = new Snowflake();
	private final ArticleCommentCountRepository articleCommentCountRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	@Transactional
	public CommentResponse create(CommentCreateRequest commentCreateRequest) {
		log.info("✅ commentCreateRequest: {}", commentCreateRequest);
		Comment parentComment = findParent(commentCreateRequest.getParentCommentId());
		Comment newComment = Comment.create(
			snowflake.nextId(),
			commentCreateRequest.getContent(),
			parentComment == null ? null : parentComment.getCommentId(),
			commentCreateRequest.getArticleId(),
			commentCreateRequest.getWriterId()
		);

		Comment savedComment = commentRepository.save(newComment);

		// save 후 article comment count 를 증가 시켜야함 sql 문으로 바로 업데이트
		int result = articleCommentCountRepository.increase(commentCreateRequest.getArticleId());
		if (result == 0) {
			// 없으면 만들어서 저장하기
			articleCommentCountRepository.save(
				ArticleCommentCount.init(commentCreateRequest.getArticleId(), 1L)
			);
		}

		CommentCreatedEventPayload commentCreatedEventPayload
			= CommentCreatedEventPayload.builder()
			.commentId(savedComment.getCommentId())
			.content(savedComment.getContent())
			.parentCommentId(savedComment.getParentCommentId())
			.articleId(savedComment.getArticleId())
			.writerId(savedComment.getWriterId())
			.deleted(savedComment.getDeleted())
			.createdAt(savedComment.getCreatedAt())
			.articleCommentCount(count(savedComment.getArticleId()))
			.build();

		outboxEventPublisher.publish(
			EventType.COMMENT_CREATED,
			commentCreatedEventPayload,
			savedComment.getArticleId()
		);

		return CommentResponse.from(savedComment);
	}

	private Comment findParent(Long parentCommentId) {
		if (parentCommentId == null) {
			return null;
		}
		// 상위 댓글을 찾을껀데 . 상위 댓글이 삭제된 상태가 아니여야함 + 최상위 댓글이여야함(최대 2depth) .
		return commentRepository.findById(parentCommentId)
			.filter(not(Comment::getDeleted))
			.filter(Comment::isRoot)
			.orElseThrow();
	}


	public CommentResponse read(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException("entity not found")
		);

		return CommentResponse.from(comment);
	}

	// TODO .. 댓글 수정 기능 추가하기 .

	@Transactional
	public void delete(Long commentId) {
		commentRepository.findById(commentId)
			.filter(not(Comment::getDeleted))
			.ifPresent(comment -> {
				if (hasChildren(comment)) {
					comment.delete(); // 자식 댓글이 있으면 delete 표시만 해줌
				} else {
					delete(comment); // 자식 댓글 없으면 그냥 삭제 처리 .
				}
				// 논리적 삭제, 물리적 삭제 둘다 댓글 수 자체는 줄여줘야함 .
				articleCommentCountRepository.decrease(comment.getArticleId());

				CommentDeletedEventPayload commentDeletedEventPayload =
					CommentDeletedEventPayload.builder()
						.commentId(comment.getCommentId())
						.content(comment.getContent())
						.articleId(comment.getArticleId())
						.writerId(comment.getWriterId())
						.deleted(comment.getDeleted())
						.createdAt(comment.getCreatedAt())
						.articleCommentCount(count(comment.getArticleId()))
						.build();

				outboxEventPublisher.publish(
					EventType.COMMENT_DELETED,
					commentDeletedEventPayload,
					comment.getArticleId()
				);
			});
	}

	@Transactional(readOnly = true)
	public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
		List<CommentResponse> commentResponses = commentRepository.findAll(articleId,
				(page - 1) * pageSize, // offset -> 얼마나 건너 뛰어야 하는가.
				pageSize)
			.stream().map(CommentResponse::from)
			.toList();

		// limit -> 이동 가능한 페이지 번호 활성화에 필요함 . movablePage -> 한 화면에서 이동 가능한 페이지 수
		Long limit = PageLimitCalculator.calculatePageLimit(page, pageSize, 10L);

		return CommentPageResponse.of(commentResponses, limit);
	}

	@Transactional(readOnly = true)
	public List<CommentResponse> readAll(Long articleId, Long lastParentCommentId,
		Long lastCommentId, Long limit) {

		List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
			commentRepository.findAllInfiniteScrollFirstPage(articleId, limit) :
			commentRepository.findAllInfiniteScrollNextPage(articleId, lastParentCommentId
				, lastCommentId, limit);

		return comments.stream()
			.map(CommentResponse::from)
			.toList();
	}

	// 진짜 삭제 하는 코드 이떄는 decrease 해야함 .
	private void delete(Comment comment) {
		commentRepository.delete(comment);
		// 만약에 '삭제된 상위 댓글'이 있다면 그것도 삭제해야함 . -> 즉 자식 먼저 삭제하고 상위 댓글을 확인함 .
		// 상위 댓글 찾고, 삭제가 되어 있는지 찾고, 삭제 되어 있으면 자식이 있는지 확인하고 .. ifPresent 완전 삭제
		if (!comment.isRoot()) {
			commentRepository.findById(comment.getParentCommentId()) // 상위 댓글 확인.
				.filter(Comment::getDeleted) // 상위 댓글이 삭제가 되어있는지 확인 .
				.filter(not(this::hasChildren)) // 다른 자식이 있는지 확인 . 자식이 없어야 삭제 가능 .
				.ifPresent(this::delete); // 댓글이 삭제 되어 있고, 자식이 없다면 delete (완전 삭제)
		}
	}

	// 일단 상위 댓글이 삭제되었다는 가정 하에 .. 이 메서드가 실행되고 자신(comment) 말고 다른 자식이 있으면 삭제 x
	private boolean hasChildren(Comment comment) {
		return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
	}

	@Transactional(readOnly = true)
	public Long count(Long articleId) {
		return articleCommentCountRepository.findById(articleId)
			.map(ArticleCommentCount::getCommentCount)
			.orElse(0L);
	}
}
