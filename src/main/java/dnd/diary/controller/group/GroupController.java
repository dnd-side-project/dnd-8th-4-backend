package dnd.diary.controller.group;

import dnd.diary.request.service.group.GroupInviteRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.group.*;
import dnd.diary.response.group.GroupInviteResponse;
import dnd.diary.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupValidator groupValidator;
    private final GroupService groupService;

    @PostMapping("/create")
    public CustomResponseEntity<GroupCreateResponse> createGroup(
            @AuthenticationPrincipal Long userId,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestParam String groupName,
            @RequestParam(required = false) String groupNote
    ) {
        groupValidator.checkGroupCreateAndUpdate(userId, groupName, groupNote);
        return CustomResponseEntity.success(groupService.createGroup(userId, multipartFile, groupName, groupNote));
    }

    @PatchMapping("/update")
    public CustomResponseEntity<GroupUpdateResponse> updateGroup(
            @AuthenticationPrincipal Long userId,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestParam Long groupId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String groupNote
    ) {
        groupValidator.checkGroupCreateAndUpdate(userId, groupName, groupNote);
        return CustomResponseEntity.success(groupService.updateGroup(userId, multipartFile, groupId, groupName, groupNote));
    }

    @DeleteMapping("/delete")
    public CustomResponseEntity<Void> deleteGroup(
            @RequestParam Long groupId,
            @AuthenticationPrincipal Long userId
    ) {
        groupService.deleteGroup(groupId,userId);
        return CustomResponseEntity.success();
    }

    // 그룹 즐겨찾기 등록
    @GetMapping("/star")
    public CustomResponseEntity<GroupStarResponse> starGroup(
            @RequestParam Long groupId,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.starGroup(groupId, userId));
    }

    // 그룹 메인 - 내가 속한 그룹 목록 조회
    @GetMapping("/list")
    public CustomResponseEntity<GroupListResponse> getGroupList(
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.getGroupList(userId));
    }

    @GetMapping("/list/my")
    public CustomResponseEntity<List<GroupSimpleResponse>> getGroupSimpleList(
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.getGroupSimpleList(userId));
    }

    // 내가 속한 그룹 목록 내 검색
    @GetMapping("/search")
    public CustomResponseEntity<GroupListResponse> searchGroupList(
            @RequestParam String keyword, @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.searchGroupList(keyword,userId));
    }

    // 그룹 메인 - 즐겨찾기 그룹 조회
    @GetMapping("/list/star")
    public CustomResponseEntity<List<GroupStarListResponse>> getGroupStarList(@AuthenticationPrincipal Long userId) {
        return CustomResponseEntity.success(groupService.getGroupStarList(userId));
    }

    // 그룹 초대
    @PostMapping("/invite")
    public CustomResponseEntity<GroupInviteResponse> inviteGroupMember(
            @RequestBody GroupInviteRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.inviteGroupMember(request, userId));
    }

    @GetMapping
    public CustomResponseEntity<GroupDetailResponse> getGroupDetail(
            @RequestParam Long groupId,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(groupService.getGroupDetail(groupId, userId));
    }

}
