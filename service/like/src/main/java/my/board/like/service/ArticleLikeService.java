package my.board.like.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.board.common.snowflake.Snowflake;
import my.board.like.entity.ArticleLike;
import my.board.like.entity.ArticleLikeCount;
import my.board.like.repository.ArticleLikeCountRepository;
import my.board.like.repository.ArticleLikeRepository;
import my.board.like.service.response.ArticleLikeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleLikeService {

	private final Snowflake snowflake = new Snowflake();
	private final ArticleLikeRepository articleLikeRepository;
	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Transactional(readOnly = true)
	public ArticleLikeResponse read(Long articleId, Long userId) {
		ArticleLike articleLike =
			articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElseThrow(
				() -> new EntityNotFoundException("article like not found")
			);

		return ArticleLikeResponse.from(articleLike);
	}

	@Transactional
	public ArticleLikeResponse like(Long articleId, Long userId) {
		ArticleLike articleLike = ArticleLike.create(
			snowflake.nextId(),
			articleId,
			userId
		);

		ArticleLike savedArticleLike = articleLikeRepository.save(articleLike);

		return ArticleLikeResponse.from(savedArticleLike);
	}

	@Transactional
	public void unlike(Long articleId, Long userId) {
		// 찾아보고 없으면 삭제 .
		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(articleLikeRepository::delete);
	}

	// update 구문 .
	@Transactional
	public ArticleLikeResponse likePessimisticLock1(Long articleId, Long userId) {
		log.info("✅Pessimistic Lock1");
		ArticleLike newArticleLike = ArticleLike.create(
			snowflake.nextId(),
			articleId,
			userId
		);
		ArticleLike savedArticleLike = articleLikeRepository.save(newArticleLike);

		int articleLikeCount = articleLikeCountRepository.increase(articleId);
		if (articleLikeCount == 0) {
			articleLikeCountRepository.save(
				// 최초 요청시에는 update 불가능 .(데이터가 없음) 그래서 init을 해주면 됨 .
				ArticleLikeCount.init(articleId, 1L)
			);
		}

		return ArticleLikeResponse.from(savedArticleLike);
	}

	@Transactional
	public void unlikePessimisticLock1(Long articleId, Long userId) {
		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(articleLike -> {
				articleLikeRepository.delete(articleLike);
				articleLikeCountRepository.decrease(articleId);
				// TODO articleLikeCount의 likeCount가 0이면 articleLikeCount 삭제 .
			});
	}

	@Transactional
	public ArticleLikeResponse likePessimisticLock2(Long articleId, Long userId) {
		log.info("✅Pessimistic Lock2");
		ArticleLike newArticleLike = ArticleLike.create(
			snowflake.nextId(),
			articleId,
			userId
		);
		ArticleLike savedArticleLike = articleLikeRepository.save(newArticleLike);

		// select .. for update 일단 articleId 로 찾음 .
		// articleLikeCountRepository 에서 찾아보고 없으면 생성함 .
		ArticleLikeCount articleLikeCount =
			articleLikeCountRepository.findLockedByArticleId(articleId)
				.orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
		articleLikeCount.increase();
		articleLikeCountRepository.save(articleLikeCount);

		return ArticleLikeResponse.from(savedArticleLike);
	}

	@Transactional
	public void unlikePessimisticLock2(Long articleId, Long userId) {

		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(articleLike -> {
				articleLikeRepository.delete(articleLike);
				ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(
					articleId).orElseThrow(
					() -> new EntityNotFoundException("article like count not found")
				);
				articleLikeCount.decrease(); // TODO articleLikeCount 가 0이 되는 경우 삭제 .. 이런거 넣어야할듯
			});
	}

	@Transactional
	public ArticleLikeResponse likeOptimisticLock(Long articleId, Long userId) {
		log.info("✅Optimistic Lock");
		ArticleLike newArticleLike = ArticleLike.create(
			snowflake.nextId(),
			articleId,
			userId
		);

		ArticleLike savedArticleLike = articleLikeRepository.save(newArticleLike);

		// 찾았는데 없으면 init 해줌.
		ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
			.orElseGet(
				() -> ArticleLikeCount.init(articleId, 0L)
			);
		articleLikeCount.increase();
		articleLikeCountRepository.save(articleLikeCount);

		return ArticleLikeResponse.from(savedArticleLike);
	}

	@Transactional
	public void unlikeOptimisticLock(Long articleId, Long userId) {
		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(
				articleLike -> {
					articleLikeRepository.delete(articleLike);
					// lock 을 사용해서 가져오기 vs 그냥 가져오기 ..
					ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(
						articleId).orElseThrow(
						() -> new EntityNotFoundException("article like count not found")
					);
					articleLikeCount.decrease(); // TODO articleLikeCount가 0일때 처리.
				}
			);
	}

	public Long count(Long articleId) {
		log.info("✅count 를 실행합니다");
		return articleLikeCountRepository.findById(articleId)
			.map(ArticleLikeCount::getLikeCount)
			.orElse(0L);
	}
}
