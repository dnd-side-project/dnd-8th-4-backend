package dnd.diary.service.mission;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.domain.sticker.StickerLevel.getSticker;
import static dnd.diary.enumeration.Result.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.dto.mission.MissionCheckContentRequest;
import dnd.diary.dto.mission.MissionCheckLocationRequest;
import dnd.diary.dto.mission.MissionListByMapRequest;
import dnd.diary.enumeration.Result;
import dnd.diary.response.mission.MissionCheckContentResponse;
import dnd.diary.response.mission.MissionCheckLocationResponse;
import dnd.diary.service.content.ContentService;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

	private final MissionRepository missionRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final UserAssignMissionRepository userAssignMissionRepository;

	private final UserService userService;
	private final ContentService contentService;
	private final StickerService stickerService;

	private final int MISSION_DISTANCE_LIMIT = 50;
	private final int LEVEL_UP_DEGREE = 3;

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
	@Transactional
	public MissionCheckLocationResponse checkMissionLocation(MissionCheckLocationRequest request) {

		// 유저가 가진 미션이 맞는지 확인
		User user = findUser();
		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();
		List<Long> userAssignMissionIdList = new ArrayList<>();
		List<Long> userAssignMissionGroupIdList = new ArrayList<>();
		userAssignMissionList.forEach(
				userAssignMission -> {
					userAssignMissionIdList.add(userAssignMission.getMission().getId());
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
		boolean checkLocationMissionFlag = false;
		Double checkDistance = distance(request.getCurrLatitude(), request.getCurrLongitude()
				, targetMission.getLatitude(), targetMission.getLongitude());

		UserAssignMission checkUserAssignMission = null;
		for (UserAssignMission userAssignMission : targetMission.getUserAssignMissions()) {
			if (user.getId().equals(userAssignMission.getUser().getId())) {
				checkUserAssignMission = userAssignMission;
				if (checkDistance.intValue() <= MISSION_DISTANCE_LIMIT) {
					checkLocationMissionFlag = true;
				}
				checkUserAssignMission.completeLocationCheck();
				break;
			}
		}

		return MissionCheckLocationResponse.builder()
				.missionId(targetMission.getId())
				.locationCheck(checkLocationMissionFlag)
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

		return dist;  // 단위 m
	}

	// 10진수를 radian 으로 변환
	private static double deg2rad(double deg){
		return (deg * Math.PI/180.0);
	}
	// radian 을 10진수로 변환
	private static double rad2deg(double rad){
		return (rad * 180 / Math.PI);
	}

	// 미션 게시물 인증
	@Transactional
	public MissionCheckContentResponse checkMissionContent(UserDetails userDetails, List<MultipartFile> multipartFile, MissionCheckContentRequest request) throws ParseException {

		User user = getUser(userDetails);
		Mission targetMission = missionRepository.findById(request.getMissionId()).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));

		// 미션 진행 기간인지 확인
		if (targetMission.getMissionStatus() != MissionStatus.ACTIVE) {
			throw new CustomException(INVALID_MISSION_PERIOD);
		}
		UserAssignMission targetUserAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), request.getMissionId());
		// 위치 인증이 우선 진행된 미션인지 확인
		if (!targetUserAssignMission.getLocationCheck()) {
			throw new CustomException(NOT_CHECK_MISSION_LOCATION);
		}
		// 이미 완료된 미션인 경우
		if (targetUserAssignMission.getIsComplete()) {
			throw new CustomException(ALREADY_COMPLETE_MISSION);
		}

		ContentDto.CreateDto createDto = ContentDto.CreateDto.builder()
				.content(request.getContent())
				.latitude(targetMission.getLatitude())
				.longitude(targetMission.getLongitude())
				.location(targetMission.getMissionLocationName())
				.groupId(targetMission.getGroup().getId())
				.build();

		contentService.createContent(
				userDetails, multipartFile, targetMission.getGroup().getId(), request.getContent(),
				targetMission.getLatitude(), targetMission.getLongitude(), targetMission.getMissionLocationName()
		);

		// 유저 미션 게시글 인증 상태 업데이트
		targetUserAssignMission.completeContentCheck();

		// 미션 인증 레벨 업데이트
		user.updateSubLevel();

		if (user.getSubLevel() == LEVEL_UP_DEGREE) {
			user.updateLevel();
			// 스티커를 획득할 수 있는 mainLevel 달성 시 획득 처리
			if (getSticker(user.getMainLevel().intValue())) {
				stickerService.acquisitionSticker(user);
			}
		}

		return MissionCheckContentResponse.builder()
				.missionId(targetMission.getId())
				.locationCheck(targetUserAssignMission.getLocationCheck())
				.contentCheck(targetUserAssignMission.getContentCheck())
				.isComplete(targetUserAssignMission.getIsComplete())
				.build();
	}

	private User getUser(UserDetails userDetails) {
		User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
				.orElseThrow(
						() -> new CustomException(Result.FAIL)
				);
		return user;
	}

	// 미션 상태별 목록 조회 (0 : 전체, 1 : 시작 전, 2 : 진행중, 3 : 종료)
	public List<MissionResponse> getMissionList(int missionStatus) {

		User user = findUser();
		MissionStatus findMissionStatus = MissionStatus.getName(missionStatus);
		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();
		List<MissionResponse> missionResponseList = new ArrayList<>();

		for (UserAssignMission userAssignMission : userAssignMissionList) {
			Mission mission = userAssignMission.getMission();
			if (MissionStatus.ALL == findMissionStatus) {
				MissionResponse missionResponse = toMissionResponse(mission);

				MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, mission, userAssignMission);
				missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

				missionResponseList.add(missionResponse);
			} else {
				if (findMissionStatus == mission.getMissionStatus()) {
					MissionResponse missionResponse = toMissionResponse(mission);

					MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, mission, userAssignMission);
					missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

					missionResponseList.add(missionResponse);
				}
			}
		}
		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		return missionResponseList;
	}

	private MissionResponse.UserAssignMissionInfo getUserAssignMissionInfo(User user, Mission mission, UserAssignMission userAssignMission) {
		return MissionResponse.UserAssignMissionInfo.builder()
				.userId(user.getId())
				.userNickname(user.getNickName())
				.missionId(mission.getId())
				.locationCheck(userAssignMission.getLocationCheck())
				.contentCheck(userAssignMission.getContentCheck())
				.isComplete(userAssignMission.getIsComplete())
				.build();
	}

	// 그룹 메인 진입 페이지 내 [시작 전/진행 중] 미션 목록 조회
	public List<MissionResponse> getReadyAndActiveGroupMissionList(int groupId) {

		User user = findUser();
		Group group = findGroup(Long.parseLong(String.valueOf(groupId)));

		List<MissionResponse> missionResponseList = new ArrayList<>();

		List<Mission> missionListInGroup = group.getMissions();
		for (Mission mission : missionListInGroup) {
			if (mission.getMissionStatus() == MissionStatus.READY || mission.getMissionStatus() == MissionStatus.ACTIVE) {
				MissionResponse missionResponse = toMissionResponse(mission);

				UserAssignMission userAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), mission.getId());
				MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, mission, userAssignMission);

				missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

				missionResponseList.add(missionResponse);
			}
		}

		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		return missionResponseList;
	}

	// 시작 전인 미션 + 진행 중인 미션 전체
	public List<MissionResponse> getReadyAndActiveMissionList() {
		List<MissionResponse> missionResponseList = getMissionList(MissionStatus.READY.getCode());
		missionResponseList.addAll(getMissionList(MissionStatus.ACTIVE.getCode()));
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

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
	}
}
