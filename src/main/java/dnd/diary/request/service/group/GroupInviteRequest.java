package dnd.diary.request.service.group;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class GroupInviteRequest {

	private Long groupId;
	private List<Long> invitedUserIdList = new ArrayList<>();
}
