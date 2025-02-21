package postservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor @Getter
public class PostEventDto {
    private String type;
    private Long postId;
    private LocalDateTime createDate;;
    private Long postWriterId;
}
