package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import java.util.List;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.repository.mission.UserAssignMissionRepository;
import dnd.diary.response.notification.InviteNotificationResponse;
import dnd.diary.service.content.ContentService;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteService {

	private final GroupRepository groupRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final UserJoinGroupRepository userJoinGroupRepository;
	private final UserAssignMissionRepository userAssignMissionRepository;
	private final ContentRepository contentRepository;

	private final UserService userService;
	private final ContentService contentService;

	// 초대 수락
	@Transactional
	public InviteNotificationResponse.InviteNotificationInfo acceptInvite(UserDetails userDetails, Long groupId, Long notificationId) throws ParseException {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);

		UserJoinGroup userJoinGroup = UserJoinGroup.toEntity(user, invitedGroup);
		userJoinGroupRepository.save(userJoinGroup);

		// 이미 그룹에 존재하는 미션 할당 처리
		for (Mission mission : invitedGroup.getMissions()) {
			UserAssignMission addUserAssignMission = UserAssignMission.toEntity(user, mission);
			userAssignMissionRepository.save(addUserAssignMission);
			log.info("그룹 가입 수락으로 추가될 미션 ID : {}", mission.getId());
			log.info("그룹 가입 수락으로 추가될 할당된 addUserAssignMission ID : {}", addUserAssignMission.getId());
		}

		Notification notification = findNotification(notificationId);
		notification.updateReadNotification();

		// 1. 초대 수락한 그룹에 새 멤버 환영 게시물 생성
		ContentDto.CreateDto contentResponse = contentService.createContent(
				userDetails, null, groupId, String.format("%s 님이 그룹에 참여했습니다. 댓글로 반갑게 인사해 주세요!🎉", user.getNickName())
				, null, null, null
		);
		Content newGroupMemberContent = contentRepository.findByIdAndDeletedYn(contentResponse.getId(), false);
		if (newGroupMemberContent == null) {
			throw new CustomException(NOT_FOUND_CONTENT);
		}

		// 2. 초대 수락한 그룹에 속해 있는 구성원에게 [새 구성원 가입] 알림 발행
		List<UserJoinGroup> userJoinGroups = invitedGroup.getUserJoinGroups();
		userJoinGroups.forEach(
			alreadyUserJoinGroup -> {
				User alreadyGroupUser = alreadyUserJoinGroup.getUser();
				if (!user.getId().equals(alreadyGroupUser.getId())) {   // 가입자 제외 새 멤버 알림 전송
					Notification newGroupMemberNotification = Notification.toNewGroupMemberEntity(invitedGroup, user, alreadyGroupUser, newGroupMemberContent, NotificationType.NEW_GROUP_MEMBER);
					notificationRepository.save(newGroupMemberNotification);

					alreadyGroupUser.updateNewNotification();
				}
			}
		);

		return toNotificationResponse(notification, invitedGroup);
	}

	// 초대 거절
	@Transactional
	public InviteNotificationResponse.InviteNotificationInfo rejectInvite(Long groupId, Long notificationId) {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);

		Notification notification = findNotification(notificationId);
		notification.updateReadNotification();

		return toNotificationResponse(notification, invitedGroup);
	}

	private InviteNotificationResponse.InviteNotificationInfo toNotificationResponse(Notification notification, Group invitedGroup) {
		InviteNotificationResponse.InviteNotificationInfo notificationInfo = InviteNotificationResponse.InviteNotificationInfo.builder()
				.notificationType(NotificationType.INVITE)
				.notificationId(notification.getId())
				.groupId(invitedGroup.getId())
				.groupName(invitedGroup.getGroupName())
				.groupNote(invitedGroup.getGroupNote())
				.groupImageUrl(invitedGroup.getGroupImageUrl())
				.groupInvitedAt(notification.getCreatedAt())
				.readYn(notification.isReadYn())
				.build();

		return notificationInfo;
	}

	private void checkAlreadyExist(User user, Group group) {
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			if (user.getId().equals(userJoinGroup.getUser().getId())) {
				throw new CustomException(ALREADY_EXIST_IN_GROUP);
			}
		}
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_GROUP));
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Notification findNotification(Long notificationId) {
		return notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTIFICATION));
	}
}
