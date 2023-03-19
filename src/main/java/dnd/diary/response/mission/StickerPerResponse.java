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
public class StickerPerResponse {

    private Long stickerGroupId;
    private String stickerGroupName;
    private Long stickerGroupLevel;
    private String stickerGroupThumbnailUrl;

    private StickerInfo stickerInfo;   // 레벨별 스티커 개별 정보

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StickerInfo {
        private Long stickerId;
        private String stickerImageUrl;
        private boolean mainStickerYn;
    }
}