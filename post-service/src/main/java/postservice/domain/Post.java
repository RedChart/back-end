package postservice.domain;

import jakarta.persistence.*;
import lombok.*;


import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;
@Entity
@Builder(toBuilder = true)
@Getter
@Table(
        name = "post",
        indexes = {
                @Index(name = "idx_timestamp", columnList = "createDateAndId"),
        }
)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PUBLIC)
public class Post extends BaseTimeEntity{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "post_id")
        private Long id;

        @Column
        private String createDateAndId;

        @Column(nullable = false)
        private Long writerId;

        @Column(length = 25, nullable = false)
        private String title;

        @Column(length = 100, nullable = false)
        private String content;

        @PostPersist
        private void setRedisKey() {
                this.createDateAndId = id + ":" +  (getCreateDate() != null ? getCreateDate().toString() : "default")  ;
        }
        public Post updatePost(String content, String title) {
                return this.toBuilder()
                        .content(content)
                        .title(title)
                        .build();
        }

}
