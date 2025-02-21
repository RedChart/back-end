package server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class ThreadConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // I/O 바운드라면 코어 수의 2배로 설정 가능
        executor.setMaxPoolSize(4); // 최대 스레드 수
        executor.setQueueCapacity(100000); // 대기열 크기
        executor.setThreadNamePrefix("TickerTask-"); // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}