package dnd.diary.service.group;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.GroupStarStatus;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.dto.request.group.GroupCreateRequest;
import dnd.diary.dto.request.group.GroupUpdateRequest;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.GroupStarRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.response.group.*;
import dnd.diary.service.user.UserService;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dnd.diary.enumeration.Result.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final GroupStarRepository groupStarRepository;

	private final UserService userService;
	private final UserJoinGroupRepository userJoinGroupRepository;
	private final S3Service s3Service;

	public GroupCreateResponse createGroup(MultipartFile multipartFile, GroupCreateRequest request) {
		User hostUser = findUser();

		// 그룹 이미지 처리
		String imageUrl = "";
		if (multipartFile != null) {
			imageUrl = s3Service.uploadImage(multipartFile);
		}

		Group group = Group.toEntity(request.getGroupName(), request.getGroupNote(), imageUrl, hostUser);
		groupRepository.save(group);

		// 그룹 생성자 가입 처리 추가
		UserJoinGroup updateHostUser = UserJoinGroup.toEntity(hostUser, group);
		userJoinGroupRepository.save(updateHostUser);

		return GroupCreateResponse.builder()
				.groupId(group.getId())
				.groupName(group.getGroupName())
				.groupNote(group.getGroupNote())
				.groupImageUrl(group.getGroupImageUrl())
				.groupCreateUserId(hostUser.getId())
				.groupCreatedAt(group.getCreatedAt())
				.groupModifiedAt(group.getModifiedAt())
				.recentUpdatedAt(group.getRecentUpdatedAt())
				.groupMemberList(List.of(
						new GroupCreateResponse.GroupMember(hostUser.getId(), hostUser.getEmail(), hostUser.getNickName()
							, updateHostUser.getCreatedAt()))
				)
				.build();
	}

	public GroupUpdateResponse updateGroup(MultipartFile multipartFile, GroupUpdateRequest request) {
		User user = findUser();
		Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// 그룹 호스트 유저만 수정 가능
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_UPDATE_GROUP);
		}

		String imageUrl = "";
		// 그룹 이미지 추가 - 기존에 저장된 그룹 이미지와 동일한 경우 체크 제외
		if (multipartFile != null) {
			imageUrl = s3Service.uploadImage(multipartFile);
		}

		group.update(request.getGroupName(), request.getGroupNote(), imageUrl);
		groupRepository.save(group);

		return GroupUpdateResponse.builder()
			.groupId(group.getId())
			.groupName(group.getGroupName())
			.groupNote(group.getGroupNote())
			.groupImageUrl(group.getGroupImageUrl())
			.groupCreateUserId(user.getId())
			.groupCreatedAt(group.getCreatedAt())
			.groupModifiedAt(group.getModifiedAt())
			.recentUpdatedAt(group.getRecentUpdatedAt())
			.isGroupDelete(group.isDeleted())
			.build();
	}

	public void deleteGroup(Long groupId) {
		User user = findUser();
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// 방장만 삭제 가능
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_DELETE_GROUP);
		}

		// 그룹 내 구성원들의 그룹 탈퇴 처리
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		userJoinGroupRepository.deleteAll(userJoinGroupList);

		// 그룹 삭제 처리
		groupRepository.delete(group);

	}

	public GroupStarResponse starGroup(Long groupId) {
		User user = findUser();

		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
		GroupStar groupStar = groupStarRepository.findByGroupIdAndUserId(groupId, user.getId());

		if (groupStar == null) {
			GroupStar newGroupStar = GroupStar.toEntity(group, user);
			groupStarRepository.save(newGroupStar);
		} else {
			// 즐겨찾기 등록상태이면 -> 해제 / 등록되어 있지 않으면 -> 등록
			if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD) {
				groupStar.update(GroupStarStatus.DELETE);
			} else {
				groupStar.update(GroupStarStatus.ADD);
			}
			groupStarRepository.save(groupStar);
		}

		GroupStar newGroupStar = groupStarRepository.findByGroupIdAndUserId(groupId, user.getId());
		return GroupStarResponse.builder()
				.userId(user.getId())
				.groupId(group.getId())
				.groupStarYn(newGroupStar.getGroupStarStatus())
				.build();
	}

	// 내가 속한 그룹 목록 조회
	public GroupListResponse getGroupList() {
        User user = findUser();

        List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
        if (userJoinGroupList.size() == 0) {
            // 가입한 그룹이 없는 경우
            return GroupListResponse.builder()
                    .existGroup(false)
                    .build();
        }
        // 사용자가 가입한 그룹 목록 조회
        List<Group> groupList = new ArrayList<>();
        userJoinGroupList.forEach(
                userJoinGroup -> groupList.add(userJoinGroup.getGroup())
        );

        GroupListResponse response = new GroupListResponse();
        response.setExistGroup(true);

        List<GroupListResponse.GroupInfo> groupInfoList = new ArrayList<>();
        for (Group group : groupList) {
            boolean isStarGroup = false;
            for (GroupStar groupStar : group.getGroupStars()) {
                if (user.getId().equals(groupStar.getUser().getId())) {
                    isStarGroup = true;
                    break;
                }
            }
            GroupListResponse.GroupInfo groupInfo = GroupListResponse.GroupInfo.builder()
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .groupNote(group.getGroupNote())
					.groupImageUrl(group.getGroupImageUrl())
                    .groupCreatedAt(group.getCreatedAt())
                    .recentUpdatedAt(group.getRecentUpdatedAt())
                    .memberCount(group.getUserJoinGroups().size())
                    .isStarGroup(isStarGroup)   // 조회 유저가 해당 그룹을 즐겨찾기 했는지 여부
                    .build();

			groupInfoList.add(groupInfo);
        }

        groupInfoList.sort(Comparator.comparing(GroupListResponse.GroupInfo::getRecentUpdatedAt).reversed());
		response.setGroupInfoList(groupInfoList);

        return response;
    }

	public List<GroupStarListResponse> getGroupStarList() {
		User user = findUser();
		List<GroupStar> userGroupStarList = user.getGroupStars();
		List<GroupStarListResponse> groupListResponseList = new ArrayList<>();
		userGroupStarList.forEach(
				userGroupStar -> groupListResponseList.add(
						GroupStarListResponse.builder()
								.groupId(userGroupStar.getGroup().getId())
								.groupName(userGroupStar.getGroup().getGroupName())
								.build()
				)
		);

		return groupListResponseList;
	}
	
	public GroupListResponse searchGroupList(String keyword) {
		User user = findUser();
		if (user.getUserJoinGroups().size() == 0) {
			return GroupListResponse.builder().existGroup(false).build();
		}
		GroupListResponse response = new GroupListResponse();
		response.setExistGroup(true);
		List<GroupListResponse.GroupInfo> groupInfoList = new ArrayList<>();
		List<Group> searchGroupList = groupRepository.findByGroupNameContainingIgnoreCaseOrGroupNoteContainingIgnoreCase(keyword, keyword);

		for (Group group : searchGroupList) {
			boolean isStarGroup = false;
			for (GroupStar groupStar : group.getGroupStars()) {
				if (user.getId().equals(groupStar.getUser().getId())) {
					isStarGroup = true;
					break;
				}
			}
			GroupListResponse.GroupInfo groupInfo = GroupListResponse.GroupInfo.builder()
				.groupId(group.getId())
				.groupName(group.getGroupName())
				.groupNote(group.getGroupNote())
				.groupImageUrl(group.getGroupImageUrl())
				.groupCreatedAt(group.getCreatedAt())
				.recentUpdatedAt(group.getRecentUpdatedAt())
				.memberCount(group.getUserJoinGroups().size())
				.isStarGroup(isStarGroup)
				.build();

			groupInfoList.add(groupInfo);
		}
		groupInfoList.sort(Comparator.comparing(GroupListResponse.GroupInfo::getRecentUpdatedAt).reversed());
		response.setGroupInfoList(groupInfoList);

		return response;
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}
