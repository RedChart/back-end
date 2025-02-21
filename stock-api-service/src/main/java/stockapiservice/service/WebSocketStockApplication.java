//package server.service;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//
//@SpringBootApplication
//public class WebSocketStockApplication {
//
//    private final StockPriceWebSocketClient stockPriceWebSocketClient;
//
//    public WebSocketStockApplication(StockPriceWebSocketClient stockPriceWebSocketClient) {
//        this.stockPriceWebSocketClient = stockPriceWebSocketClient;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(WebSocketStockApplication.class, args);
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartup() {
//        String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB", "TSLA", "NVDA", "JPM", "JNJ", "V"};
//        stockPriceWebSocketClient.subscribeToStockPrices(symbols);
//    }
//}