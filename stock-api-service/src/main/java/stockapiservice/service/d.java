//package server.service;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Component
//public class StockPriceWebSocketClient {
//
//    private final String wsUri = "ws://your-websocket-server-url";
//    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//    public void subscribeToStockPrices(String[] symbols) {
//        CountDownLatch latch = new CountDownLatch(symbols.length);
//
//        for (String symbol : symbols) {
//            executorService.submit(() -> {
//                try {
//                    connectAndSubscribe(symbol);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        try {
//            latch.await(); // 모든 연결이 완료될 때까지 대기
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    private void connectAndSubscribe(String symbol) throws Exception {
//        StandardWebSocketClient client = new StandardWebSocketClient();
//        WebSocketHandler handler = new StockPriceWebSocketHandler(symbol);
//
//        client.doHandshake(handler, wsUri).get();
//    }
//
//    private class StockPriceWebSocketHandler implements WebSocketHandler {
//        private final String symbol;
//
//        public StockPriceWebSocketHandler(String symbol) {
//            this.symbol = symbol;
//        }
//
//        @Override
//        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//            String subscribeMessage = String.format("{\"type\":\"subscribe\",\"symbol\":\"%s\"}", symbol);
//            session.sendMessage(new TextMessage(subscribeMessage));
//        }
//
//        @Override
//        public void handleTextMessage(WebSocketSession session, TextMessage message) {
//            System.out.println("Received for " + symbol + ": " + message.getPayload());
//        }
//
//        @Override
//        public void handleTransportError(WebSocketSession session, Throwable exception) {
//            System.err.println("Error occurred for " + symbol + ": " + exception.getMessage());
//        }
//
//        @Override
//        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//            System.out.println("Connection closed for " + symbol + " with status: " + status);
//        }
//
//        @Override
//        public boolean supportsPartialMessages() {
//            return false;
//        }
//    }
//}