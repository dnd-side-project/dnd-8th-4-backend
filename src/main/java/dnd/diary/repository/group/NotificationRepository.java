package dnd.diary.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.group.Notification;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByContentIdAndEmotionIdAndUserId(Long contentId, Long emotionId, Long userId);
}
