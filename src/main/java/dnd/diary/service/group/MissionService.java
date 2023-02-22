package dnd.diary.service.group;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.enumeration.Result.*;

import java.time.*;

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
import dnd.diary.repository.group.MissionRepository;
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
			LocalDateTime today = LocalDateTime.now();
			// 미션 시작일 > 오늘 -> 미션 진행중 상태
			if (today.isBefore(request.getMissionStartDate().atStartOfDay())) {
				missionStatus = MissionStatus.ACTIVE;
			}
			// 미션 종료일 < 오늘 -> 미션 종료 상태
			if (today.isAfter(request.getMissionEndDate().atStartOfDay().plusDays(1))) {
				missionStatus = MissionStatus.FINISH;
			}
		}
		
		Mission mission = Mission.toEntity(user, group, request.getMissionName(), request.getMissionNote()
			, request.getExistPeriod()
			, convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay(), ZoneId.of("Asia/Seoul"), ZoneOffset.UTC)
			, convertLocalDateTimeZone(request.getMissionEndDate().atTime(LocalTime.MAX), ZoneId.of("Asia/Seoul"), ZoneOffset.UTC)
			, request.getMissionLocationName(), request.getLatitude(), request.getLongitude()
			, request.getMissionColor(), missionStatus);
		
		missionRepository.save(mission);
		
		Period diff = Period.between(LocalDate.now(), request.getMissionEndDate());
		long missionDday = diff.getDays();

		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())
			.createUserId(user.getId())
			.groupId(group.getId())

			.existPeriod(request.getExistPeriod())
			.missionStartDate(convertLocalDateTimeZone(mission.getMissionStartDate(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.missionEndDate(convertLocalDateTimeZone(mission.getMissionEndDate(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))
			.missionStatus(missionStatus)

			.missionLocationName(mission.getMissionLocationName())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())

			.locationCheck(mission.getLocationCheck())
			.contentCheck(mission.getContentCheck())
			.isComplete(mission.getIsComplete())

			.missionDday(missionDday)
			.missionColor(mission.getMissionColor())
			.build();
	}
	
	// 미션 삭제
	@Transactional
	public void deleteMission(Long missionId) {
		User user = findUser();
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));

		if (!user.getId().equals(mission.getUser().getId())) {
			throw new CustomException(FAIL_DELETE_MISSION);
		}
		missionRepository.delete(mission);
	}
	
	// 미션 위치 인증
	
	
	// 미션 글쓰기 인증
	
	
	// 미션 완료 체크

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
	}
}
