package dnd.diary.service.group;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.enumeration.Result.*;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.GroupStarStatus;
import dnd.diary.domain.group.Invite;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.group.GroupInviteRequest;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.dto.group.GroupCreateRequest;
import dnd.diary.dto.group.GroupUpdateRequest;
import dnd.diary.exception.CustomException;
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

import java.time.ZoneId;
import java.time.ZoneOffset;
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

	private final UserService userService;
	private final S3Service s3Service;

	private final int MAX_GROUP_MEMBER_COUNT = 50;

	@Transactional
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
			.groupCreatedAt(convertLocalDateTimeZone(group.getCreatedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.groupModifiedAt(convertLocalDateTimeZone(group.getModifiedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.recentUpdatedAt(convertLocalDateTimeZone(group.getRecentUpdatedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.groupMemberList(List.of(
				new GroupCreateResponse.GroupMember(hostUser.getId(), hostUser.getEmail(), hostUser.getNickName()
					, updateHostUser.getCreatedAt()))
			)
			.build();
	}

	@Transactional
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

		return GroupUpdateResponse.builder()
			.groupId(group.getId())
			.groupName(group.getGroupName())
			.groupNote(group.getGroupNote())
			.groupImageUrl(group.getGroupImageUrl())
			.groupCreateUserId(user.getId())
            .groupCreatedAt(group.getCreatedAt())
            .groupModifiedAt(group.getModifiedAt())
            .recentUpdatedAt(group.getRecentUpdatedAt())
//			.groupCreatedAt(convertLocalDateTimeZone(group.getCreatedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
//			.groupModifiedAt(convertLocalDateTimeZone(group.getModifiedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
//			.recentUpdatedAt(convertLocalDateTimeZone(group.getRecentUpdatedAt(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.isGroupDelete(group.isDeleted())
			.build();
	}

	@Transactional
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
			// groupStarRepository.save(groupStar);
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

	public List<GroupStarListResponse> getGroupStarList() {
		User user = findUser();
		List<GroupStar> userGroupStarList = user.getGroupStars();
		List<GroupStarListResponse> groupListResponseList = new ArrayList<>();

		for (GroupStar groupStar : userGroupStarList) {
			if (groupStar.getGroupStarStatus() == GroupStarStatus.ADD) {
				groupListResponseList.add(
					GroupStarListResponse.builder()
						.groupId(groupStar.getGroup().getId())
						.groupName(groupStar.getGroup().getGroupName())
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

	// 그룹 초대
	@Transactional
	public GroupInviteResponse inviteGroupMember(GroupInviteRequest request) {

		User hostUser = findUser();
		Group inviteGroup = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));

		// 초대는 방장만 가능
		if (!hostUser.getId().equals(inviteGroup.getGroupCreateUser().getId())) {
			throw new CustomException(NO_AUTHORITY_INVITE);
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
			Notification notification = Notification.toEntity(invite, invitedUser);

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

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}