package dnd.diary.response.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateResponse {
    private Long groupId;
    private String groupName;
    private String groupNote;
    private String groupImageUrl;
    private Long groupCreateUserId;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime groupCreatedAt;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime groupModifiedAt;

    // 현재 그룹에 가입된 구성원 정보
    private List<GroupMember> groupMemberList = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    public static class GroupMember {
        private Long userId;
        private String userEmail;
        private String userNickname;
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime userJoinGroupDatedAt;   // 그룹 가입일

        public GroupMember(Long userId, String userEmail, String userNickname, LocalDateTime userJoinGroupDatedAt) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.userNickname = userNickname;
            this.userJoinGroupDatedAt = userJoinGroupDatedAt;
        }
    }
}
