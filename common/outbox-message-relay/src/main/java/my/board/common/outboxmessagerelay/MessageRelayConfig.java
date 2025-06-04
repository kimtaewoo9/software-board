package my.board.common.outboxmessagerelay;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync // @Async 붙은 메서드가 실행되면 메인 스레드가 아닌 별도의 스레드에서 해당 메서드가 실행됨.
@Configuration
@ComponentScan("my.board.common.outboxmessagerelay")
@EnableScheduling
public class MessageRelayConfig {
	// kafka template 를 이용해서 kafka 로 메시지를 보내야함 .

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public KafkaTemplate<String, String> messageRelayKafkaTemplate() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

	// 카프카에게 비동기 전송을 하기 위한 executor
	@Bean
	public Executor messageRelayPublishEventExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("mr-pub-event-");
		return executor;
	}

	// 아직 전송 되지 않은 이벤트를 주기적으로 확인하기 위한 executor
	// 메인 스레드 블로킹 없이 백그라운드에서 지속적으로 DB를 모니터링 할 수 있게됨 .
	@Bean
	public Executor messageRelayPublishPendingEventExecutor() {
		return Executors.newSingleThreadExecutor();
	}
}
