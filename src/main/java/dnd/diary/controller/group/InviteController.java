package dnd.diary.controller.group;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.notification.NotificationResponse;
import dnd.diary.service.group.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("group/invite")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @GetMapping("/accept")
    public CustomResponseEntity<NotificationResponse.NotificationInfo> acceptInvite(
            @RequestParam Long groupId,
            @RequestParam Long notificationId
    ) {
        return CustomResponseEntity.success(inviteService.acceptInvite(groupId, notificationId));
    }

    @GetMapping("/reject")
    public CustomResponseEntity<NotificationResponse.NotificationInfo> rejectInvite(
            @RequestParam Long groupId,
            @RequestParam Long notificationId
    ) {
        return CustomResponseEntity.success(inviteService.rejectInvite(groupId, notificationId));
    }
}
