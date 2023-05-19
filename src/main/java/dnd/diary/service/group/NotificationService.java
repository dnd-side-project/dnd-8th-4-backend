package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import dnd.diary.request.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.notification.*;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				Group invitedGroup = notification.getInvite().getGroup();   // 이미 삭제된 그룹 알림도 포함
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

	// 전체 알림 목록 조회
	public AllNotificationListResponse getAllNotificationList() {
		User user = findUser();

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = getContentCommentNotificationList(user);
		notificationInfoList.addAll(getContentEmotionNotificationList(user));
		notificationInfoList.addAll(getNewGroupMemberNotificationList(user));
		notificationInfoList.addAll(getCommentLikeNotificationList(user));

		// 알림 최신순 정렬
		notificationInfoList.sort(Comparator.comparing(AllNotificationListResponse.NotificationInfo::getCreatedAt, Comparator.reverseOrder()));

		long notificationCount = notificationInfoList.size();
		AllNotificationListResponse allNotificationListResponse = AllNotificationListResponse.builder()
				.notificationInfoList(notificationInfoList)
				.totalCount(notificationCount)
				.build();

		return allNotificationListResponse;
	}

	// 초대 알림 목록
	private List<AllNotificationListResponse.NotificationInfo> getInviteNotificationList(User user) {
		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.INVITE) {
				if (notification.getInvite() == null) {   // 초대 정보가 없는 알림인 경우
					continue;
				}
				AllNotificationListResponse.NotificationInfo notificationInfo = new AllNotificationListResponse.NotificationInfo(notification);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("그룹 초대 알림 개수 : {}", notificationInfoList.size());

		return notificationInfoList;
	}

	// 게시물 댓글 알림 목록
	// 이미 삭제된 게시물일 경우에도 포함
	private List<AllNotificationListResponse.NotificationInfo> getContentCommentNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.CONTENT_COMMENT) {
			    // 알림의 게시물/댓글 정보가 없는 경우 제외
				if (notification.getContent() == null || notification.getComment() == null) {
					continue;
				}
				Content content = notification.getContent();
				Comment comment = notification.getComment();
				AllNotificationListResponse.NotificationInfo notificationInfo = new AllNotificationListResponse.NotificationInfo(notification, content, comment);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("게시물 댓글 알림 개수 : {}", notificationInfoList.size());

		return notificationInfoList;
	}

	// 게시물 공감 알림 목록
	// 이미 삭제된 게시물일 경우에도 포함
	private List<AllNotificationListResponse.NotificationInfo> getContentEmotionNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.CONTENT_EMOTION) {
                // 알림의 게시물/공감 정보가 없는 경우 제외
				if (notification.getContent() == null || notification.getEmotion() == null) {
					continue;
				}
				Content content = notification.getContent();
				Emotion emotion = notification.getEmotion();
				AllNotificationListResponse.NotificationInfo notificationInfo = new AllNotificationListResponse.NotificationInfo(notification, content, emotion);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("게시물 공감 알림 개수 : {}", notificationInfoList.size());

		return notificationInfoList;
	}

	// 댓글 좋아요 알림 목록
	// 이미 삭제된 댓글일 경우에도 포함
	private List<AllNotificationListResponse.NotificationInfo> getCommentLikeNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {}", notificationList.size());

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.COMMENT_LIKE) {
				// 알림의 댓글/좋아요 정보가 없는 경우 제외
				if (notification.getComment() == null || notification.getCommentLike() == null) {
					continue;
				}
				Comment comment = notification.getComment();
				CommentLike commentLike = notification.getCommentLike();
				AllNotificationListResponse.NotificationInfo notificationInfo = new AllNotificationListResponse.NotificationInfo(notification, comment, commentLike);
				notificationInfoList.add(notificationInfo);
			}
		}
		log.info("댓글 좋아요 알림 개수 : {}", notificationInfoList.size());

		return notificationInfoList;
	}

	// 그룹 새 멤버 알림 목록
	private List<AllNotificationListResponse.NotificationInfo> getNewGroupMemberNotificationList(User user) {

		List<Notification> notificationList = user.getNotifications();
		log.info("유저 전체 알림 개수 : {} : ", notificationList.size());

		List<AllNotificationListResponse.NotificationInfo> notificationInfoList = new ArrayList<>();
		for (Notification notification : notificationList) {
			if (notification.getNotificationType() == NotificationType.NEW_GROUP_MEMBER) {
				if (notification.getGroup() == null || notification.getNewGroupUser() == null || notification.getContent() == null) {
					continue;
				}
				// 이미 삭제된 그룹인 경우에도 포함
				Group group = notification.getGroup();
				User newGroupUser = notification.getNewGroupUser();
				Content newGroupMemberContent = notification.getContent();
				AllNotificationListResponse.NotificationInfo notificationInfo = new AllNotificationListResponse.NotificationInfo(group, newGroupUser, newGroupMemberContent, notification);
				notificationInfoList.add(notificationInfo);
			}
		}

		log.info("그룹 새 멤버 알림 개수 : {}", notificationInfoList.size());

		return notificationInfoList;
	}

	// 알림 단일 클릭(읽기)
	@Transactional
	public NotificationReadResponse readNotification(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTIFICATION));
		if (!notification.isReadYn()) {
			notification.updateReadNotification();
		}

		return NotificationReadResponse.builder()
			.notificationId(notification.getId())
			.notificationType(notification.getNotificationType())
            .readYn(notification.isReadYn())
			.build();
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}