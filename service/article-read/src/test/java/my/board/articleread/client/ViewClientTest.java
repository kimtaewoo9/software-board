package my.board.articleread.client;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ViewClientTest {

	@Autowired
	ViewClient viewClient;

	@Test
	void readCacheableTest() throws InterruptedException {
		viewClient.count(1L); // 로그 출력
		viewClient.count(1L); // 로그 미출력
		viewClient.count(1L); // 로그 미출력
		viewClient.count(1L); // 로그 미출력

		TimeUnit.SECONDS.sleep(3);

		Long count = viewClient.count(1L); // 로그 출력
	}
}
