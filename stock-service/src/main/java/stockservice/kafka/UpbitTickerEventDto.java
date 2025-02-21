package server.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.domain.Stock_BTC;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpbitTickerEventDto {

    private String type;

    private String code;

    @JsonProperty("opening_price")
    private double openingPrice;

    @JsonProperty("high_price")
    private double highPrice;

    @JsonProperty("low_price")
    private double lowPrice;

    @JsonProperty("trade_price")
    private double tradePrice;

    @JsonProperty("prev_closing_price")
    private double prevClosingPrice;

    @JsonProperty("change")
    private String change;

    @JsonProperty("signed_change_price")
    private double signedChangePrice;

    @JsonProperty("signed_change_rate")
    private double signedChangeRate;

    @JsonProperty("acc_trade_price")
    private double accTradePrice;

    @JsonProperty("acc_trade_volume")
    private double accTradeVolume;
    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;
    @JsonProperty("timestamp")
    private long timestamp;

    public Stock_BTC toEntity() {
        return new Stock_BTC(
                null, // ID는 자동 생성되므로 null로 설정
                "ticker", // type 값은 고정 문자열 "ticker"로 설정
                this.code, // Dto의 market 필드를 entity의 code로 매핑
                this.openingPrice,
                this.highPrice,
                this.lowPrice,
                this.tradePrice,
                this.prevClosingPrice,
                this.change,
                this.signedChangePrice,
                this.signedChangeRate,
                this.accTradePrice,
                this.accTradeVolume,
                this.accTradePrice24h,
                this.timestamp
        );
    }
}