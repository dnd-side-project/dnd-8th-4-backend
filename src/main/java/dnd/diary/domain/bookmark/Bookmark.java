package dnd.diary.domain.bookmark;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bookmark")
@Builder
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", content=" + content +
                ", user=" + user +
                '}';
    }
}
