package my.board.common.outboxmessagerelay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRelayConstants {

	// 샤딩이 되어 있는 상황으로 가정함 . 애플리케이션마다 적절하게 샤드가 분산 되어 이벤트 전송을 수행하는 것을 볼 수 있도록 만들어보자.
	public static final int SHARD_COUNT = 4;
}
