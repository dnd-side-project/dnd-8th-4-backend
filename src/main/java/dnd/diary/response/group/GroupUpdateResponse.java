package dnd.diary.response.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateResponse {

    private Long groupId;
    private String groupName;
    private String groupNote;
    private String groupImageUrl;
    private Long groupCreateUserId;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime groupCreatedAt;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime groupModifiedAt;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime recentUpdatedAt;   // 게시물 최신 등록일

    private boolean isGroupDelete;

    private boolean deletedYn;

}
