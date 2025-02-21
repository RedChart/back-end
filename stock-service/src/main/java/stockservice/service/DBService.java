package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.domain.Stock_BTC;
import server.kafka.UpbitTickerEventDto;
import server.repository.UpbitTickerEventRepository;

@Service
@RequiredArgsConstructor
public class DBService {
    private final UpbitTickerEventRepository eventRepository;

    public void deleteLatestEvent(UpbitTickerEventDto upbitTickerEventDto) {

        eventRepository.save(upbitTickerEventDto.toEntity());
        // 가장 작은 timestamp를 가진 엔티티를 찾음
        Stock_BTC latestEvent = eventRepository.findTopByOrderByTimestampAsc();

        if (eventRepository.count() > 10) {
            // 해당 엔티티를 삭제
            eventRepository.delete(latestEvent);
        } else {
        }
    }
}
