package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.notification.*;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	private final UserService userService;

	public InviteNotificationResponse getInviteNotification() {
		User user = findUser();
		List<Notification> notificationList = user.getNotifications();
		
		long notificationCount = 0;
		List<InviteNotificationResponse.InviteNotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getInvite() == null) {
				continue;
			}
			if (notification.getNotificationType() == NotificationType.INVITE) {
				Group invitedGroup = notification.getInvite().getGroup();
				InviteNotificationResponse.InviteNotificationInfo notificationInfo = InviteNotificationResponse.InviteNotificationInfo
						.builder()
						.notificationType(NotificationType.INVITE)
						.notificationId(notification.getId())
						.groupId(invitedGroup.getId())
						.groupName(invitedGroup.getGroupName())
						.groupNote(invitedGroup.getGroupNote())
						.groupImageUrl(invitedGroup.getGroupImageUrl())
						.groupInvitedAt(notification.getCreatedAt())   // 초대된 날짜
						.readYn(notification.isReadYn())
						.build();
				notificationInfoList.add(notificationInfo);
				notificationCount += 1;
			}
		}

		return InviteNotificationResponse.builder()
			.notificationInfoList(notificationInfoList)
			.totalCount(notificationCount)
			.build();
	}

	public List<NotificationAllResponse.NotificationInfo> getInviteNotificationList(User user) {
		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<NotificationAllResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getInvite() == null) {
				continue;
			}
			if (notification.getNotificationType() == NotificationType.INVITE) {
				NotificationAllResponse.NotificationInfo notificationInfo = new NotificationAllResponse.NotificationInfo(notification);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("그룹 초대 알림 개수 : {}", notificationList.size());

		return notificationInfoList;
	}

	public List<NotificationAllResponse.NotificationInfo> getContentCommentNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<NotificationAllResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.CONTENT_COMMENT) {
				Content content = notification.getContent();
				Comment comment = notification.getComment();
				NotificationAllResponse.NotificationInfo notificationInfo = new NotificationAllResponse.NotificationInfo(notification, content, comment);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("게시물 댓글 알림 개수 : {}", notificationList.size());

		return notificationInfoList;
	}

	public List<NotificationAllResponse.NotificationInfo> getContentEmotionNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<NotificationAllResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.CONTENT_EMOTION) {
				Content content = notification.getContent();
				Emotion emotion = notification.getEmotion();
				NotificationAllResponse.NotificationInfo notificationInfo = new NotificationAllResponse.NotificationInfo(notification, content, emotion);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("게시물 공감 알림 개수 : {}", notificationList.size());

		return notificationInfoList;
	}

	public NotificationAllResponse getAllNotificationList() {
		User user = findUser();

		List<NotificationAllResponse.NotificationInfo> notificationInfoList = getInviteNotificationList(user);
		notificationInfoList.addAll(getContentCommentNotificationList(user));
		notificationInfoList.addAll(getContentEmotionNotificationList(user));

		// 알림 최신순 정렬
		notificationInfoList.sort(Comparator.comparing(NotificationAllResponse.NotificationInfo::getCreatedAt, Comparator.reverseOrder()));

		long notificationCount = notificationInfoList.size();
		NotificationAllResponse notificationAllResponse = NotificationAllResponse.builder()
				.notificationInfoList(notificationInfoList)
				.totalCount(notificationCount)
				.build();

		return notificationAllResponse;
	}

	public NotificationReadResponse readNotification(Long notificationId) {
		User user = findUser();
		Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTIFICATION));
		if (!notification.isReadYn()) {
			notification.readNotification();
		}
		Group group = notification.getInvite().getGroup();
		return NotificationReadResponse.builder()
			.notificationId(notification.getId())
			.notificationType(NotificationType.INVITE)
			.notificationInfo(
				NotificationReadResponse.NotificationInfo.builder()
					.groupId(group.getId())
					.groupName(group.getGroupName())
					.groupNote(group.getGroupName())
					.groupImageUrl(group.getGroupImageUrl())
					.readYn(notification.isReadYn())
					.build()
			)
			.build();
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}