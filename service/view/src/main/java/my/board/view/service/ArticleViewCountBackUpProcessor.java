package my.board.view.service;

import lombok.RequiredArgsConstructor;
import my.board.view.entity.ArticleViewCount;
import my.board.view.repository.ArticleViewCountBackUpRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {

	private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

	@Transactional
	public void backUp(Long articleId, Long viewCount) {
		int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
		// article_id 가 없는 경우에는 레코드를 새로 생성해줘야함 .
		if (result == 0) {
			articleViewCountBackUpRepository.findById(articleId)
				.ifPresentOrElse(ignored -> {
					},
					() -> articleViewCountBackUpRepository.save(
						ArticleViewCount.init(articleId, viewCount))
				);
		}
	}
}
