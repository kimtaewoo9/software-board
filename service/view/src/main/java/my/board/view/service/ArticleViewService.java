package my.board.view.service;

import lombok.RequiredArgsConstructor;
import my.board.view.repository.ArticleViewCountBackUpRepository;
import my.board.view.repository.ArticleViewCountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

	private final ArticleViewCountRepository articleViewCountRepository;
	private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
	private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;

	private static final int BACKUP_BATCH_SIZE = 100;


	public Long increase(Long articleId, Long userId) {
		Long count = articleViewCountRepository.increase(articleId);
		if (count % BACKUP_BATCH_SIZE == 0) {
			articleViewCountBackUpProcessor.backUp(articleId, count);
		}

		return count;
	}

	public Long count(Long articleId) {
		return articleViewCountRepository.read(articleId);
	}
}
