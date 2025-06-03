package my.board.hotarticle.util;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class TimeCalculatorUtilsTest {

	@Test
	void timeCalculatorTest() {
		Duration duration = TimeCalculatorUtils.calculateDurationToMidnight();
		System.out.println(
			"자정까지 " + duration.getSeconds() / 60 / 60 + "시간 " + duration.getSeconds() / 60 % 60
				+ "분 남았습니다.");
	}
}
