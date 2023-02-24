package dnd.diary.service.group;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.enumeration.Result.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.mission.MissionCheckLocationRequest;
import dnd.diary.dto.mission.MissionListByMapRequest;
import dnd.diary.response.mission.MissionCheckLocationResponse;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
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

	private final int MISSION_DISTANCE_LIMIT = 50;

	// 미션 생성
	@Transactional
	public MissionResponse createMission(MissionCreateRequest request) throws ParseException {
		User user = findUser();
		Group group = findGroup(request.getGroupId());

		MissionStatus missionStatus = MissionStatus.READY;
		// 미션 기간을 설정하지 않은 경우 - 항상 ACTIVE
		if (!request.getExistPeriod()) {
			missionStatus = MissionStatus.ACTIVE;
		} else {
			LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
			// 미션 시작일 < 오늘 -> 미션 진행중 상태
			if (today.isAfter(convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay().minusHours(9), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.ACTIVE;
			}
			// 미션 종료일 < 오늘 -> 미션 종료 상태
			if (today.isAfter(convertLocalDateTimeZone(request.getMissionEndDate().atStartOfDay().plusDays(1).minusHours(9), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.FINISH;
			}
		}

		// 서버 기준 시간 + 9 -> 00:00 / 23:59 로
		String pointWKT = String.format("POINT(%s %s)", request.getLatitude(), request.getLongitude());
		Point point = (Point) new WKTReader().read(pointWKT);
		Mission mission = Mission.toEntity(user, group, request.getMissionName(), request.getMissionNote()
			, request.getExistPeriod()
			, convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
			, convertLocalDateTimeZone(request.getMissionEndDate().atTime(23, 59, 59), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
			, request.getMissionLocationName(), request.getLatitude(), request.getLongitude()
			, request.getMissionColor(), missionStatus, point);
		
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
			.groupName(group.getGroupName())
			.groupImageUrl(group.getGroupImageUrl())

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

		// 미션 참여자(그룹 구성원) 에게 할당된 미션 삭제 처리
		List<UserAssignMission> userAssignMissionList = mission.getUserAssignMissions();
		userAssignMissionRepository.deleteAll(userAssignMissionList);

		// 미션 삭제 처리
		missionRepository.delete(mission);
	}
	
	// 미션 위치 인증
	public MissionCheckLocationResponse checkLocation(MissionCheckLocationRequest request) {

		// 유저가 가진 미션이 맞는지 확인
		User user = findUser();
		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();
		List<Long> userAssignMissionIdList = new ArrayList<>();
		List<Long> userAssignMissionGroupIdList = new ArrayList<>();
		userAssignMissionList.forEach(
				userAssignMission -> {
					log.info("유저가 가진 미션 ID : {}", userAssignMission.getMission().getId());
					userAssignMissionIdList.add(userAssignMission.getMission().getId());
					log.info("유저가 가진 미션이 속한 그룹 ID : {}", userAssignMission.getMission().getGroup().getId());
					userAssignMissionGroupIdList.add(userAssignMission.getMission().getGroup().getId());
				}
		);
		// 해당 그룹의 미션이 맞는지 확인
		if (!userAssignMissionGroupIdList.contains(request.getGroupId())) {
			throw new CustomException(INVALID_GROUP_MISSION);
		}
		// 유저가 가진 미션이 맞는지 확인
		if (!userAssignMissionIdList.contains(request.getMissionId())) {
			throw new CustomException(INVALID_USER_MISSION);
		}
		// 미션 진행 기간인지 확인
		Mission targetMission = missionRepository.findById(request.getMissionId()).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));
		if (targetMission.getMissionStatus() != MissionStatus.ACTIVE) {
			throw new CustomException(INVALID_MISSION_PERIOD);
		}

		// 미션 위치 기준 현재 자신의 위치가 반경 50m 이내에 있는지 체크
		boolean checkLocationMission = false;
		Double checkDistance = distance(request.getCurrLatitude(), request.getCurrLongitude()
				, targetMission.getLatitude(), targetMission.getLongitude());
		log.info("미션 인증 위치와 현재 위치 간 거리 : {}", checkDistance);

		UserAssignMission checkUserAssignMission = null;
		if (checkDistance.intValue() <= MISSION_DISTANCE_LIMIT) {

			checkLocationMission = true;

			for (UserAssignMission userAssignMission : targetMission.getUserAssignMissions()) {
				if (user.getId().equals(userAssignMission.getUser().getId())) {
					checkUserAssignMission = userAssignMission;
					log.info("위치 인증 상태 업데이트 전 : {}", checkUserAssignMission.getLocationCheck());
					// 유저에게 할당된 미션의 위치 인증 상태 업데이트
					checkUserAssignMission.completeLocationCheck();
					log.info("위치 인증 상태 업데이트 후 : {}", checkUserAssignMission.getLocationCheck());
					break;
				}
			}
		}
		return MissionCheckLocationResponse.builder()
				.missionId(targetMission.getId())
				.locationCheck(checkLocationMission)
				.contentCheck(checkUserAssignMission.getContentCheck())
				.isComplete(checkUserAssignMission.getIsComplete())
				.build();
	}

	private static double distance(double lat1, double lon1, double lat2, double lon2){
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1))* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60*1.1515*1609.344;

		return dist; // 단위 m
	}

	// 10진수를 radian 으로 변환
	private static double deg2rad(double deg){
		return (deg * Math.PI/180.0);
	}
	// radian 을 10진수로 변환
	private static double rad2deg(double rad){
		return (rad * 180 / Math.PI);
	}


	// 미션 상태별 목록 조회 (0 : 전체, 1 : 시작 전, 2 : 진행중, 3 : 종료)
	public List<MissionResponse> getMissionList(int missionStatus) {

		User user = findUser();
		MissionStatus findMissionStatus = MissionStatus.getName(missionStatus);
		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();
		List<MissionResponse> missionResponseList = new ArrayList<>();

		for (UserAssignMission userAssignMission : userAssignMissionList) {
			Mission mission = userAssignMission.getMission();
			log.info("userAssignMission ID : {} 인 미션의 상태 : {}", userAssignMission.getId(), mission.getMissionStatus());
			log.info("미션 시작일 : {} , 미션 종료일 : {}", mission.getMissionStartDate(), mission.getMissionEndDate());
			if (MissionStatus.ALL == findMissionStatus) {
				MissionResponse missionResponse = toMissionResponse(mission);
				missionResponseList.add(missionResponse);
			} else {
				if (findMissionStatus == mission.getMissionStatus()) {
					MissionResponse missionResponse = toMissionResponse(mission);
					missionResponseList.add(missionResponse);
				}
			}
		}
		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		return missionResponseList;
	}

	public MissionResponse getMission(Long missionId) {
		User user = findUser();
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));
		return toMissionResponse(mission);
	}

	private MissionResponse toMissionResponse(Mission mission) {
		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())
			.createUserId(mission.getMissionCreateUser().getId())
			.groupId(mission.getGroup().getId())
			.groupName(mission.getGroup().getGroupName())
			.groupImageUrl(mission.getGroup().getGroupImageUrl())

			.existPeriod(mission.getExistPeriod())
			.missionStartDate(mission.getMissionStartDate())
			.missionEndDate(mission.getMissionEndDate())
			.missionStatus(mission.getMissionStatus())

			.missionLocationName(mission.getMissionLocationName())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())

			.missionDday(
				Period.between(LocalDate.now(ZoneId.of("Asia/Seoul")), mission.getMissionEndDate().toLocalDate()).getDays()
			)
			.missionColor(mission.getMissionColor())
			.build();
	}

	public List<MissionResponse> getMissionListByMap(MissionListByMapRequest missionListByMapRequest) {

		User user = findUser();
		List<Mission> userMissionList = new ArrayList<>();
		user.getUserAssignMissions().forEach(
				userAssignMission -> userMissionList.add(userAssignMission.getMission())
		);
		List<MissionResponse> missionResponseList = new ArrayList<>();
		MissionListByMapRequest request = missionListByMapRequest.setStartXY();
		List<Mission> userMissionListWithInMap = missionRepository.findWithinMap(
				request.getStartLatitude(), request.getEndLatitude(), request.getStartLongitude(), request.getEndLongitude()
		);

		for (Mission mission : userMissionListWithInMap) {
			if (mission.getMissionStatus() == MissionStatus.ACTIVE) {
				missionResponseList.add(toMissionResponse(mission));
			}
		}
		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		return missionResponseList;
	}

	// 완료한 미션 목록 조회 -> 스티커 쪽
	public List<MissionResponse> getCompleteMissionList() {
		List<MissionResponse> missionResponseList = new ArrayList<>();
		return missionResponseList;
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
