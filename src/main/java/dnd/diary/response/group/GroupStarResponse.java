package dnd.diary.response.group;

import dnd.diary.domain.group.GroupStarStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupStarResponse {

	private Long userId;
	private Long groupId;
	private GroupStarStatus groupStarYn;
}
