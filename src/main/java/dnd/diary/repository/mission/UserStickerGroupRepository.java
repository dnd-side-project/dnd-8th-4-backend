package dnd.diary.repository.mission;

import dnd.diary.domain.sticker.UserStickerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStickerGroupRepository extends JpaRepository<UserStickerGroup, Long> {
    Boolean existsByUserIdAndStickerGroupId(Long userId, Long stickerGroupId);

    UserStickerGroup findByUserIdAndStickerGroupId(Long userId, Long stickerGroupId);
}
