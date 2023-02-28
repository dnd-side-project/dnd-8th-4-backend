package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerGroupResponse {

	private Long stickerGroupId;
	private String stickerGroupName;
	private Long stickerGroupLevel;
	private String stickerGroupThumbnailUrl;
}
