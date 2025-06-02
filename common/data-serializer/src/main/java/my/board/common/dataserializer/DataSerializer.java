package my.board.common.dataserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSerializer {

	private static final ObjectMapper objectMapper = initialize();

	// 카프카는 .. 오직 byte array 만 저장할 수 있기 때문에 직렬화 해서 저장함 .
	private static ObjectMapper initialize() {
		return new ObjectMapper()
			.registerModule(new JavaTimeModule()) // 자바 8의 날짜/시간 타입을 지원하기 위한 설정 .
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 역직렬화 시 필드가 없어도 에러가 나지 않도록 설정해줌 .
	}

	// String 을 자바 객체로 변환 (역직렬화)
	public static <T> T deserialize(String data, Class<T> clazz) {
		try {
			return objectMapper.readValue(data, clazz);
		} catch (JsonProcessingException e) {
			log.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, e);
			return null;
		}
	}

	// object 를 자바 객체로 역직렬화 .
	public static <T> T deserialize(Object data, Class<T> clazz) {
		return objectMapper.convertValue(data, clazz);
	}

	// 자바 객체를 string 으로 변환 ..
	public static String serialize(Object object) {
		try {
			return objectMapper.writeValueAsString(object); // jackson 라이브러리의 objectMapper를 사용하여 직렬화
		} catch (JsonProcessingException e) {
			log.error("[DataSerializer.serialize] object={}", object, e);
			return null;
		}
	}
}
