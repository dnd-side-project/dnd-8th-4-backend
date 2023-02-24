package dnd.diary.controller.mission;

import java.util.List;

import dnd.diary.dto.mission.MissionCheckLocationRequest;
import dnd.diary.dto.mission.MissionListByMapRequest;
import dnd.diary.response.mission.MissionCheckLocationResponse;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.*;

import dnd.diary.dto.group.MissionCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.MissionResponse;
import dnd.diary.service.group.MissionService;
import lombok.RequiredArgsConstructor;

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

	@PostMapping("/certification/location")
	public CustomResponseEntity<MissionCheckLocationResponse> checkLocation(@RequestBody MissionCheckLocationRequest request) {
		return CustomResponseEntity.success(missionService.checkLocation(request));
	}
}