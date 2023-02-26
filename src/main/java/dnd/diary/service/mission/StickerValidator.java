package dnd.diary.service.mission;

import dnd.diary.exception.CustomException;
import dnd.diary.repository.mission.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static dnd.diary.enumeration.Result.ALREADY_EXIST_STICKER_LEVEL;
import static dnd.diary.enumeration.Result.ALREADY_EXIST_STICKER_NAME;

@Component
@RequiredArgsConstructor
public class StickerValidator {

    private final StickerRepository stickerRepository;

    public void existStickerName(String stickerName) {
        Boolean existStickerName = stickerRepository.existsByStickerName(stickerName);
        if (existStickerName) {
            throw new CustomException(ALREADY_EXIST_STICKER_NAME);
        }
    }

    public void existStickerLevel (Long stickerLevel) {
        Boolean existStickerLevel = stickerRepository.existsByStickerLevel(stickerLevel);
        if (existStickerLevel) {
            throw new CustomException(ALREADY_EXIST_STICKER_LEVEL);
        }
    }
}
