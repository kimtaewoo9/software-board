package my.board.view.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import my.board.view.entity.ArticleViewCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ArticleViewCountBackUpRepositoryTest {

	@Autowired
	ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Test
	@Transactional
	void updateViewCountTest() {

		Long articleId = 1L;

		articleViewCountBackUpRepository.save(
			ArticleViewCount.init(articleId, 0L)
		);

		entityManager.flush();
		entityManager.clear();

		int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
		int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
		int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

		assertThat(result1).isEqualTo(1);
		assertThat(result2).isEqualTo(1);
		assertThat(result3).isEqualTo(0); // 조회수는 더 작은 수로 update 할 수 없음 .

		ArticleViewCount articleViewCount = articleViewCountBackUpRepository
			.findById(articleId).get();

		assertThat(articleViewCount.getViewCount()).isEqualTo(300L);
	}
}
