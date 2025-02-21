package server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.kafka.UpbitTickerEventDto;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final SimpMessagingTemplate messagingTemplate;
    private final DBService dbService;
    private volatile int lastProcessedMinute = -1;

    /*
     * 1. 토픽이랑 리스너를 한개로 사용해서 병렬처리 해보고 성능 측정
     * 2. 지금처럼 토픽 10개 발행, 리스너 한개로 사용해서 병렬처리 해보고 성능 측정
     * 3. 토픽 10개 발행, 리스너 10개 만들어서 처리해보고 성능 측정
     *
     * (이준석) 측정 해야 하는것 : 첫 데이터 받고 DB 저장까지 걸리는 시간 / 10가지 이벤트 발행했을 때 각각의 리스너가 메시지를 처리하기 시작하는 시간?
     * 리스너 작동 시간 측정 -> DB 인서트 되는시간 측정
     * jmeter의 카프카 db 인서트 되는 시간?
     * (오주연) 측정 해야 하는것 : 여러 데이터를 보냈을 때 보낸 데이받은 데이터가 언제
     * */
    // 코인마다 리스너를 만들어야함
    @KafkaListener(topics = "KRW-BTC-stock-event", groupId = "stock-service-group")
    public void listenStockEvent(UpbitTickerEventDto upbitTickerEventDto) {

        // 웹소켓에 전달
        messagingTemplate.convertAndSend("/sub/topic/KRW-BTC/sec", upbitTickerEventDto);
        int currentMinute = Instant.ofEpochMilli(upbitTickerEventDto.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().getMinute();
        //timestamp의 분이 바뀌면 저장
        if (currentMinute != lastProcessedMinute) {
            messagingTemplate.convertAndSend("/sub/topic/KRW-BTC/min_1", upbitTickerEventDto);
            log.info(String.valueOf(upbitTickerEventDto.getTimestamp()));
            // 마지막으로 처리된 분 업데이트
            lastProcessedMinute = currentMinute; // 마지막으로 처리된 분 업데이트
            // db에 있는 데이터를 병렬적으로 보냄
            dbService.deleteLatestEvent(upbitTickerEventDto);
        }
    }
}
