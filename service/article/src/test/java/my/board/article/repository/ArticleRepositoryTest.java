package my.board.article.repository;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import my.board.article.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ArticleRepositoryTest {

	@Autowired
	ArticleRepository articleRepository;

	@Test
	void findAllTest() {
		List<Article> articles = articleRepository.findAll(1L, 1499970L, 30L);
		log.info("articles.size = {}", articles.size());
		for (Article article : articles) {
			log.info("article = {}", article);
		}
	}

	@Test
	void countTest() {
		// limit에는 몇개의 게시을 확인할지 넣어주면 됨 .. 게시글 전체 count를 할 필요가 없다 !.
		Long count = articleRepository.count(1L, 10000L);
		log.info("count = {}", count);
	}

	@Test
	void findInfiniteScrollTest() {
		List<Article> articles = articleRepository.findAllInfiniteScrollFirstPage(1L, 30L);
		for (Article article : articles) {
			log.info("article_id = {}", article.getArticleId());
		}

		Long lastArticleId = articles.getLast().getArticleId();
		List<Article> articles2 = articleRepository
			.findAllInfiniteScrollNextPage(1L, 30L, lastArticleId);
		for (Article article : articles2) {
			log.info("article_id = {}", article.getArticleId());
		}
	}
}
