package my.board.article.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.article.document.ArticleDocument;
import my.board.article.entity.Article;
import my.board.article.entity.BoardArticleCount;
import my.board.article.repository.ArticleDocumentRepository;
import my.board.article.repository.ArticleRepository;
import my.board.article.repository.BoardArticleCountRepository;
import my.board.article.service.request.ArticleCreateRequest;
import my.board.article.service.request.ArticleUpdateRequest;
import my.board.article.service.response.ArticlePageResponse;
import my.board.article.service.response.ArticleResponse;
import my.board.common.event.EventType;
import my.board.common.event.payload.ArticleCreatedEventPayload;
import my.board.common.event.payload.ArticleDeletedEventPayload;
import my.board.common.event.payload.ArticleUpdatedEventPayload;
import my.board.common.outboxmessagerelay.OutboxEventPublisher;
import my.board.common.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;
	private final ArticleDocumentRepository articleDocumentRepository;

	private final OutboxEventPublisher outboxEventPublisher;

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

		ArticleDocument articleDocument = ArticleDocument.create(
			savedArticle.getArticleId(),
			savedArticle.getTitle(),
			savedArticle.getContent(),
			savedArticle.getBoardId(),
			savedArticle.getWriterId(),
			savedArticle.getCreatedAt(),
			savedArticle.getUpdatedAt()
		);
		articleDocumentRepository.save(articleDocument);

		ArticleCreatedEventPayload articleCreateEventPayload =
			ArticleCreatedEventPayload.builder()
				.articleId(savedArticle.getArticleId())
				.title(savedArticle.getTitle())
				.content(savedArticle.getContent())
				.boardId(savedArticle.getBoardId())
				.writerId(savedArticle.getWriterId())
				.createdAt(savedArticle.getCreatedAt())
				.updatedAt(savedArticle.getUpdatedAt())
				.boardArticleCount(count(savedArticle.getBoardId()))
				.build();

		// @TransactionalEventListener 애노테이션이 있는 메서드들에게 이벤트를 전송
		// 이벤트 발생을 알린다 .
		outboxEventPublisher.publish(
			EventType.ARTICLE_CREATED,
			articleCreateEventPayload,
			savedArticle.getBoardId() // 단일 트랜잭션에서 .. 동일한 샤드로 처리 되어야하므로 .. 샤드키 전달
		);

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

		ArticleDocument articleDocument = ArticleDocument.create(
			article.getArticleId(),
			article.getTitle(),
			article.getContent(),
			article.getBoardId(),
			article.getWriterId(),
			article.getCreatedAt(),
			article.getUpdatedAt()
		);
		articleDocumentRepository.save(articleDocument);

		ArticleUpdatedEventPayload articleUpdatedEventPayload =
			ArticleUpdatedEventPayload.builder()
				.articleId(article.getArticleId())
				.title(article.getTitle())
				.content(article.getContent())
				.boardId(article.getBoardId())
				.writerId(article.getWriterId())
				.createdAt(article.getCreatedAt())
				.updatedAt(article.getUpdatedAt())
				.build();

		// 이벤트 데이터 생성 및 아웃 박스 테이블에 저장 .
		outboxEventPublisher.publish(
			EventType.ARTICLE_UPDATED,
			articleUpdatedEventPayload,
			article.getBoardId() // 단일 트랜잭션에서 동일한 샤드로 처리 되어야하므로 샤드키 전달
		);

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
		articleDocumentRepository.deleteById(articleId);

		// 좋아요는 .. 좋아요 객체를 삭제해야함 .
		// 근데 조회수는 그냥 조회수 삭제해주면 됨 .
		boardArticleCountRepository.decrease(article.getBoardId());

		ArticleDeletedEventPayload articleDeletedEventPayload
			= ArticleDeletedEventPayload.builder()
			.articleId(article.getArticleId())
			.title(article.getTitle())
			.content(article.getContent())
			.boardId(article.getBoardId())
			.writerId(article.getWriterId())
			.createdAt(article.getCreatedAt())
			.updatedAt(article.getUpdatedAt())
			.boardArticleCount(count(article.getBoardId()))
			.build();

		outboxEventPublisher.publish(
			EventType.ARTICLE_DELETED,
			articleDeletedEventPayload,
			article.getBoardId()
		);
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
