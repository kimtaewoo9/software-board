package my.board.hotarticle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.random.RandomGenerator;
import my.board.hotarticle.repository.ArticleCommentCountRepository;
import my.board.hotarticle.repository.ArticleLikeCountRepository;
import my.board.hotarticle.repository.ArticleViewCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotArticleScoreCalculatorTest {

	@InjectMocks
	HotArticleScoreCalculator hotArticleScoreCalculator;
	@Mock
	ArticleLikeCountRepository articleLikeCountRepository;
	@Mock
	ArticleViewCountRepository articleViewCountRepository;
	@Mock
	ArticleCommentCountRepository articleCommentCountRepository;

	@Test
	void calculateTest() {
		Long articleId = 1L;

		long likeCount = RandomGenerator.getDefault().nextLong(100);
		long viewCount = RandomGenerator.getDefault().nextLong(100);
		long commentCount = RandomGenerator.getDefault().nextLong(100);

		given(articleLikeCountRepository.read(articleId)).willReturn(likeCount);
		given(articleViewCountRepository.read(articleId)).willReturn(viewCount);
		given(articleCommentCountRepository.read(articleId)).willReturn(commentCount);

		// when hotArticleScoreCalculator 에 articleId 를 던져주면 score 가 나옴 .
		long score = hotArticleScoreCalculator.calculate(articleId);

		// then
		assertThat(score)
			.isEqualTo(3 * likeCount + viewCount + commentCount);
	}
}
