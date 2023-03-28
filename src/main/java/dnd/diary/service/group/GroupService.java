package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.GroupImage;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.GroupStarStatus;
import dnd.diary.domain.group.Invite;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.group.GroupInviteRequest;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupImageRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.GroupStarRepository;
import dnd.diary.repository.group.InviteRepository;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.response.group.*;
import dnd.diary.service.user.UserService;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final UserJoinGroupRepository userJoinGroupRepository;
	private final GroupStarRepository groupStarRepository;
	private final InviteRepository inviteRepository;
	private final NotificationRepository notificationRepository;
	private final GroupImageRepository groupImageRepository;

	private final UserService userService;
	private final S3Service s3Service;

	private final int MAX_GROUP_MEMBER_COUNT = 50;

	@Transactional
	public GroupCreateResponse createGroup(MultipartFile multipartFile, String groupName, String groupNote) {
		User hostUser = findUser();

		// 그룹 이미지 처리
		String imageUrl = "";
		if (multipartFile != null) {
			imageUrl = s3Service.uploadImage(multipartFile);
		} else {   // null 일 경우 기본 이미지 세팅
			int sampleGroupImageCount = groupImageRepository.findAll().size();
			int randomIdx = getRandomNumber(1, sampleGroupImageCount);
			GroupImage sampleGroupImage = groupImageRepository.findById((long)randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP_IMAGE));
			imageUrl = sampleGroupImage.getGroupImageUrl();
		}

		// 그룹 이름 중복 체크
		validCreateAndUpdateGroup(groupName);

		Group group = Group.toEntity(groupName, groupNote, imageUrl, hostUser);
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
			.deletedYn(group.isDeletedYn())
			.groupMemberList(List.of(
				new GroupCreateResponse.GroupMember(hostUser.getId(), hostUser.getEmail(), hostUser.getNickName()
					, updateHostUser.getCreatedAt()))
			)
			.build();
	}

	@Transactional
	public GroupUpdateResponse updateGroup(MultipartFile multipartFile, Long groupId, String groupName, String groupNote) {
		User user = findUser();
		Group group = findGroup(groupId);
		// 그룹 호스트 유저만 수정 가능
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_UPDATE_GROUP);
		}

		String imageUrl = "";
		// 그룹 이미지 추가 - 기존에 저장된 그룹 이미지와 동일한 경우 체크 제외
		if (multipartFile != null) {
			imageUrl = s3Service.uploadImage(multipartFile);
		} else {
			int sampleGroupImageCount = groupImageRepository.findAll().size();
			int randomIdx = getRandomNumber(1, sampleGroupImageCount);
			GroupImage sampleGroupImage = groupImageRepository.findById((long)randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP_IMAGE));
			imageUrl = sampleGroupImage.getGroupImageUrl();
		}

		group.update(groupName, groupNote, imageUrl);

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
			.deletedYn(group.isDeletedYn())
			.build();
	}

	@Transactional
	public void deleteGroup(Long groupId) {
		User user = findUser();
		Group group = findGroup(groupId);
		// 방장만 삭제 가능
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_DELETE_GROUP);
		}

		// 그룹 내 구성원들의 그룹 탈퇴 처리
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		userJoinGroupRepository.deleteAll(userJoinGroupList);

		// 그룹 삭제 처리
//		groupRepository.delete(group);
		group.deleteGroupByColumn();

	}

	@Transactional
	public GroupStarResponse starGroup(Long groupId) {
		User user = findUser();
		Group group = findGroup(groupId);
		GroupStar groupStar = groupStarRepository.findByGroupIdAndUserId(groupId, user.getId());

		if (groupStar == null) {
			GroupStar newGroupStar = GroupStar.toEntity(group, user);
			groupStarRepository.save(newGroupStar);
		} else {
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
			userJoinGroup -> {
				if (!userJoinGroup.getGroup().isDeletedYn()) {
					groupList.add(userJoinGroup.getGroup());
				}
			}
		);

		GroupListResponse response = new GroupListResponse();
		response.setExistGroup(true);

		List<GroupListResponse.GroupInfo> groupInfoList = new ArrayList<>();
		for (Group group : groupList) {
			if (group.isDeletedYn()) {
				continue;
			}
			boolean isStarGroup = false;
			for (GroupStar groupStar : group.getGroupStars()) {
				if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD && user.getId().equals(groupStar.getUser().getId())) {
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

	// 내가 속한 그룹 목록 Simple 조회
	public List<GroupSimpleResponse> getGroupSimpleList() {
		User user = findUser();
		List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
		List<GroupSimpleResponse> groupSimpleResponseList = new ArrayList<>();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			Group group = userJoinGroup.getGroup();
			if (group.isDeletedYn()) {
				continue;
			}
			groupSimpleResponseList.add(
					GroupSimpleResponse.builder()
							.groupId(group.getId())
							.groupName(group.getGroupName())
							.groupImageUrl(group.getGroupImageUrl())
							.build()
			);
		}
		return groupSimpleResponseList;
	}

	public List<GroupStarListResponse> getGroupStarList() {
		User user = findUser();
		List<GroupStar> userGroupStarList = user.getGroupStars();
		List<GroupStarListResponse> groupListResponseList = new ArrayList<>();

		for (GroupStar groupStar : userGroupStarList) {
			if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD) {
				Group group = groupStar.getGroup();
				groupListResponseList.add(
					GroupStarListResponse.builder()
						.groupId(group.getId())
						.groupName(group.getGroupName())
						.groupImageUrl(group.getGroupImageUrl())
						.memberCount(group.getUserJoinGroups().size())
						.build()
				);
			}
		}

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
		List<Group> searchGroupList = groupRepository.findByGroupNameContainingIgnoreCaseOrGroupNoteContainingIgnoreCaseAndDeletedYn(keyword, keyword, false);

		for (Group group : searchGroupList) {
			if (group.isDeletedYn()) {
				continue;
			}
			boolean isStarGroup = false;
			for (GroupStar groupStar : group.getGroupStars()) {
				if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD && user.getId().equals(groupStar.getUser().getId())) {
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

	// 그룹 초대
	@Transactional
	public GroupInviteResponse inviteGroupMember(GroupInviteRequest request) {

		User hostUser = findUser();
		Group inviteGroup = findGroup(request.getGroupId());

		// 초대는 방장만 가능
		// if (!hostUser.getId().equals(inviteGroup.getGroupCreateUser().getId())) {
		// 	throw new CustomException(NO_AUTHORITY_INVITE);
		// }

		// 초대는 그룹 구성원 모두 가능
		UserJoinGroup checkUserJoinGroup = userJoinGroupRepository.findUserJoinGroupByUserIdAndGroupId(hostUser.getId(), inviteGroup.getId());
		if (checkUserJoinGroup == null) {
			throw new CustomException(NOT_GROUP_MEMBER);
		}

		// 초대 시도하는 사용자 수가 50명 이상인 경우(방장 제외)
		if (request.getInvitedUserIdList().size() >= MAX_GROUP_MEMBER_COUNT) {
			throw new CustomException(HIGH_MAX_INVITE_MEMBER_COUNT);
		}
		// 초대하려는 인원 + 기존 그룹 인원 > 50명
		if (request.getInvitedUserIdList().size() + inviteGroup.getUserJoinGroups().size() >= MAX_GROUP_MEMBER_COUNT) {
			throw new CustomException(HIGH_MAX_GROUP_MEMBER_COUNT);
		}

		List<UserJoinGroup> userJoinGroupList = inviteGroup.getUserJoinGroups();
		List<Long> groupUserIdList = new ArrayList<>();
		userJoinGroupList.forEach(
			userJoinGroup -> groupUserIdList.add(userJoinGroup.getUser().getId())
		);

		List<GroupInviteResponse.InvitedUserInfo> invitedUserInfoList = new ArrayList<>();

		List<Invite> inviteList = new ArrayList<>();
		for (Long userId : request.getInvitedUserIdList()) {
			User invitedUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
			if (groupUserIdList.contains(invitedUser.getId())) {
				throw new CustomException(ALREADY_EXIST_IN_GROUP);
			}
			invitedUserInfoList.add(new GroupInviteResponse.InvitedUserInfo(invitedUser));

			Invite invite = Invite.toEntity(inviteGroup, invitedUser);
			Notification notification = Notification.toInviteEntity(invite, invitedUser, NotificationType.INVITE);

			inviteRepository.save(invite);
			log.info("초대 ID : {}", invite.getId());
			notificationRepository.save(notification);
			log.info("생성된 알림 ID : {} , 알림을 보낸 그룹 ID : {}", notification.getId(), notification.getInvite().getGroup().getId());

			inviteList.add(invite);
		}

		return GroupInviteResponse.builder()
			.groupId(inviteGroup.getId())
			.groupName(inviteGroup.getGroupName())
			.hostUser(new GroupInviteResponse.HostUser(inviteGroup.getGroupCreateUser()))
			.invitedUserInfoList(invitedUserInfoList)
			.successInvitedUserCount(invitedUserInfoList.size())
			.build();
	}

	public GroupDetailResponse getGroupDetail(Long groupId) {

		User user = findUser();
		Group targetGroup = findGroup(groupId);

		boolean isStarGroup = false;
		for (GroupStar groupStar : targetGroup.getGroupStars()) {
			if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD && user.getId().equals(groupStar.getUser().getId())) {
				isStarGroup = true;
				break;
			}
		}

		User hostUser = targetGroup.getGroupCreateUser();

		// 그룹 구성원 정보
		List<UserJoinGroup> userJoinGroupList = targetGroup.getUserJoinGroups();
		List<GroupDetailResponse.GroupMemberInfo> groupMemberInfoList = new ArrayList<>();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			User groupUser = userJoinGroup.getUser();
			GroupDetailResponse.GroupMemberInfo groupMemberInfo = GroupDetailResponse.GroupMemberInfo.builder()
				.userId(groupUser.getId())
				.userName(groupUser.getNickName())
				.userNickname(groupUser.getNickName())
				.userEmail(groupUser.getEmail())
				.userProfileImageUrl(groupUser.getProfileImageUrl())
				.userJoinGroupDatedAt(userJoinGroup.getCreatedAt())   // 유저의 그룹 가입일
				.build();

			groupMemberInfoList.add(groupMemberInfo);
		}

		return GroupDetailResponse.builder()
			.groupId(targetGroup.getId())
			.groupName(targetGroup.getGroupName())
			.groupNote(targetGroup.getGroupNote())
			.groupImageUrl(targetGroup.getGroupImageUrl())
			.isHostUser(Objects.equals(user.getId(), hostUser.getId()))
			.hostUserInfo(new GroupDetailResponse.HostUserInfo(hostUser.getId(), hostUser.getNickName(), hostUser.getProfileImageUrl()))
			.groupCreatedAt(targetGroup.getCreatedAt())
			.groupModifiedAt(targetGroup.getModifiedAt())
			.groupRecentUpdatedAt(targetGroup.getRecentUpdatedAt())
			.memberCount(userJoinGroupList.size())
			.isStarGroup(isStarGroup)
			.groupMemberInfoList(groupMemberInfoList)
			.build();
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findByIdAndDeletedYn(groupId, false).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
	}

	private void validCreateAndUpdateGroup(String groupName) {
		Boolean existGroupName = groupRepository.existsByGroupName(groupName);
		if (existGroupName) {
			throw new CustomException(ALREADY_EXIST_GROUP_NAME);
		}
	}

	private int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}
}