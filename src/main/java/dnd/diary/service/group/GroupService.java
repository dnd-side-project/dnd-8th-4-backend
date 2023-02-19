package dnd.diary.service.group;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.GroupStarStatus;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.UserDto;
import dnd.diary.dto.request.group.GroupCreateRequest;
import dnd.diary.dto.request.group.GroupUpdateRequest;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.GroupStarRepository;
import dnd.diary.repository.UserRepository;
import dnd.diary.response.group.*;
import dnd.diary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static dnd.diary.enumeration.Result.*;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final GroupStarRepository groupStarRepository;
	private final UserService userService;

	public GroupCreateResponse createGroup(GroupCreateRequest request) {
		User user = findUser();
		Group group = Group.toEntity(request.getGroupName(), request.getGroupNote(), request.getGroupImageUrl());
		groupRepository.save(group);

		// 그룹 생성자 가입 처리 추가

		return GroupCreateResponse.builder()
				.groupId(group.getId())
				.groupName(group.getGroupName())
				.groupNote(group.getGroupNote())
				.groupImageUrl(group.getGroupImageUrl())
				.groupCreateUserId(user.getId())
				.groupCreatedAt(group.getCreatedAt())
				.groupModifiedAt(group.getModifiedAt())
				.build();
	}

	public GroupUpdateResponse updateGroup(GroupUpdateRequest request) {
		User user = findUser();
		Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
		group.update(request.getGroupName(), request.getGroupNote(), request.getGroupImageUrl());

		return GroupUpdateResponse.builder()
			.groupId(group.getId())
			.groupName(group.getGroupName())
			.groupNote(group.getGroupNote())
			.groupImageUrl(group.getGroupImageUrl())
			.groupCreateUserId(user.getId())
			.groupCreatedAt(group.getCreatedAt())
			.groupModifiedAt(group.getModifiedAt())
			.build();
	}

	public void deleteGroup(Long groupId) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
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
		}

		GroupStar newGroupStar = groupStarRepository.findByGroupIdAndUserId(groupId, user.getId());
		return GroupStarResponse.builder()
				.userId(user.getId())
				.groupId(group.getId())
				.groupStarYn(newGroupStar.getGroupStarStatus())
				.build();
	}

	// 내가 속한 그룹 목록 조회
	public List<GroupListResponse> getGroupList() {
		User user = findUser();

		List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
		if (userJoinGroupList.size() == 0) {
			// 가입한 그룹이 없는 경우
			throw new CustomException(NO_USER_GROUP_LIST);
		}
		List<Group> groupList = new ArrayList<>();
		userJoinGroupList.forEach(
			userJoinGroup -> groupList.add(userJoinGroup.getGroup())
		);
		List<GroupListResponse> groupListResponseList = new ArrayList<>();
		groupList.forEach(
			group -> groupListResponseList.add(GroupListResponse.builder()
				.build())
		);
		return groupListResponseList;
	}

	// 그룹에 속한 게시글 조회
/*
	public PageResponse<List<ContentResponse>> getGroupContentList() {

		User user = findUser(1L);
		List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
		if (userJoinGroupList.size() == 0) {
			// 가입한 그룹이 없는 경우
			throw new CustomException(NO_USER_GROUP_LIST);
		}
		List<Group> groupList = new ArrayList<>();
		userJoinGroupList.forEach(
			userJoinGroup -> groupList.add(userJoinGroup.getGroup())
		);
		List<Content> contentList = new ArrayList<>();
		groupList.forEach(
			group -> contentList.addAll(group.getContents())
		);
		List<ContentResponse> contentResponseList = new ArrayList<>();
		contentList.forEach(
			content -> contentResponseList.add(ContentResponse.builder()
				.build())
		);
		return PageResponse.<List<ContentResponse>>builder()
			.contents(contentResponseList)
			.build();
	}
 */

	/*
	public PageResponse<List<GroupListResponse>> searchGroupList(GroupSearchRequest request) {

		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.Direction.DESC);
		PageImpl<Group> groupList = groupRepository.searchGroupByKeyword(request.getKeyword(), pageable);
		List<GroupListResponse> groupListResponseList = new ArrayList<>();
		groupList.forEach(
			group -> groupListResponseList.add(GroupListResponse.builder()
					.groupId(group.getId())
					.groupName(group.getGroupName())
					.groupNote(group.getGroupNote())
					.groupCreatedAt(group.getCreatedAt())
					.memberCount(group.getUserJoinGroups().size())
					.isStarGroup(false)
				.build())
		);

		return PageResponse.<List<GroupListResponse>>builder()
			.contents(groupListResponseList)
			.build();
	}
	 */

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

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}
