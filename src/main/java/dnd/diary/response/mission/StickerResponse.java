package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerResponse {

    private Long stickerId;
    private String stickerName;
    private Long stickerLevel;
    private String stickerUrl;
}
