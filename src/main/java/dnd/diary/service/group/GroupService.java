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
import dnd.diary.dto.group.GroupCreateRequest;
import dnd.diary.dto.group.GroupUpdateRequest;
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

		// ?????? ????????? ??????
		String imageUrl = "";
		if (multipartFile != null) {
			imageUrl = s3Service.uploadImage(multipartFile);
		} else {   // null ??? ?????? ?????? ????????? ??????
			int sampleGroupImageCount = groupImageRepository.findAll().size();
			int randomIdx = getRandomNumber(1, sampleGroupImageCount);
			GroupImage sampleGroupImage = groupImageRepository.findById((long)randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP_IMAGE));
			imageUrl = sampleGroupImage.getGroupImageUrl();
		}

		// ?????? ?????? ?????? ??????
		validCreateAndUpdateGroup(groupName);

		Group group = Group.toEntity(groupName, groupNote, imageUrl, hostUser);
		groupRepository.save(group);

		// ?????? ????????? ?????? ?????? ??????
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

	@Transactional
	public GroupUpdateResponse updateGroup(MultipartFile multipartFile, Long groupId, String groupName, String groupNote) {
		User user = findUser();
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// ?????? ????????? ????????? ?????? ??????
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_UPDATE_GROUP);
		}

		String imageUrl = "";
		// ?????? ????????? ?????? - ????????? ????????? ?????? ???????????? ????????? ?????? ?????? ??????
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
			.build();
	}

	@Transactional
	public void deleteGroup(Long groupId) {
		User user = findUser();
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// ????????? ?????? ??????
		if (!group.getGroupCreateUser().getId().equals(user.getId())) {
			throw new CustomException(FAIL_DELETE_GROUP);
		}

		// ?????? ??? ??????????????? ?????? ?????? ??????
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		userJoinGroupRepository.deleteAll(userJoinGroupList);

		// ?????? ?????? ??????
		groupRepository.delete(group);

	}

	@Transactional
	public GroupStarResponse starGroup(Long groupId) {
		User user = findUser();

		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
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

	// ?????? ?????? ?????? ?????? ??????
	public GroupListResponse getGroupList() {
		User user = findUser();

		List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
		if (userJoinGroupList.size() == 0) {
			// ????????? ????????? ?????? ??????
			return GroupListResponse.builder()
				.existGroup(false)
				.build();
		}
		// ???????????? ????????? ?????? ?????? ??????
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

	// ?????? ?????? ?????? ?????? Simple ??????
	public List<GroupSampleResponse> getGroupSimpleList() {
		User user = findUser();
		List<UserJoinGroup> userJoinGroupList = user.getUserJoinGroups();
		List<GroupSampleResponse> groupSampleResponseList = new ArrayList<>();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			Group group = userJoinGroup.getGroup();
			groupSampleResponseList.add(
					GroupSampleResponse.builder()
							.groupId(group.getId())
							.groupName(group.getGroupName())
							.groupImageUrl(group.getGroupImageUrl())
							.build()
			);
		}
		return groupSampleResponseList;
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
		List<Group> searchGroupList = groupRepository.findByGroupNameContainingIgnoreCaseOrGroupNoteContainingIgnoreCase(keyword, keyword);

		for (Group group : searchGroupList) {
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

	// ?????? ??????
	@Transactional
	public GroupInviteResponse inviteGroupMember(GroupInviteRequest request) {

		User hostUser = findUser();
		Group inviteGroup = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// ????????? ????????? ??????
		if (!hostUser.getId().equals(inviteGroup.getGroupCreateUser().getId())) {
			throw new CustomException(NO_AUTHORITY_INVITE);
		}
		// ?????? ???????????? ????????? ?????? 50??? ????????? ??????(?????? ??????)
		if (request.getInvitedUserIdList().size() >= MAX_GROUP_MEMBER_COUNT) {
			throw new CustomException(HIGH_MAX_INVITE_MEMBER_COUNT);
		}
		// ??????????????? ?????? + ?????? ?????? ?????? > 50???
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
			log.info("?????? ID : {}", invite.getId());
			notificationRepository.save(notification);
			log.info("????????? ?????? ID : {} , ????????? ?????? ?????? ID : {}", notification.getId(), notification.getInvite().getGroup().getId());

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

		// ?????? ????????? ??????
		List<UserJoinGroup> userJoinGroupList = targetGroup.getUserJoinGroups();
		List<GroupDetailResponse.GroupMemberInfo> groupMemberInfoList = new ArrayList<>();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			User groupUser = userJoinGroup.getUser();
			GroupDetailResponse.GroupMemberInfo groupMemberInfo = GroupDetailResponse.GroupMemberInfo.builder()
				.userId(groupUser.getId())
				.userName(groupUser.getName())
				.userNickname(groupUser.getNickName())
				.userEmail(groupUser.getEmail())
				.userProfileImageUrl(groupUser.getProfileImageUrl())
				.userJoinGroupDatedAt(userJoinGroup.getCreatedAt())   // ????????? ?????? ?????????
				.build();

			groupMemberInfoList.add(groupMemberInfo);
		}

		return GroupDetailResponse.builder()
			.groupId(targetGroup.getId())
			.groupName(targetGroup.getGroupName())
			.groupNote(targetGroup.getGroupNote())
			.groupImageUrl(targetGroup.getGroupImageUrl())
			.hostUserInfo(new GroupDetailResponse.HostUserInfo(
				targetGroup.getGroupCreateUser().getId(), targetGroup.getGroupCreateUser().getNickName(), targetGroup.getGroupCreateUser().getProfileImageUrl()
			))
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
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
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