package my.board.comment.service;

import static java.util.function.Predicate.not;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.board.comment.entity.Comment;
import my.board.comment.repository.CommentRepository;
import my.board.comment.service.request.CommentCreateRequest;
import my.board.comment.service.response.CommentResponse;
import my.board.common.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final Snowflake snowflake = new Snowflake();


    @Transactional
    public CommentResponse create(CommentCreateRequest commentCreateRequest) {
        Comment parentComment = findParent(commentCreateRequest.getParentCommentId());

        Comment newComment = Comment.create(
            snowflake.nextId(),
            commentCreateRequest.getContent(),
            parentComment == null ? null : parentComment.getCommentId(),
            commentCreateRequest.getArticleId(),
            commentCreateRequest.getWriterId()
        );

        Comment savedComment = commentRepository.save(newComment);

        return CommentResponse.from(savedComment);
    }

    private Comment findParent(Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }
        // 상위 댓글을 찾을껀데 . 상위 댓글이 삭제된 상태가 아니여야함 + 최상위 댓글이여야함(최대 2depth) .
        return commentRepository.findById(parentCommentId)
            .filter(not(Comment::getDelete))
            .filter(Comment::isRoot)
            .orElseThrow();
    }


    public CommentResponse read(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            () -> new EntityNotFoundException("entity not found")
        );

        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
            .filter(not(Comment::getDelete))
            .ifPresent(comment -> {
                if (hasChildren(comment)) {
                    comment.delete(); // 자식 댓글이 있으면 delete 표시만 해줌
                } else {
                    delete(comment); // 자식 댓글 없으면 그냥 삭제 처리 .
                }
            });
    }

    private void delete(Comment comment) {
        commentRepository.delete(comment);
        // 만약에 '삭제된 상위 댓글'이 있다면 그것도 삭제해야함 . -> 즉 자식 먼저 삭제하고 상위 댓글을 확인함 .
        // 상위 댓글 찾고, 삭제가 되어 있는지 찾고, 삭제 되어 있으면 자식이 있는지 확인하고 .. ifPresent 완전 삭제
        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId()) // 상위 댓글 확인.
                .filter(Comment::getDelete) // 상위 댓글이 삭제가 되어있는지 확인 .
                .filter(not(this::hasChildren)) // 다른 자식이 있는지 확인 . 자식이 없어야 삭제 가능 .
                .ifPresent(this::delete); // 댓글이 삭제 되어 있고, 자식이 없다면 delete (완전 삭제)
        }
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }
}
