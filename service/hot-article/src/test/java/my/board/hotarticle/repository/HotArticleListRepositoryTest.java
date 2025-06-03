package my.board.hotarticle.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HotArticleListRepositoryTest {

	@Autowired
	HotArticleListRepository hotArticleListRepository;

	@Test
	void addTest() throws InterruptedException {

		// given
		LocalDateTime time = LocalDateTime.of(2025, 6, 3, 0, 0);
		long limit = 3;

		// when
		hotArticleListRepository.add(1L, time, 2L, limit, Duration.ofSeconds(3));
		hotArticleListRepository.add(2L, time, 1L, limit, Duration.ofSeconds(3));
		hotArticleListRepository.add(3L, time, 3L, limit, Duration.ofSeconds(3));
		hotArticleListRepository.add(4L, time, 5L, limit, Duration.ofSeconds(3));
		hotArticleListRepository.add(5L, time, 4L, limit, Duration.ofSeconds(3));

		// then
		List<Long> articleIds = hotArticleListRepository.readAll("20250603");

		assertThat(articleIds).hasSize(3); // limit 3개니까 3개만 가져옴 .
		assertThat(articleIds.get(0)).isEqualTo(4);
		assertThat(articleIds.get(1)).isEqualTo(5);
		assertThat(articleIds.get(2)).isEqualTo(3);

		TimeUnit.SECONDS.sleep(5); // 5초 기다렸다가 삭제 되는지 확인.

		assertThat(hotArticleListRepository.readAll("20250603")).isEmpty();
	}
}
