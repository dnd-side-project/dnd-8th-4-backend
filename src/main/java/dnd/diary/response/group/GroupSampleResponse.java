package dnd.diary.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSampleResponse {

    private Long groupId;
    private String groupImageUrl;
    private String groupName;
}
