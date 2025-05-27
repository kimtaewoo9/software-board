package my.board.comment.repository;

import my.board.comment.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // covering index 를 활용하기 위해서 sub query를 넣어줌 .
    @Query(
        value = "select count(*) from (" +
            "   select comment_id from comment " +
            "   where article_id = :articleId and parent_comment_id = :parentCommentId " +
            "   limit :limit" +
            ") t",
        nativeQuery = true
    )
    Long countBy(
        @Param("articleId") Long articleId,
        @Param("parentCommentId") Long parentCommentId,
        @Param("limit") Long limit
    );
}
