package dnd.diary.request.group;

import lombok.Getter;

@Getter
public class GroupUpdateRequest {
	private Long groupId;
	private String groupName;
	private String groupNote;
	private String groupImageUrl;
}
