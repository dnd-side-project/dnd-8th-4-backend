package dnd.diary.service.group;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.enumeration.Result.*;

import java.time.*;
import java.util.List;

import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.mission.MissionCheckLocationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.mission.MissionStatus;
import dnd.diary.domain.user.User;
import dnd.diary.dto.group.MissionCreateRequest;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.mission.MissionRepository;
import dnd.diary.repository.mission.UserAssignMissionRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.mission.MissionResponse;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

	private final MissionRepository missionRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final UserAssignMissionRepository userAssignMissionRepository;

	private final UserService userService;

	// 미션 생성
	@Transactional
	public MissionResponse createMission(MissionCreateRequest request) {
		User user = findUser();
		Group group = findGroup(request.getGroupId());

		MissionStatus missionStatus = MissionStatus.READY;
		// 미션 기간을 설정하지 않은 경우 - 항상 ACTIVE
		if (!request.getExistPeriod()) {
			missionStatus = MissionStatus.ACTIVE;
		} else {
			LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
			// 미션 시작일 > 오늘 -> 미션 진행중 상태
			if (today.isBefore(convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.ACTIVE;
			}
			// 미션 종료일 < 오늘 -> 미션 종료 상태
			if (today.isAfter(convertLocalDateTimeZone(request.getMissionEndDate().atStartOfDay().plusDays(1), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.FINISH;
			}
		}

		// 서버 기준 시간 + 9 -> 00:00 / 23:59 로
		Mission mission = Mission.toEntity(user, group, request.getMissionName(), request.getMissionNote()
			, request.getExistPeriod()
			, convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
			, convertLocalDateTimeZone(request.getMissionEndDate().atTime(LocalTime.MAX), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
			, request.getMissionLocationName(), request.getLatitude(), request.getLongitude()
			, request.getMissionColor(), missionStatus);
		
		missionRepository.save(mission);

		// 그룹에 속한 구성원 모두에게 미션 할당
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			User groupUser = userJoinGroup.getUser();
			UserAssignMission userAssignMission = UserAssignMission.toEntity(groupUser, mission);
			userAssignMissionRepository.save(userAssignMission);
		}
		
		Period diff = Period.between(LocalDate.now(), request.getMissionEndDate());
		long missionDday = diff.getDays();

		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())
			.createUserId(user.getId())
			.groupId(group.getId())

			.existPeriod(request.getExistPeriod())
			.missionStartDate(mission.getMissionStartDate())
			.missionEndDate(mission.getMissionEndDate())
			.missionStatus(missionStatus)

			.missionLocationName(mission.getMissionLocationName())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())

			.missionDday(missionDday)
			.missionColor(mission.getMissionColor())
			.build();
	}
	
	// 미션 삭제
	@Transactional
	public void deleteMission(Long missionId) {
		User user = findUser();
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));

		if (!user.getId().equals(mission.getMissionCreateUser().getId())) {
			throw new CustomException(FAIL_DELETE_MISSION);
		}
		missionRepository.delete(mission);
	}
	
	// 미션 위치 인증
	public MissionResponse checkLocation(MissionCheckLocationRequest request) {
		return MissionResponse.builder().build();
	}
	
	// 미션 글쓰기 인증 -> Content 생성 시 체크
	
	
	// 미션 완료 체크


	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
	}
}
