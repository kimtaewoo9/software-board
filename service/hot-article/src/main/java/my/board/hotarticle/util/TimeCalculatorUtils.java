package my.board.hotarticle.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculatorUtils {

	// 자정까지 얼마나 남았는지 계산하는 메서드. 이걸로 ttl 을 계산해서 해당 날짜가 지나면 없어지도록 만듦.
	public static Duration calculateDurationToMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
		return Duration.between(now, midnight);
	}
}
