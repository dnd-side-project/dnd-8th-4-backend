package dnd.diary.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dnd.diary.dto.mission.StickerCreateRequest;
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
		@RequestPart StickerCreateRequest request
	) {
		return CustomResponseEntity.success(adminService.createStickerGroup(request, multipartFile));
	}

	// [관리자] 획득 가능한 스티커 그룹 목록 조회
	@GetMapping("/sticker/group/list")
	public CustomResponseEntity<List<StickerGroupResponse>> getStickerList() {
		return CustomResponseEntity.success(adminService.getSickerGroupList());
	}

	// TODO [관리자] 스티커 그룹 별 개별 스티커 등록

	// TODO [관리자] 획득 가능한 스티커 그룹 별 전체 스티커 목록 조회
}
