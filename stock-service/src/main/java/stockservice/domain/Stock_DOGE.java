package server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_doge", indexes = {
        @Index(name = "IDX_timestamp", columnList = "timestamp")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock_DOGE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String code;

    @Column(name = "opening_price")
    private double openingPrice;

    @Column(name = "high_price")
    private double highPrice;

    @Column(name = "low_price")
    private double lowPrice;

    @Column(name = "trade_price")
    private double tradePrice;

    @Column(name = "prev_closing_price")
    private double prevClosingPrice;
    @Column(name = "changed_price")
    private String changed_price;

    @Column(name = "signed_change_price")
    private double signedChangePrice;

    @Column(name = "signed_change_rate")
    private double signedChangeRate;

    @Column(name = "acc_trade_price")
    private double accTradePrice;

    @Column(name = "acc_trade_volume")
    private double accTradeVolume;

    @Column(name = "acc_trade_price_24h")
    private double accTradePrice24h;

    @Column(name = "timestamp")
    private long timestamp;
}