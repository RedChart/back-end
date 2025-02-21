package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

}
