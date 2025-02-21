package server.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpbitTickerEventDto {

    String type;

    String code;

    @JsonProperty("opening_price")
    Double openingPrice;

    @JsonProperty("high_price")
    Double highPrice;

    @JsonProperty("low_price")
    Double lowPrice;

    @JsonProperty("trade_price")
    Double tradePrice;

    @JsonProperty("prev_closing_price")
    Double prevClosingPrice;

    @JsonProperty("change")
    String change;

    @JsonProperty("signed_change_price")
    Double signedChangePrice;

    @JsonProperty("signed_change_rate")
    Double signedChangeRate;

    @JsonProperty("acc_trade_price")
    Double accTradePrice;

    @JsonProperty("acc_trade_volume")
    Double accTradeVolume;

    @JsonProperty("acc_trade_price_24h")
    Double accTradePrice24h;

    @JsonProperty("timestamp")
    Long timestamp;

}