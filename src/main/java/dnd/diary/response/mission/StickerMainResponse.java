package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerMainResponse {

    private CurrMissionInfo currMissionInfo;
    private List<AcquisitionStickerInfo> acquisitionStickerInfo;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrMissionInfo {
        private Long subLevel;
        private Long mainLevel;
        private Long progressBarRange;
//        private Long remainToUpMainLevel;   // main 레벨 상승까지 남은 subLevel
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcquisitionStickerInfo {
        private Long stickerGroupId;
        private String stickerGroupName;
        private Long stickerGroupLevel;
        private String stickerGroupThumbnailUrl;
        private Boolean isAcquisitionStickerGroup;   // 조회 유저가 해당 스티커를 획득했는지 여부
    }
}