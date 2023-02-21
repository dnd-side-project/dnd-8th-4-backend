package dnd.diary.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.group.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
