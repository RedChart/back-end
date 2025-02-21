package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StockApiServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(StockApiServiceApplication.class, args);
    }

}
