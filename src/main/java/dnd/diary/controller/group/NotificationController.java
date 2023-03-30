package dnd.diary.controller.group;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.notification.AllNotificationListResponse;
import dnd.diary.response.notification.NotificationReadResponse;
import dnd.diary.response.notification.InviteNotificationResponse;
import dnd.diary.response.notification.UserNotificationInfoResponse;
import dnd.diary.service.group.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	// 초대 알림 목록 조회
	@GetMapping
	public CustomResponseEntity<InviteNotificationResponse> getInviteNotification() {
		return CustomResponseEntity.success(notificationService.getInviteNotification());
	}

	@GetMapping("/all")
	public CustomResponseEntity<AllNotificationListResponse> getAllNotificationList() {
		return CustomResponseEntity.success(notificationService.getAllNotificationList());
	}

	// 알림 단일 클릭(읽기)
	@GetMapping("/read")
	public CustomResponseEntity<NotificationReadResponse> readNotification(@RequestParam Long notificationId) {
		return CustomResponseEntity.success(notificationService.readNotification(notificationId));
	}

	// 새로 등록된 알림 표시 비활성화
	@GetMapping("/all/read")
	public CustomResponseEntity<UserNotificationInfoResponse> readAllNotificationList() {
		return CustomResponseEntity.success(notificationService.readAllNotificationList());
	}
}