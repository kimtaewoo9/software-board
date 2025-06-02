package my.board.view.repository;

import my.board.view.entity.ArticleViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewCountBackUpRepository extends JpaRepository<ArticleViewCount, Long> {

	@Query(
		value = "update article_view_count set view_count = :viewCount "
			+ "where article_id = :articleId and view_count < :viewCount", // view count가 작아 질 수는 없음.
		nativeQuery = true
	)
	@Modifying
	int updateViewCount(
		@Param("articleId") Long articleId,
		@Param("viewCount") Long viewCount
	);
}
