package dnd.diary.repository.content;

import dnd.diary.domain.content.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
}
