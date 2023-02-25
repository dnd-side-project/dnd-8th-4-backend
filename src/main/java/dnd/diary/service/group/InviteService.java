package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import java.util.List;

import dnd.diary.domain.group.Notification;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.repository.mission.UserAssignMissionRepository;
import dnd.diary.response.notification.NotificationResponse;
import org.springframework.stereotype.Service;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.InviteRepository;
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

	private final UserService userService;

	// 초대 수락
	@Transactional
	public NotificationResponse.NotificationInfo acceptInvite(Long groupId, Long notificationId) {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);   //

		// 그룹 가입 처리
		UserJoinGroup userJoinGroup = UserJoinGroup.toEntity(user, invitedGroup);
		userJoinGroupRepository.save(userJoinGroup);

		/**
		 * 이미 그룹에 존재하는 미션 할당 처리
		 */
		for (Mission mission : invitedGroup.getMissions()) {
			UserAssignMission addUserAssignMission = UserAssignMission.toEntity(user, mission);
			userAssignMissionRepository.save(addUserAssignMission);
			log.info("그룹 가입 수락으로 추가될 미션 ID : {}", mission.getId());
			log.info("그룹 가입 수락으로 추가될 할당된 addUserAssignMission ID : {}", addUserAssignMission.getId());
		}

		// 알림 읽음 처리
		Notification notification = findNotification(notificationId);
		notification.readNotification();

		NotificationResponse.NotificationInfo notificationInfo = NotificationResponse.NotificationInfo.builder()
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

	// 초대 거절
	@Transactional
	public NotificationResponse.NotificationInfo rejectInvite(Long groupId, Long notificationId, boolean acceptYn) {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);

		// 알림 읽음 처리
		Notification notification = findNotification(notificationId);
		notification.readNotification();

		NotificationResponse.NotificationInfo notificationInfo = NotificationResponse.NotificationInfo.builder()
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

	// 이미 가입한 그룹인지 체크
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
