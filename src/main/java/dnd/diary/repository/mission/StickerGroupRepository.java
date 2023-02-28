package dnd.diary.repository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.sticker.StickerGroup;

@Repository
public interface StickerGroupRepository extends JpaRepository<StickerGroup, Long> {

	Boolean existsByStickerGroupName(String stickerName);
	Boolean existsByStickerGroupLevel(Long stickerLevel);
	StickerGroup findByStickerGroupLevel(Long stickerLevel);
}
