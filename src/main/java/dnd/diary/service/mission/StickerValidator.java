package dnd.diary.service.mission;

import dnd.diary.exception.CustomException;
import dnd.diary.repository.mission.StickerGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static dnd.diary.enumeration.Result.ALREADY_EXIST_STICKER_LEVEL;
import static dnd.diary.enumeration.Result.ALREADY_EXIST_STICKER_NAME;

@Component
@RequiredArgsConstructor
public class StickerValidator {

    private final StickerGroupRepository stickerGroupRepository;

    public void existStickerThumbnailName(String stickerName) {
        Boolean existStickerName = stickerGroupRepository.existsByStickerGroupName(stickerName);
        if (existStickerName) {
            throw new CustomException(ALREADY_EXIST_STICKER_NAME);
        }
    }

    public void existStickerLevel (Long stickerLevel) {
        Boolean existStickerLevel = stickerGroupRepository.existsByStickerGroupLevel(stickerLevel);
        if (existStickerLevel) {
            throw new CustomException(ALREADY_EXIST_STICKER_LEVEL);
        }
    }
}
