package my.board.hotarticle.service;

import lombok.RequiredArgsConstructor;
import my.board.hotarticle.repository.ArticleCommentCountRepository;
import my.board.hotarticle.repository.ArticleLikeCountRepository;
import my.board.hotarticle.repository.ArticleViewCountRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotArticleScoreCalculator {

	// 여기서 이제 score 를 계산함 .
	// TODO 그냥 좋아요 수만으로 . hot article 을 선정
	private final ArticleLikeCountRepository articleLikeCountRepository;
	private final ArticleViewCountRepository articleViewCountRepository;
	private final ArticleCommentCountRepository articleCommentCountRepository;

	private static final long ARTICLE_LIKE_COUNT_WEIGHT = 3;
	private static final long ARTICLE_VIEW_COUNT_WEIGHT = 1; // view count 의 가중치가 2는 너무 높음.
	private static final long ARTICLE_COMMENT_COUNT_WEIGHT = 1;

	public long calculate(Long articleId) {
		// redis 에서 데이터 읽어서 score 계산 후 반환 .
		Long likeCount = articleLikeCountRepository.read(articleId);
		Long viewCount = articleViewCountRepository.read(articleId);
		Long commentCount = articleCommentCountRepository.read(articleId);

		return likeCount * ARTICLE_LIKE_COUNT_WEIGHT
			+ viewCount * ARTICLE_VIEW_COUNT_WEIGHT
			+ commentCount * ARTICLE_COMMENT_COUNT_WEIGHT;
	}
}
