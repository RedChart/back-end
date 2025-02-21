package server.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.repository.StockRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {
    private final StockRepository stockRepository;
    private final SimpMessagingTemplate messagingTemplate;

    //    //    StockCircularBuffer buffer = new StockCircularBuffer();
////    CircularBufferEntityIndex buffer = new CircularBufferEntityIndex(5);
//    private final StockCircularBuffer stockCircularBuffer;
//
//    @Operation(summary = "댓글 작성")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK")
//    })
//    @PostMapping("/add")
//    public ResponseEntity<?> addcomment(@RequestBody Long a) {
////        StockCircularBuffer buffer = new StockCircularBuffer();
//        Stock stock = new Stock(a);
//        stockCircularBuffer.addStock(stock);
////        buffer.addEntityId(a);
//        return ResponseEntity.status(HttpStatus.OK).body("댓글이 생성되었습니다.");
//    }
//    @Operation(summary = "댓글 작성")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK")
//    })
//    @PostMapping("/add")
//    public ResponseEntity<?> addcomment() {
////        StockCircularBuffer buffer = new StockCircularBuffer();
//        messagingTemplate.convertAndSend("/topic/events", "Event triggered: ");
////        buffer.addEntityId(a);
//        return ResponseEntity.status(HttpStatus.OK).body("댓글이 생성되었습니다.");
//    }
    @GetMapping("/details")
//    @MessageMapping("/start")
    public void handleStart() {
        String message = "Event triggered: ";
        messagingTemplate.convertAndSend("/sub/topic", message);
    }
//
}
