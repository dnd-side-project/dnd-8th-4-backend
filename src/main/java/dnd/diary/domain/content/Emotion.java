package dnd.diary.domain.content;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.user.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Where(clause = "delete_at IS NULL")
@SQLDelete(sql = "UPDATE emotion SET delete_at = CURRENT_TIMESTAMP where emotion_id = ?")
public class Emotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long id;

    @Column(nullable = false)
    private Long emotionStatus;

    // 공감이 표현된 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    // 공감을 남긴 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime deleteAt;

    // 공감 표현 등록 상태 여부 -> 공감 취소 시 emotionYn = false 처리
    private boolean emotionYn = Boolean.TRUE;

    public void updateEmotion(Long emotionStatus){
        this.emotionStatus = emotionStatus;
        this.emotionYn = true;
    }

    public void cancelEmotion() {
        this.emotionYn = false;
    }

    public void reAddEmotion() {
        this.emotionYn = true;
    }
}
