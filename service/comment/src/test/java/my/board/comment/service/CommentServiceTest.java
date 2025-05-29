package my.board.comment.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import my.board.comment.entity.Comment;
import my.board.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;

    // 1. 하위 댓글이 있는 댓글 삭제.
    // 2. 상위 댓글이 삭제 되지 않은 댓글 삭제
    // 3. 상위 댓글이 삭제된 댓글 삭제.
    // 4. 상위 댓글, 하위 댓글 전부 없는 댓글 삭제 .. 그냥 혼자인 댓글

    @Test
    @DisplayName("1. 삭제할 댓글이 자식이 있으면, 삭제 표시만 한다.")
    void deleteShouldMarkDeletedIfHasChildren() {
        // given
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);

        given(commentRepository.findById(commentId))
            .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
            .willReturn(2L); // 삭제할 댓글이 자식이 있는 경우 .. countBy 에서 2L을 반환하는 경우 .

        // when
        commentService.delete(commentId);

        // then
        verify(comment).delete(); // mock 객체인 entity 에서 delete 가 호출 되었는지 확인 ..
    }

    @Test
    @DisplayName("1. 하위 댓글을 가지고 있으면 .. 삭제가 되면 안된다.")
    void deleteShouldMarkDeletedOnly() {
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId); // parent 가 없는 entity

        given(commentRepository.findById(commentId))
            .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
            .willReturn(2L); // 자식이 있다고 반환해야함 .

        // when
        commentService.delete(commentId);

        // then
        verify(commentRepository, never()).delete(comment);
    }

    @Test
    @DisplayName("2. 하위 댓글이 삭제 됐을때, 상위 댓글이 삭제되지 않은 상태라면 하위 댓글만 삭제한다.")
    void deleteShouldDeleteChildOnlyIfNotDeletedParent() {
        // given
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = mock(Comment.class);
        given(parentComment.getDelete()).willReturn(false); // 상위 댓글은 삭제 안된 상태.

        given(commentRepository.findById(commentId))
            .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
            .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
            .willReturn(Optional.of(parentComment));

        // when
        commentService.delete(commentId);

        // then
        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parentComment); // parent는 삭제 되면 안됨./
    }

    @Test
    @DisplayName("3. 하위 댓글을 삭제 했을때, 상위 댓글도 삭제된 상태라면 상위 댓글을 완전 삭제 처리.")
    void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        // entity 의 mock 객체 만들고 .
        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        // parent entity 의 mock 객체 만들고 .
        Comment parentComment = createComment(articleId, parentCommentId);
        given(parentComment.isRoot()).willReturn(true);
        given(parentComment.getDelete()).willReturn(true); // parent entity 는 삭제된 상태

        given(commentRepository.findById(commentId))
            .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
            .willReturn(1L); // entity 는 자식이 없음 . (depth=2니까 당연한거 ..)

        given(commentRepository.findById(parentCommentId))
            .willReturn(Optional.of(parentComment));

        given(commentRepository.countBy(articleId, parentCommentId, 2L))
            .willReturn(1L); // 다른 자식이 없어야함 .. 1L

        // when
        commentService.delete(commentId);

        // then .. entity 와 parent 가 완전 삭제 처리 되어야함 .
        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parentComment);
    }

    @Test
    @DisplayName("4. 부모도 자식도 아닌 댓글 그냥은 삭제 ..")
    void deleteNoParentNoChildrenComment() {
        // given
        Long articleId = 1L;
        Long commentId = 2L; // 1L 해도 상관 없긴 함 .

        Comment comment = createComment(1L, 2L);
        given(comment.isRoot()).willReturn(true);
        given(comment.getDelete()).willReturn(false); // false가 기본 .

        given(commentRepository.findById(commentId))
            .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
            .willReturn(1L); // 자식이 없음 .

        // when
        commentService.delete(commentId);

        // then
        verify(commentRepository).delete(comment); // 그냥 바로 삭제 .
    }

    private Comment createComment(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = createComment(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }
}
