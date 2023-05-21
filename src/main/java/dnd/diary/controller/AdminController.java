package dnd.diary.controller;

import java.util.List;

import dnd.diary.response.mission.StickerPerResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dnd.diary.request.service.mission.StickerGroupCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.StickerGroupResponse;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.AdminService;
import dnd.diary.service.mission.StickerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final StickerService stickerService;
	private final AdminService adminService;

	// 사용자 기본 프로필 등록 - 여러 개 한 번에 등록 가능
	@PostMapping("/user/profile")
	public CustomResponseEntity<Void> createUserProfileImage(
		@RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles
	) {
		try {
			adminService.createUserProfileImage(multipartFiles);
			return CustomResponseEntity.success();
		} catch (Exception e) {
			return CustomResponseEntity.fail();
		}
	}

	// 그룹 기본 이미지 등록 - 여러 개 한 번에 등록 가능
	@PostMapping("/group/profile")
	public CustomResponseEntity<Void> createGroupProfileImage(
		@RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles
		) {
		try {
			adminService.createGroupProfileImage(multipartFiles);
			return CustomResponseEntity.success();
		} catch (Exception e) {
			return CustomResponseEntity.fail();
		}
	}

	// [관리자] 스티커 그룹 썸네일 등록 - 한 번에 한 개 등록 가능
	@PostMapping("/sticker/group")
	public CustomResponseEntity<StickerGroupResponse> createStickerThumbnail(
		@RequestPart(value = "image", required = false) MultipartFile multipartFile,
		@RequestPart(value = "request") StickerGroupCreateRequest request
	) {
		return CustomResponseEntity.success(adminService.createStickerGroup(request, multipartFile));
	}

	// [관리자] 획득 가능한 스티커 그룹 목록 조회
	@GetMapping("/sticker/group/list")
	public CustomResponseEntity<List<StickerGroupResponse>> getStickerGroupList() {
		return CustomResponseEntity.success(adminService.getSickerGroupList());
	}

	// [관리자] 스티커 그룹 별 개별 스티커 등록
	@PostMapping("/sticker")
	public CustomResponseEntity<StickerResponse> createSticker(
			@RequestParam Long stickerGroupId,
			@RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles
	) {
		return CustomResponseEntity.success(adminService.createSticker(stickerGroupId, multipartFiles));
	}

	@PostMapping("/sticker/one")
	public CustomResponseEntity<StickerPerResponse> createSticker(
			@RequestParam Long stickerGroupId,
			@RequestPart(value = "image", required = false) MultipartFile multipartFile,
			@RequestParam(required = false) boolean mainStickerYn
	) {
		return CustomResponseEntity.success(adminService.createStickerOne(stickerGroupId, multipartFile, mainStickerYn));
	}

	// [관리자] 획득 가능한 스티커 그룹 별 전체 스티커 목록 조회
	@GetMapping("/sticker/list")
	public CustomResponseEntity<List<StickerResponse>> getStickerList() {
		return CustomResponseEntity.success(adminService.getStickerList());
	}
}
