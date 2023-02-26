package dnd.diary.repository.mission;

import dnd.diary.domain.sticker.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
    Boolean existsByUserIdAndStickerId(Long userId, Long stickerId);
}
