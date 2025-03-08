package newsfeedservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter @AllArgsConstructor @NoArgsConstructor
public class PostEventDto {
    private String type;
    private String postCreateDateAndId;
    private Long postWriterId;
}
