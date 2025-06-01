package my.board.article.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import my.board.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<BoardArticleCount> findLockedByBoardId(Long boardId);

	@Query(
		value = "update board_article_count set article_count = article_count + 1 where board_id = :boardId",
		nativeQuery = true
	)
	@Modifying
		// 조회가 아니라 데이터를 바꾸는 쿼리임을 알려줘야함 .
	int increase(@Param("boardId") Long boardId);

	@Query(
		value = "update board_article_count set article_count = article_count - 1 where board_id = :boardId",
		nativeQuery = true
	)
	@Modifying
	int decrease(@Param("boardId") Long boardId);
}
