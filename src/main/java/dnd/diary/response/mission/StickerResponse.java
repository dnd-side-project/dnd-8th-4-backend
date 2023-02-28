package dnd.diary.response.mission;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerResponse {

    private Long stickerGroupId;
    private String stickerGroupName;
    private Long stickerGroupLevel;
    private String stickerGroupThumbnailUrl;

    private List<StickerInfo> stickerInfo;   // 레벨별 스티커 개별 정보

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StickerInfo {
        private Long stickerId;
        private String stickerImageUrl;
    }
}
