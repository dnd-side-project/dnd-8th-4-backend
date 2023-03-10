package dnd.diary.controller.mission;

import java.util.List;

import dnd.diary.dto.mission.MissionCheckContentRequest;
import dnd.diary.dto.mission.MissionCheckLocationRequest;
import dnd.diary.dto.mission.MissionListByMapRequest;
import dnd.diary.response.mission.MissionCheckContentResponse;
import dnd.diary.response.mission.MissionCheckLocationResponse;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import dnd.diary.dto.group.MissionCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.MissionResponse;
import dnd.diary.service.mission.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mission")
@RequiredArgsConstructor
public class MissionController {

	private final MissionService missionService;
	private final MissionValidator missionValidator;

	@PostMapping
	public CustomResponseEntity<MissionResponse> createMission(@RequestBody MissionCreateRequest request)
			throws ParseException {
		missionValidator.checkCreateMission(request);
		return CustomResponseEntity.success(missionService.createMission(request));
	}

	@DeleteMapping
	public CustomResponseEntity<Void> deleteMission(@RequestParam Long missionId) {
		missionService.deleteMission(missionId);
		return CustomResponseEntity.success();
	}

	@GetMapping("/list")
	public CustomResponseEntity<List<MissionResponse>> getMissionList(@RequestParam int missionStatus) {
		return CustomResponseEntity.success(missionService.getMissionList(missionStatus));
	}

	// 그룹 메인 진입 페이지 내 [시작 전/진행 중] 미션 목록 조회
	@GetMapping("/list/group")
	public CustomResponseEntity<List<MissionResponse>> getReadyAndActiveGroupMissionList(@RequestParam int groupId) {
		return CustomResponseEntity.success(missionService.getReadyAndActiveGroupMissionList(groupId));
	}

	@GetMapping("/list/main")
	public CustomResponseEntity<List<MissionResponse>> getReadyAndActiveMissionList() {
		return CustomResponseEntity.success(missionService.getReadyAndActiveMissionList());
	}

	@GetMapping
	public CustomResponseEntity<MissionResponse> getMission(@RequestParam Long missionId) {
		return CustomResponseEntity.success(missionService.getMission(missionId));
	}

	@PostMapping("/list/map")
	public CustomResponseEntity<List<MissionResponse>> getMissionListByMap(
			@RequestBody MissionListByMapRequest request
	) {
		return CustomResponseEntity.success(missionService.getMissionListByMap(request));
	}

	@GetMapping("/map")
	public CustomResponseEntity<MissionResponse> getMissionByMap(@RequestParam Long missionId) {
		return CustomResponseEntity.success(missionService.getMission(missionId));
	}

	@PostMapping("/check/location")
	public CustomResponseEntity<MissionCheckLocationResponse> checkMissionLocation(
			@RequestBody MissionCheckLocationRequest request) {
		return CustomResponseEntity.success(missionService.checkMissionLocation(request));
	}

	@PostMapping("/check/content")
	public CustomResponseEntity<MissionCheckContentResponse> checkMissionContent(
			@AuthenticationPrincipal final UserDetails user,
			@RequestPart(required = false) final List<MultipartFile> multipartFiles,
			@RequestParam final Long missionId,
			@RequestParam final String content
	) throws ParseException {
		return CustomResponseEntity.success(missionService.checkMissionContent(user, multipartFiles, missionId, content));
	}
}