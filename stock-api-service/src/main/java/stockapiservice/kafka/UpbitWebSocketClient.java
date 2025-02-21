package server.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@ClientEndpoint
@Component
public class UpbitWebSocketClient {
    private static final String WS_URI = "wss://api.upbit.com/websocket/v1";
    private final WebSocketContainer container;
    private final ScheduledExecutorService executor;
    private final KafkaTemplate<String, UpbitTickerEventDto> kafkaTemplate;
    private final Executor taskExecutor;
    private Session session;
    private int retryCount = 0;


    public UpbitWebSocketClient(KafkaTemplate<String, UpbitTickerEventDto> kafkaTemplate, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.container = ContainerProvider.getWebSocketContainer();
        this.executor = Executors.newScheduledThreadPool(1);
        this.kafkaTemplate = kafkaTemplate;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void init() {
        connect();
    }

    private void connect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
            session = container.connectToServer(this, URI.create(WS_URI));
        } catch (Exception e) {
            System.err.println("Failed to connect: " + e.getMessage());
            retryConnection();
        }
    }

    private void retryConnection() {
        retryCount++;
        long delay = Math.min(30, (long) Math.pow(2, retryCount));
        System.out.println("Attempting to reconnect in " + delay + " seconds...");
        executor.schedule(this::connect, delay, TimeUnit.SECONDS);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to Upbit WebSocket server");
        retryCount = 0;
        try {
            String message = "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\",\"KRW-XRP\",\"KRW-SOL\",\"KRW-SEI\",\"KRW-ETH\",\"KRW-ZRO\",\"KRW-SUI\",\"KRW-USDT\",\"KRW-STX\",\"KRW-DOGE\"],\"isOnlyRealtime\":true}]";
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(ByteBuffer message) {
        try {
            String receivedMessage = new String(message.array(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열을 DTO 객체로 역직렬화
            UpbitTickerEventDto tickerEvent = objectMapper.readValue(receivedMessage, UpbitTickerEventDto.class);
            // switch 문을 사용하여 숫자에 따른 동작 구현
            sendKafkaToStockService(tickerEvent);

        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> sendKafkaToStockService(UpbitTickerEventDto tickerEvent) {
        kafkaTemplate.send(tickerEvent.getCode() + "-stock-event", tickerEvent);
        return CompletableFuture.completedFuture(null);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
        retryConnection();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason);
        retryConnection();
    }
}

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import jakarta.websocket.*;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.*;
//
//@EnableAsync
//@ClientEndpoint
//@Component
//public class UpbitWebSocketClient {
//    private static final String WS_URI = "wss://api.upbit.com/websocket/v1";
//    private final WebSocketContainer container;
//    private final ScheduledExecutorService reconnectExecutor;
//    //    private final ExecutorService messageProcessingExecutor;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final Executor taskExecutor;
//    private Session session;
//    private int retryCount = 0;
//
//    public UpbitWebSocketClient(@Qualifier("taskExecutor")
//                                Executor taskExecutor) {
//        this.taskExecutor = taskExecutor;
//        this.container = ContainerProvider.getWebSocketContainer();
//        this.reconnectExecutor = Executors.newScheduledThreadPool(1);
//    }
//
////    public UpbitWebSocketClient() {
////        this.container = ContainerProvider.getWebSocketContainer();
////        this.reconnectExecutor = Executors.newScheduledThreadPool(1);
//////        this.messageProcessingExecutor = Executors.newFixedThreadPool(2); // 10개의 스레드 풀
////    }
//
//    @PostConstruct
//    public void init() {
//        connect();
//    }
//
//    private void connect() {
//        try {
//            if (session != null && session.isOpen()) {
//                session.close();
//            }
//            session = container.connectToServer(this, URI.create(WS_URI));
//        } catch (Exception e) {
//            System.err.println("Failed to connect: " + e.getMessage());
//            retryConnection();
//        }
//    }
//
//    private void retryConnection() {
//        retryCount++;
//        long delay = Math.min(30, (long) Math.pow(2, retryCount));
//        System.out.println("Attempting to reconnect in " + delay + " seconds...");
//        reconnectExecutor.schedule(this::connect, delay, TimeUnit.SECONDS);
//    }
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("Connected to Upbit WebSocket server");
//        retryCount = 0;
//        sendSubscriptionMessages(session); // 구독 메시지 전송
//    }
//
//    // 여러 구독 메시지를 전송하는 메서드
//    private void sendSubscriptionMessages(Session session) {
//        try {
//            // 여러 구독 메시지
//            String[] messages = {
//                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC,KRW-XRP,KRW-SOL,KRW-SEI,KRW-ETH,KRW-ZRO\"],\"isOnlyRealtime\":true}]",
//////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-ETH\"],\"isOnlyRealtime\":true}]",
//////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-ZRO\"],\"isOnlyRealtime\":true}]",
////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-SUI,KRW-QKC\"],\"isOnlyRealtime\":true}]",
//////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-QKC\"],\"isOnlyRealtime\":true}]",
////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-STX\"],\"isOnlyRealtime\":true}]",
////                    "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[],\"isOnlyRealtime\":true}]"
//            };
//
//            for (String message : messages) {
//                session.getBasicRemote().sendText(message);
//                System.out.println("Sent subscription message: " + message);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //    @OnMessage
////    public void onMessage(ByteBuffer message) {
////        messageProcessingExecutor.submit(() -> {
////            try {
////                String receivedMessage = new String(message.array(), StandardCharsets.UTF_8);
////                System.out.println("Received message: " + receivedMessage);
////
////                JsonNode jsonNode = objectMapper.readTree(receivedMessage);
////                String type = jsonNode.get("type").asText();
////
////                // 받은 메시지 타입에 따른 처리
////                switch (type) {
////                    case "ticker":
////                        handleTickerMessage(jsonNode);
////                        break;
////                    default:
////                        System.out.println("Unknown message type: " + type);
////                }
////            } catch (Exception e) {
////                System.err.println("Failed to process message: " + e.getMessage());
////                e.printStackTrace();
////            }
////        });
////    }
//    @OnMessage
//    public void onMessage(ByteBuffer message) {
//        // 비동기 메서드 호출로 메시지 처리
//        processMessageAsync(message);
//    }
//
//
//    @Async("taskExecutor")
//    public CompletableFuture<Void> processMessageAsync(ByteBuffer message) {
//        try {
//            String receivedMessage = new String(message.array(), StandardCharsets.UTF_8);
//            System.out.println("Received message: " + receivedMessage);
//
//            JsonNode jsonNode = objectMapper.readTree(receivedMessage);
//            String type = jsonNode.get("type").asText();
//
//            // 받은 메시지 타입에 따른 처리
//            switch (type) {
//                case "ticker":
//                    handleTickerMessage(jsonNode);
//                    break;
//                default:
//                    System.out.println("Unknown message type: " + type);
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to process message: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return CompletableFuture.completedFuture(null);
//    }
//
//    private void handleTickerMessage(JsonNode jsonNode) {
//        try {
//            UpbitTickerEventDto tickerEvent = objectMapper.treeToValue(jsonNode, UpbitTickerEventDto.class);
//            System.out.println("Processed Ticker Message: " + tickerEvent.getAccTradePrice24h());
//        } catch (Exception e) {
//            System.err.println("Failed to process ticker message: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        System.out.println("Error: " + throwable.getMessage());
//        retryConnection();
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason reason) {
//        System.out.println("WebSocket closed: " + reason);
//        retryConnection();
//    }
//}