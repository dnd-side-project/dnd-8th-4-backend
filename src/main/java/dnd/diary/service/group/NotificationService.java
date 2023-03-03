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
import dnd.diary.response.notification.ContentCommentNotificationResponse;
import dnd.diary.response.notification.ContentEmotionNotificationResponse;
import dnd.diary.response.notification.NotificationReadResponse;
import dnd.diary.response.notification.InviteNotificationResponse;
import dnd.diary.response.notification.NotificationResponse;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	private final UserService userService;

	public InviteNotificationResponse getInviteNotificationList() {
		User user = findUser();
		List<Notification> notificationList = user.getNotifications();
		log.info("유저 알림 개수 : {}", notificationList.size());

		long notificationCount = 0;
		List<InviteNotificationResponse.InviteNotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			log.info("notification ID : {}", notification.getId());
			log.info("invite ID : {}", notification.getInvite().getId());
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

		return InviteNotificationResponse.builder()
			.notificationInfoList(notificationInfoList)
			.totalCount(notificationCount)
			.build();
	}

	public ContentCommentNotificationResponse getContentCommentNotificationList(User user) {

		List<ContentCommentNotificationResponse.ContentCommentNotificationInfo> notificationInfoList = new ArrayList<>();
		List<Notification> notifications = user.getNotifications();
		for (Notification notification : notifications) {
			if (notification.getNotificationType() == NotificationType.CONTENT_COMMENT) {
				Content content = notification.getContent();
				Group group = content.getGroup();
				Comment comment = notification.getComment();
				ContentCommentNotificationResponse.ContentCommentNotificationInfo info = new ContentCommentNotificationResponse
					.ContentCommentNotificationInfo(notification, group, user, content, comment);

				notificationInfoList.add(info);
			}
		}
		return ContentCommentNotificationResponse.builder()
			.contentCommentNotificationInfoList(notificationInfoList)
			.count(notificationInfoList.size())
			.build();
	}

	public ContentEmotionNotificationResponse getContentEmotionNotificationList(User user) {

		List<ContentEmotionNotificationResponse.ContentEmotionNotificationInfo> notificationInfoList = new ArrayList<>();
		List<Notification> notifications = user.getNotifications();

		for (Notification notification : notifications) {
			if (notification.getNotificationType() == NotificationType.CONTENT_EMOTION) {
				Content content = notification.getContent();
				Group group = content.getGroup();
				Emotion emotion = notification.getEmotion();
				ContentEmotionNotificationResponse.ContentEmotionNotificationInfo info = new ContentEmotionNotificationResponse
					.ContentEmotionNotificationInfo(notification, group, user, content, emotion);

				notificationInfoList.add(info);
			}
		}

		return ContentEmotionNotificationResponse.builder()
			.contentEmotionNotificationInfoList(notificationInfoList)
			.count(notificationInfoList.size())
			.build();
	}

	public NotificationResponse getAllNotificationList() {
		User user = findUser();
		return NotificationResponse.builder()
			.inviteNotificationResponse(getInviteNotificationList())
			.contentCommentNotificationResponse(getContentCommentNotificationList(user))
			.contentEmotionNotificationResponse(getContentEmotionNotificationList(user))
			.build();
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