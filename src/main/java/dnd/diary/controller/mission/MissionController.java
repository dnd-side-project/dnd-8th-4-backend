package dnd.diary.controller.mission;

import java.util.List;

import dnd.diary.request.mission.MissionCheckLocationRequest;
import dnd.diary.request.mission.MissionListByMapRequest;
import dnd.diary.response.mission.MissionCheckContentResponse;
import dnd.diary.response.mission.MissionCheckLocationResponse;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import dnd.diary.request.group.MissionCreateRequest;
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
    public CustomResponseEntity<MissionResponse> createMission(
            @RequestBody MissionCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) throws ParseException {
        missionValidator.checkCreateMission(request);
        return CustomResponseEntity.success(missionService.createMission(request, userId));
    }

    @DeleteMapping
    public CustomResponseEntity<Void> deleteMission(
            @RequestParam Long missionId,
            @AuthenticationPrincipal Long userId
    ) {
        missionService.deleteMission(missionId, userId);
        return CustomResponseEntity.success();
    }

    @GetMapping("/list")
    public CustomResponseEntity<List<MissionResponse>> getMissionList(
            @RequestParam int missionStatus,
            @AuthenticationPrincipal final Long userId
    ) {
        return CustomResponseEntity.success(missionService.getMissionList(userId, missionStatus));
    }

    // 그룹 메인 진입 페이지 내 [시작 전/진행 중] 미션 목록 조회
    @GetMapping("/list/group")
    public CustomResponseEntity<List<MissionResponse>> getReadyAndActiveGroupMissionList(
            @RequestParam int groupId,
            @AuthenticationPrincipal final Long userId
    ) {
        return CustomResponseEntity.success(missionService.getReadyAndActiveGroupMissionList(userId, groupId));
    }

    @GetMapping("/list/main")
    public CustomResponseEntity<List<MissionResponse>> getReadyAndActiveMissionList(
            @AuthenticationPrincipal final Long userId
    ) {
        return CustomResponseEntity.success(missionService.getReadyAndActiveMissionList(userId));
    }

    @GetMapping
    public CustomResponseEntity<MissionResponse> getMission(
            @RequestParam Long missionId,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(missionService.getMission(userId, missionId));
    }

    @PostMapping("/list/map")
    public CustomResponseEntity<List<MissionResponse>> getMissionListByMap(
            @RequestBody MissionListByMapRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(missionService.getMissionListByMap(request, userId));
    }

    @GetMapping("/map")
    public CustomResponseEntity<MissionResponse> getMissionByMap(@RequestParam Long missionId) {
        return CustomResponseEntity.success(missionService.getMission(missionId, missionId));
    }

    @PostMapping("/check/location")
    public CustomResponseEntity<MissionCheckLocationResponse> checkMissionLocation(
            @RequestBody MissionCheckLocationRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(missionService.checkMissionLocation(userId, request));
    }

    @PostMapping("/check/content")
    public CustomResponseEntity<MissionCheckContentResponse> checkMissionContent(
            @AuthenticationPrincipal final Long userId,
            @RequestPart(required = false) final List<MultipartFile> multipartFiles,
            @RequestParam final Long missionId,
            @RequestParam final String content
    ) throws ParseException {
        return CustomResponseEntity.success(missionService.checkMissionContent(userId, multipartFiles, missionId, content));
    }

    // 완료한 미션 조회
    @GetMapping("auth/my/mission/complete")
    public CustomResponseEntity<List<MissionResponse>> getCompleteMissionList(@AuthenticationPrincipal Long userId) {
        return CustomResponseEntity.success(missionService.getCompleteMissionList(userId));
    }
}