package dnd.diary.repository.content;

import dnd.diary.domain.content.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    List<Emotion> findByContentId(Long contentId);
    Emotion findByContentIdAndUserId(Long contentId, Long userId);
    Optional<Emotion> findByContentIdAndUserIdAndEmotionYn(Long contentId, Long userId, boolean emotionYn);
}
