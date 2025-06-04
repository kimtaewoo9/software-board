package my.board.article.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.board.article.entity.Article;
import my.board.article.entity.BoardArticleCount;
import my.board.article.repository.ArticleRepository;
import my.board.article.repository.BoardArticleCountRepository;
import my.board.article.service.request.ArticleCreateRequest;
import my.board.article.service.request.ArticleUpdateRequest;
import my.board.article.service.response.ArticlePageResponse;
import my.board.article.service.response.ArticleResponse;
import my.board.common.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Transactional
	public ArticleResponse create(ArticleCreateRequest articleCreateRequest) {
		Article newArticle = Article.create(
			snowflake.nextId(),
			articleCreateRequest.getTitle(),
			articleCreateRequest.getContent(),
			articleCreateRequest.getBoardId(),
			articleCreateRequest.getWriterId()
		);
		Article savedArticle = articleRepository.save(newArticle);

		Long boardId = articleCreateRequest.getBoardId();
		int result = boardArticleCountRepository.increase(boardId);
		if (result == 0) {
			boardArticleCountRepository.save(
				BoardArticleCount.init(boardId, 1L)
			);
		}

		return ArticleResponse.from(savedArticle);
	}

	@Transactional
	public ArticleResponse update(Long articleId, ArticleUpdateRequest articleUpdateRequest) {
		Article article = articleRepository.findById(articleId).orElseThrow(
			() -> new EntityNotFoundException("article not found")
		);

		String title = articleUpdateRequest.getTitle();
		String content = articleUpdateRequest.getContent();
		article.update(title, content);

		return ArticleResponse.from(article);
	}

	public ArticleResponse read(Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow(
			() -> new EntityNotFoundException("article not found")
		);

		return ArticleResponse.from(article);
	}

	@Transactional
	public void delete(Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow(
			() -> new EntityNotFoundException("article not found")
		);
		articleRepository.delete(article);

		// 좋아요는 .. 좋아요 객체를 삭제해야함 .
		// 근데 조회수는 그냥 조회수 삭제해주면 됨 .
		boardArticleCountRepository.decrease(article.getBoardId());
	}

	public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
		List<ArticleResponse> articleResponse = articleRepository.findAll(boardId,
				(page - 1) * pageSize,
				pageSize)
			.stream()
			.map(ArticleResponse::from)
			.toList();

		Long articleCount = articleRepository.count(
			boardId,
			PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
		);

		return ArticlePageResponse.of(articleResponse, articleCount);
	}

	public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize,
		Long lastArticleId) {
		List<Article> articles = lastArticleId == null ?
			articleRepository.findAllInfiniteScrollFirstPage(boardId, pageSize) :
			articleRepository.findAllInfiniteScrollNextPage(boardId, pageSize, lastArticleId);

		return articles.stream()
			.map(ArticleResponse::from)
			.toList();
	}

	// 조회수 가져오기 .
	public Long count(Long boardId) {
		return boardArticleCountRepository.findById(boardId)
			.map(BoardArticleCount::getArticleCount)
			.orElse(0L); // board article count 객체가 없으면 게시글 수 0
	}
}
