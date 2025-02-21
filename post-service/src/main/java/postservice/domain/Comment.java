package postservice.domain;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

@Entity
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PUBLIC)
public class Comment extends BaseTimeEntity{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "comment_id")
        private Long id;

        @Column(nullable = false)
        private Long writerId;

        @Column(length = 100, nullable = false)
        private String content;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id")
        private Post post;
        public Comment updateComment(String content) {
                return this.toBuilder()
                        .content(content)
                        .build();
        }

}
