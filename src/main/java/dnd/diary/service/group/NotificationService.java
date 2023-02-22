package dnd.diary.service.group;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.user.User;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.NotificationType;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.notification.NotificationReadResponse;
import dnd.diary.response.notification.NotificationResponse;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static dnd.diary.enumeration.Result.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	private final UserService userService;

	public NotificationResponse getNotificationList() {
		User user = findUser();
		List<Notification> notificationList = user.getNotifications();
		log.info("유저 알림 개수 : {}", notificationList.size());

		long notificationCount = 0;
		List<NotificationResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			log.info("notification ID : {}", notification.getId());
			log.info("invite ID : {}", notification.getInvite().getId());
			Group invitedGroup = notification.getInvite().getGroup();
			NotificationResponse.NotificationInfo notificationInfo = NotificationResponse.NotificationInfo
				.builder()
				.groupId(invitedGroup.getId())
				.groupName(invitedGroup.getGroupName())
				.readYn(notification.isReadYn())
				.build();
			notificationInfoList.add(notificationInfo);
			notificationCount += 1;
		}

		return NotificationResponse.builder()
			.notificationType(NotificationType.INVITE)
			.notificationInfoList(notificationInfoList)
			.totalCount(notificationCount)
			.build();
	}

	public NotificationReadResponse readNotification(Long notificationId) {
		User user = findUser();

		return null;
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}
