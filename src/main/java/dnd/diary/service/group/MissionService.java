package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

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
		LocalDateTime today = LocalDateTime.now();
		// 미션 시작일 > 오늘 -> 미션 진행중 상태
		if (today.isBefore(request.getMissionStartDate().atStartOfDay())) {
			missionStatus = MissionStatus.ACTIVE;
		}
		// 미션 종료일 < 오늘 -> 미션 종료 상태
		if (today.isAfter(request.getMissionEndDate().atStartOfDay().plusDays(1))) {
			missionStatus = MissionStatus.FINISH;
		}
		
		Mission mission = Mission.toEntity(user, group, request.getMissionName(), request.getMissionNote()
			, request.getMissionStartDate().atStartOfDay(), request.getMissionEndDate().atTime(LocalTime.MAX), request.getMissionLocationName()
			, request.getLatitude(), request.getLongitude(), missionStatus);
		
		missionRepository.save(mission);
		
		Period diff = Period.between(LocalDate.now(), request.getMissionEndDate());
		long missionDday = diff.getDays();

		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())
			.createUserId(user.getId())
			.groupId(group.getId())
			.missionStartDate(mission.getMissionStartDate())
			.missionEndDate(mission.getMissionEndDate())
			.missionStatus(missionStatus)
			.missionLocationName(mission.getMissionLocationName())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())
			.locationCheck(mission.getLocationCheck())
			.contentCheck(mission.getContentCheck())
			.missionDday(missionDday)
			.build();
	}
	
	// 미션 삭제
	
	
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
