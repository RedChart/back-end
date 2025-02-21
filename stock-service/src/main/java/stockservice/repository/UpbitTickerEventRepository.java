package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Stock_BTC;

@Repository
public interface UpbitTickerEventRepository extends JpaRepository<Stock_BTC, Long> {

    // timestamp 값이 가장 큰 데이터 조회
    Stock_BTC findTopByOrderByTimestampAsc();
}
