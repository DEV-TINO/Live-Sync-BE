package com.devtino.livesync.global.config;

import com.devtino.livesync.global.sse.NotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Redis에 저장되는 데이터를 문자열로 직렬화하도록 설정
        // 이 설정이 없으면 기본적으로 JDK 직렬화가 사용되어 JSON이 깨지는 문제가 발생
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    /*
     * Redis 메시지 리스너 등록
     * notification 채널 구독
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(
            LettuceConnectionFactory connectionFactory,
            NotificationSubscriber subscriber
    ) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                subscriber,
                new PatternTopic("notification")
        );

        return container;
    }
}