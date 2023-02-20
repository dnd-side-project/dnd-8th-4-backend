package dnd.diary.response.group;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSearchResponse {

	List<GroupSearchInfo> groupSearchInfoList;

	public static class GroupSearchInfo {
		private Long groupId;
		private String groupName;
		private String groupNote;
		private String groupImageUrl;
		@JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
		private LocalDateTime groupCreatedAt;
		@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime recentUpdatedAt;
		private int memberCount;
		private Boolean isStarGroup;
	}
}
