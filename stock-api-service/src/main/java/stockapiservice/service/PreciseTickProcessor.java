//package server.service;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//import server.kafka.UpbitTickerEventDto;
//
//import java.net.URI;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PreciseTickProcessor {
//
//    private static final String UPBIT_API_URL = "https://api.upbit.com/v1/ticker";
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
//    private final RestTemplate restTemplate;
//    private final KafkaTemplate<String, UpbitTickerEventDto> kafkaTemplate;
//    private long nextExecutionTime;
//
//    // 시작 조건
//    @PostConstruct
//    public void init() {
//        while (true) {
//            if (System.currentTimeMillis() % 1000 == 0) {  // 밀리초가 0인지 확인
//                scheduleNextExecution();
//                nextExecutionTime = getNextTickTime(System.currentTimeMillis());
//                // nextExecutionTime == n.000초
//                break;
//            }
//        }
//    }
//
//    private void scheduleNextExecution() {
//        if (nextExecutionTime - System.currentTimeMillis() < -1000) {
//            // 틱을 없애기 위해 차이에서 1초 단위를 제거
//            long missedTicks = (-(nextExecutionTime - System.currentTimeMillis()) / 1000) * 1000;
//            nextExecutionTime += missedTicks;
//            scheduler.schedule(this::executeTask, 0, TimeUnit.MILLISECONDS);
//        } else {
//            // 다음 n+1.000초 - 이전 메소드 실행 시간 = 기다렸다 실행 되는 시간
//            scheduler.schedule(this::executeTask, nextExecutionTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
//        }
////        scheduler.schedule(this::executeTask, nextExecutionTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
//    }
//
//    private void executeTask() {
//
//
//        // 실제 작업 수행
//        processTickLogic();
//
//        // 다음 실행 시각 계산(이전 실행 시각+1초)
//        nextExecutionTime = getNextTickTime(nextExecutionTime);
//
//        // 다음 실행 예약
//        scheduleNextExecution();
//    }
//
//    private long getNextTickTime(long currentTime) {
//        return ((currentTime / 1000) + 1) * 1000;  // 다음 정각 초
//    }
//
//
//    private void processTickLogic() {
//        getTicker("KRW-BTC");
//
////        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> getTicker("KRW-BTC"), executorService);
////        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> getTicker("KRW-XRP"), executorService);
////        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> getTicker("KRW-SOL"), executorService);
////        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> getTicker("KRW-SEI"), executorService);
////        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> getTicker("KRW-ETH"), executorService);
////        CompletableFuture<Void> future6 = CompletableFuture.runAsync(() -> getTicker("KRW-ZRO"), executorService);
////        CompletableFuture<Void> future7 = CompletableFuture.runAsync(() -> getTicker("KRW-SUI"), executorService);
////        CompletableFuture<Void> future8 = CompletableFuture.runAsync(() -> getTicker("KRW-QKC"), executorService);
////        CompletableFuture<Void> future9 = CompletableFuture.runAsync(() -> getTicker("KRW-STX"), executorService);
////        CompletableFuture<Void> future10 = CompletableFuture.runAsync(() -> getTicker("KRW-DOGE"), executorService);
////        System.out.println("Processing tick at: " + Instant.ofEpochMilli(System.currentTimeMillis()));
////
////        // 모든 작업이 완료될 때까지 대기
////        CompletableFuture.allOf(future1, future2, future3,future4,future5,future6,future7,future8,future9, future10).join();
//    }
//
//
//    public void getTicker(String markets) {
//        URI uri = UriComponentsBuilder.fromHttpUrl(UPBIT_API_URL)
//                .queryParam("markets", markets)
//                .build()
//                .encode()
//                .toUri();
//        for (int i = 0; i < 10; i++) {
//            try {
//                // 카프카로 전달
//                UpbitTickerEventDto[] response = restTemplate.getForObject(uri, UpbitTickerEventDto[].class);
//                System.out.println("Processing tick at: " + response[0].getTimestamp());
//
//                kafkaTemplate.send("stock-event", response[0]);
//                break;
//            } catch (Exception er) {
//
//            }
//        }
//    }
//
//
//    @PreDestroy
//    public void shutdown() {
//        // 장애 시 크롤링하는 api 호출
//        scheduler.shutdown();
//    }
//}