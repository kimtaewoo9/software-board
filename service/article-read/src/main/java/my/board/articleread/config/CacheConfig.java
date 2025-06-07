package my.board.articleread.config;

import java.time.Duration;
import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching // 캐시 추상화 기능 .. @Cacheable 이게 있어야 애노테이션이 동작함
public class CacheConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.builder(connectionFactory)
			.withInitialCacheConfigurations(
				Map.of(
					"articleViewCount",
					RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(1))
				)
			)
			.build();
	}
}
