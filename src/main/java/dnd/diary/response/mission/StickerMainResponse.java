package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerMainResponse {

    private CurrMissionInfo currMissionInfo;
    private AcquisitionStickerInfo acquisitionStickerInfo;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrMissionInfo {
        private Long subLevel;
        private Long mainLevel;
        private Long remainToUpMainLevel;   // main 레벨 상승까지 남은 subLevel
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcquisitionStickerInfo {
        private Long stickerId;
        private String stickerName;
        private Long stickerLevel;
        private String stickerUrl;
        private Boolean isAcquisitionSticker;   // 조회 유저가 해당 스티커를 획득했는지 여부
    }
}