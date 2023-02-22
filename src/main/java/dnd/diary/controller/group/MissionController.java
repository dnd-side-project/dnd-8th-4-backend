package dnd.diary.controller.group;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping
	public CustomResponseEntity<MissionResponse> createMission(@RequestBody MissionCreateRequest request) {
		return CustomResponseEntity.success(missionService.createMission(request));
	}
}
