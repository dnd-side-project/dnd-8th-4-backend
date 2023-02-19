package dnd.diary.controller.group;

import dnd.diary.dto.request.group.GroupCreateRequest;
import dnd.diary.dto.request.group.GroupUpdateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.group.*;
import dnd.diary.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

	private final GroupValidator groupValidator;
	private final GroupService groupService;

	 @PostMapping("/create")
	 public CustomResponseEntity<GroupCreateResponse> createGroup(@RequestBody GroupCreateRequest request) {
		 groupValidator.checkGroupCreateAndUpdate(request.getGroupName(), request.getGroupNote());
	 	return CustomResponseEntity.success(groupService.createGroup(request));
	 }

	 @PatchMapping("/update")
	public CustomResponseEntity<GroupUpdateResponse> updateGroup(@RequestBody GroupUpdateRequest request) {
		 groupValidator.checkGroupCreateAndUpdate(request.getGroupName(), request.getGroupNote());
		 return CustomResponseEntity.success(groupService.updateGroup(request));
	 }

	 @DeleteMapping("/delete")
	public CustomResponseEntity<Void> deleteGroup(@RequestBody GroupUpdateRequest request) {
		 groupService.deleteGroup(request.getGroupId());
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
//	@PostMapping("/search")
//	public CustomResponseEntity<List<GroupListResponse>> searchGroupList(
//		@RequestBody GroupSearchRequest request
//	) {
//		 return CustomResponseEntity.success(groupService.searchGroupList(request));
//	}

	// 그룹 메인 - 즐겨찾기 그룹 조회
	@GetMapping("/list/star")
	public CustomResponseEntity<List<GroupStarListResponse>> getGroupStarList() {
		 return CustomResponseEntity.success(groupService.getGroupStarList());
	}

}
