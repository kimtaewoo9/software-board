package my.board.like.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import my.board.like.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikeCountRepository extends JpaRepository<ArticleLikeCount, Long> {

	// select  ... for update 구문 이렇게하면 조회와 동시에 비관적 락이 잡힘 .
	// 좀 더 객체지향적으로 코딩이 가능 (엔티티를 찾아서 수정)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<ArticleLikeCount> findLockedByArticleId(Long articleId);

	@Query(
		value = "update article_like_count set like_count = like_count + 1 where article_id = :articleId",
		nativeQuery = true
	)
	@Modifying
		// update 쿼리임을 hibernate 에게 알려줌.
	int increase(@Param("articleId") Long articleId);

	@Query(
		value = "update article_like_count set like_count = like_count - 1 where article_id = :articleId",
		nativeQuery = true
	)
	@Modifying
	int decrease(@Param("articleId") Long articleId);
}
