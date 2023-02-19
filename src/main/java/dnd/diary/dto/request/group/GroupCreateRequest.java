package dnd.diary.dto.request.group;

import lombok.Getter;

@Getter
public class GroupCreateRequest {

	private String groupName;
	private String groupNote;
	private String groupImageUrl;

}
