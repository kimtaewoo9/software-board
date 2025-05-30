package my.board.like.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.board.common.snowflake.Snowflake;
import my.board.like.entity.ArticleLike;
import my.board.like.repository.ArticleLikeRepository;
import my.board.like.service.response.ArticleLikeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

	private final Snowflake snowflake = new Snowflake();
	private final ArticleLikeRepository articleLikeRepository;

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
}
