package dnd.diary.repository.content;

import dnd.diary.domain.content.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    Emotion findByContentIdAndUserId(Long contentId, Long userId);
}
