package dnd.diary.controller.group;

import dnd.diary.dto.group.GroupCreateRequest;
import dnd.diary.dto.group.GroupInviteRequest;
import dnd.diary.dto.group.GroupUpdateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.group.*;
import dnd.diary.response.group.GroupInviteResponse;
import dnd.diary.service.group.GroupService;
import lombok.RequiredArgsConstructor;
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
	 		@RequestPart(value = "image", required = false) MultipartFile multipartFile,
			@RequestPart GroupCreateRequest request
	 ) {
		 groupValidator.checkGroupCreateAndUpdate(request.getGroupName(), request.getGroupNote());
	 	return CustomResponseEntity.success(groupService.createGroup(multipartFile, request));
	 }

	 @PatchMapping("/update")
	public CustomResponseEntity<GroupUpdateResponse> updateGroup(
			@RequestPart(value = "image", required = false) MultipartFile multipartFile,
			@RequestPart GroupUpdateRequest request
	 ) {
		 groupValidator.checkGroupCreateAndUpdate(request.getGroupName(), request.getGroupNote());
		 return CustomResponseEntity.success(groupService.updateGroup(multipartFile, request));
	 }

	 @DeleteMapping("/delete")
	public CustomResponseEntity<Void> deleteGroup(@RequestParam Long groupId) {
		 groupService.deleteGroup(groupId);
		 return CustomResponseEntity.success();
	 }

	 // 그룹 즐겨찾기 등록
	@GetMapping("/star")
	public CustomResponseEntity<GroupStarResponse> starGroup(@RequestParam Long groupId) {
		 return CustomResponseEntity.success(groupService.starGroup(groupId));
	}

	// 그룹 메인 - 내가 속한 그룹 목록 조회
	@GetMapping("/list")
	public CustomResponseEntity<GroupListResponse> getGroupList() {
		 return CustomResponseEntity.success(groupService.getGroupList());
	}

	// 내가 속한 그룹 목록 내 검색
	@GetMapping("/search")
	public CustomResponseEntity<GroupListResponse> searchGroupList(
		@RequestParam String keyword
	) {
		 return CustomResponseEntity.success(groupService.searchGroupList(keyword));
	}

	// 그룹 메인 - 즐겨찾기 그룹 조회
	@GetMapping("/list/star")
	public CustomResponseEntity<List<GroupStarListResponse>> getGroupStarList() {
		 return CustomResponseEntity.success(groupService.getGroupStarList());
	}

	// 그룹 초대
	@PostMapping("/invite")
	public CustomResponseEntity<GroupInviteResponse> inviteGroupMember(@RequestBody GroupInviteRequest request) {
	 	return CustomResponseEntity.success(groupService.inviteGroupMember(request));
	}

}
