package dnd.diary.service.mission;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;
import static dnd.diary.domain.sticker.StickerLevel.getSticker;
import static dnd.diary.enumeration.Result.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.domain.sticker.StickerGroup;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.mission.MissionCheckLocationRequest;
import dnd.diary.dto.mission.MissionListByMapRequest;
import dnd.diary.enumeration.Result;
import dnd.diary.repository.mission.StickerGroupRepository;
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
	private final StickerGroupRepository stickerGroupRepository;

	private final UserService userService;
	private final ContentService contentService;
	private final StickerService stickerService;

//	private final int MISSION_DISTANCE_LIMIT = 50;
	private final int MISSION_DISTANCE_LIMIT = 200;
	private final int LEVEL_UP_DEGREE = 3;
	private final Long MISSION_DEFAULT_D_DAY = 365L;

	// ?????? ??????
	@Transactional
	public MissionResponse createMission(MissionCreateRequest request) throws ParseException {
		User user = findUser();
		Group group = findGroup(request.getGroupId());

		MissionStatus missionStatus = MissionStatus.READY;

		Mission mission = null;
		String pointWKT = String.format("POINT(%s %s)", request.getLatitude(), request.getLongitude());
		Point point = (Point) new WKTReader().read(pointWKT);

		// ?????? ????????? ???????????? ?????? ?????? - ?????? ACTIVE
		if (!request.getExistPeriod()) {
			missionStatus = MissionStatus.ACTIVE;
			mission = Mission.toEntity(
					user, group, request.getMissionName(), request.getMissionNote()
					, request.getExistPeriod()
					, convertLocalDateTimeZone(LocalDate.now().atStartOfDay(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")), null
					, request.getMissionLocationName(), request.getMissionLocationAddress()
					, request.getLatitude(), request.getLongitude()
					, request.getMissionColor(), missionStatus, point);

		} else {
			LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
			// ?????? ????????? < ?????? -> ?????? ????????? ??????
			if (today.isAfter(convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay().minusHours(9), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.ACTIVE;
			}
			// ?????? ????????? < ?????? -> ?????? ?????? ??????
			if (today.isAfter(convertLocalDateTimeZone(request.getMissionEndDate().atStartOfDay().plusDays(1).minusHours(9), ZoneOffset.UTC, ZoneId.of("Asia/Seoul")))) {
				missionStatus = MissionStatus.FINISH;
			}
			// ?????? ?????? ?????? + 9 -> 00:00 / 23:59 ???
			mission = Mission.toEntity(user, group, request.getMissionName(), request.getMissionNote()
					, request.getExistPeriod()
					, convertLocalDateTimeZone(request.getMissionStartDate().atStartOfDay(), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
					, convertLocalDateTimeZone(request.getMissionEndDate().atTime(23, 59, 59), ZoneOffset.UTC, ZoneId.of("Asia/Seoul"))
					, request.getMissionLocationName(), request.getMissionLocationAddress()
					, request.getLatitude(), request.getLongitude()
					, request.getMissionColor(), missionStatus, point);

		}
		missionRepository.save(mission);
		log.info("mission startDate : {}", mission.getMissionStartDate());

		// ????????? ?????? ????????? ???????????? ?????? ??????
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			User groupUser = userJoinGroup.getUser();
			UserAssignMission userAssignMission = UserAssignMission.toEntity(groupUser, mission);
			userAssignMissionRepository.save(userAssignMission);
		}

		Long missionDday;
		if (!request.getExistPeriod()) {
			missionDday = MISSION_DEFAULT_D_DAY;
		} else {
			Period diff = Period.between(LocalDate.now(), request.getMissionEndDate());
			missionDday = Long.valueOf(diff.getDays());
		}

		UserAssignMission userAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), mission.getId());

		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())
			.createUserId(user.getId())
			.createUserName(user.getName())
			.createUserProfileImageUrl(user.getProfileImageUrl())

			.groupId(group.getId())
			.groupName(group.getGroupName())
			.groupImageUrl(group.getGroupImageUrl())

			.existPeriod(request.getExistPeriod())
			.missionStartDate(
					mission.getMissionStartDate() != null ? String.valueOf(mission.getMissionStartDate()).substring(0, 10).replace("-", ".") : String.valueOf(mission.getMissionStartDate())
			)
			.missionEndDate(mission.getMissionEndDate() != null ? String.valueOf(mission.getMissionEndDate()).substring(0, 10).replace("-", ".") : "ing")
			.missionStatus(missionStatus)

			.missionLocationName(mission.getMissionLocationName())
			.missionLocationAddress(mission.getMissionLocationAddress())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())

			.missionDday(missionDday)
			.missionColor(mission.getMissionColor())

			.userAssignMissionInfo(getUserAssignMissionInfo(user, mission, userAssignMission))

			.build();
	}
	
	// ?????? ??????
	@Transactional
	public void deleteMission(Long missionId) {
		User user = findUser();
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));

		if (!user.getId().equals(mission.getMissionCreateUser().getId())) {
			throw new CustomException(FAIL_DELETE_MISSION);
		}

		// ?????? ?????????(?????? ?????????) ?????? ????????? ?????? ?????? ??????
		List<UserAssignMission> userAssignMissionList = mission.getUserAssignMissions();
		userAssignMissionRepository.deleteAll(userAssignMissionList);

		// ?????? ?????? ??????
		missionRepository.delete(mission);
	}
	
	// ?????? ?????? ??????
	@Transactional
	public MissionCheckLocationResponse checkMissionLocation(MissionCheckLocationRequest request) {

		// ????????? ?????? ????????? ????????? ??????
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
		// ?????? ????????? ????????? ????????? ??????
		if (!userAssignMissionGroupIdList.contains(request.getGroupId())) {
			throw new CustomException(INVALID_GROUP_MISSION);
		}
		// ????????? ?????? ????????? ????????? ??????
		if (!userAssignMissionIdList.contains(request.getMissionId())) {
			throw new CustomException(INVALID_USER_MISSION);
		}
		// ?????? ?????? ???????????? ??????
		Mission targetMission = missionRepository.findById(request.getMissionId()).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));
		if (targetMission.getMissionStatus() != MissionStatus.ACTIVE) {
			throw new CustomException(INVALID_MISSION_PERIOD);
		}

		// ?????? ?????? ?????? ?????? ????????? ????????? ?????? 50m ????????? ????????? ??????
		boolean checkLocationMissionFlag = false;
		Double checkDistance = distance(request.getCurrLatitude(), request.getCurrLongitude()
				, targetMission.getLatitude(), targetMission.getLongitude());

		UserAssignMission checkUserAssignMission = null;
		for (UserAssignMission userAssignMission : targetMission.getUserAssignMissions()) {
			if (user.getId().equals(userAssignMission.getUser().getId())) {
				checkUserAssignMission = userAssignMission;

				if (checkDistance.intValue() <= MISSION_DISTANCE_LIMIT) {
					checkLocationMissionFlag = true;
					checkUserAssignMission.completeLocationCheck();
					user.updateSubLevel();
				}
				break;
			}
		}

		return MissionCheckLocationResponse.builder()
				.missionId(targetMission.getId())
				.distance(checkDistance.intValue())
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

		dist = dist * 60 * 1.1515 * 1609.344;

		return dist;  // ?????? m
	}

	// 10????????? radian ?????? ??????
	private static double deg2rad(double deg){
		return (deg * Math.PI / 180.0);
	}
	// radian ??? 10????????? ??????
	private static double rad2deg(double rad){
		return (rad * 180 / Math.PI);
	}

	// ?????? ????????? ??????
	@Transactional
	public MissionCheckContentResponse checkMissionContent(UserDetails userDetails, List<MultipartFile> multipartFiles, Long missionId, String content) throws ParseException {

		User user = getUser(userDetails);
		Mission targetMission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));

		// ?????? ?????? ???????????? ??????
		if (targetMission.getMissionStatus() != MissionStatus.ACTIVE) {
			throw new CustomException(INVALID_MISSION_PERIOD);
		}
		UserAssignMission targetUserAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), missionId);
		// ?????? ????????? ?????? ????????? ???????????? ??????
		if (!targetUserAssignMission.getLocationCheck()) {
			throw new CustomException(NOT_CHECK_MISSION_LOCATION);
		}
		// ?????? ????????? ????????? ??????
		if (targetUserAssignMission.getIsComplete()) {
			throw new CustomException(ALREADY_COMPLETE_MISSION);
		}

		contentService.createContent(
				userDetails, multipartFiles, targetMission.getGroup().getId(), content,
				targetMission.getLatitude(), targetMission.getLongitude(), targetMission.getMissionLocationName()
		);

		// ?????? ?????? ????????? ?????? ?????? ????????????
		targetUserAssignMission.completeContentCheck();

		// ?????? ?????? ?????? ????????????
		user.updateSubLevel();

		Boolean isGetNewSticker = false;
		Long currMainLevel = user.getMainLevel();
		Long stickerGroupId = null;
		String stickerGroupName = null;

		if (user.getSubLevel().intValue() == LEVEL_UP_DEGREE) {
			user.updateLevel();
			// ???????????? ????????? ??? ?????? mainLevel ?????? ??? ?????? ??????
			if (getSticker(user.getMainLevel().intValue())) {
				stickerService.acquisitionSticker(user);
				isGetNewSticker = true;
				currMainLevel = user.getMainLevel();
				// mainLevel ??? ???????????? ????????? ????????? ID
				StickerGroup stickerGroup = stickerGroupRepository.findByStickerGroupLevel(user.getMainLevel());
				stickerGroupId = stickerGroup.getId();
				stickerGroupName = stickerGroup.getStickerGroupName();
			}
		}

		return MissionCheckContentResponse.builder()
				.missionId(targetMission.getId())
				.locationCheck(targetUserAssignMission.getLocationCheck())
				.contentCheck(targetUserAssignMission.getContentCheck())
				.isComplete(targetUserAssignMission.getIsComplete())

				.isGetNewSticker(isGetNewSticker)   // true ??? ???????????? getNewStickerGroupId ??? null ??? ?????? ???
				.currMainLevel(currMainLevel)
				.getNewStickerGroupId(stickerGroupId)
				.getNewStickerGroupName(stickerGroupName)
				.build();
	}

	private User getUser(UserDetails userDetails) {
		User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
				.orElseThrow(
						() -> new CustomException(Result.FAIL)
				);
		return user;
	}

	// ?????? ????????? ?????? ?????? (0 : ??????, 1 : ?????? ???, 2 : ?????????, 3 : ??????)
	public List<MissionResponse> getMissionList(int missionStatus) {

		User user = findUser();
		MissionStatus findMissionStatus = MissionStatus.getName(missionStatus);
		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();
		List<MissionResponse> missionResponseList = new ArrayList<>();

		for (UserAssignMission userAssignMission : userAssignMissionList) {
			Mission mission = userAssignMission.getMission();

			if (userAssignMission.getIsComplete()) continue;

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

	// ?????? ?????? ?????? ????????? ??? [?????? ???/?????? ???] ?????? ?????? ?????? - ?????? 4???
	public List<MissionResponse> getReadyAndActiveGroupMissionList(int groupId) {

		User user = findUser();
		Group group = findGroup(Long.parseLong(String.valueOf(groupId)));

		List<MissionResponse> missionResponseList = new ArrayList<>();

		List<Mission> missionListInGroup = group.getMissions();
		for (Mission mission : missionListInGroup) {
			if (mission.getMissionStatus() == MissionStatus.READY || mission.getMissionStatus() == MissionStatus.ACTIVE) {
				MissionResponse missionResponse = toMissionResponse(mission);

				UserAssignMission userAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), mission.getId());
				if (userAssignMission == null) {
					throw new CustomException(INVALID_USER_MISSION);
				}
				if (!userAssignMission.getIsComplete()) {   // ???????????? ????????? ????????? ??????
					MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, mission, userAssignMission);
					missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

					missionResponseList.add(missionResponse);
				}
			}
		}

		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		List<MissionResponse> maxFourMissionResponseList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			if (i < missionResponseList.size()) {
				maxFourMissionResponseList.add(missionResponseList.get(i));
			}
		}
		return maxFourMissionResponseList;
	}

	// ?????? ?????? ?????? + ?????? ?????? ?????? ??????
	public List<MissionResponse> getReadyAndActiveMissionList() {
		List<MissionResponse> missionResponseList = getMissionList(MissionStatus.READY.getCode());
		missionResponseList.addAll(getMissionList(MissionStatus.ACTIVE.getCode()));
		return missionResponseList;
	}

	public MissionResponse getMission(Long missionId) {
		User user = findUser();
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new CustomException(NOT_FOUND_MISSION));
		UserAssignMission userAssignMission = userAssignMissionRepository.findByUserIdAndMissionId(user.getId(), missionId);
		if (userAssignMission == null) {
			throw new CustomException(INVALID_USER_MISSION);
		}
		MissionResponse missionResponse = toMissionResponse(mission);
		MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, mission, userAssignMission);
		missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

		return missionResponse;
	}

	private MissionResponse toMissionResponse(Mission mission) {
		return MissionResponse.builder()
			.missionId(mission.getId())
			.missionName(mission.getMissionName())
			.missionNote(mission.getMissionNote())

			.createUserId(mission.getMissionCreateUser().getId())
			.createUserName(mission.getMissionCreateUser().getName())
			.createUserProfileImageUrl(mission.getMissionCreateUser().getProfileImageUrl())

			.groupId(mission.getGroup().getId())
			.groupName(mission.getGroup().getGroupName())
			.groupImageUrl(mission.getGroup().getGroupImageUrl())

			.existPeriod(mission.getExistPeriod())
			.missionStartDate(
					mission.getMissionStartDate() != null ? String.valueOf(mission.getMissionStartDate()).substring(0, 10).replace("-", ".") : String.valueOf(mission.getMissionStartDate())
			)
			.missionEndDate(mission.getMissionEndDate() != null ? String.valueOf(mission.getMissionEndDate()).substring(0, 10).replace("-", ".") : "ing")

			.missionStatus(mission.getMissionStatus())
			.missionLocationName(mission.getMissionLocationName())
			.missionLocationAddress(mission.getMissionLocationAddress())
			.latitude(mission.getLatitude())
			.longitude(mission.getLongitude())

			.missionDday(
					mission.getMissionEndDate() != null ? (long) Period.between(LocalDate.now(ZoneId.of("Asia/Seoul")), mission.getMissionEndDate().toLocalDate()).getDays() : MISSION_DEFAULT_D_DAY
			)
			.missionColor(mission.getMissionColor())
			.build();
	}

	// ???????????? ????????? ?????? ???, ?????? ?????? ?????? ???????????? ?????? ?????? ??????
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
			if (!userMissionList.contains(mission)) {   // ???????????? ????????? ????????? ?????? ??????
				continue;
			}
			if (mission.getMissionStatus() == MissionStatus.ACTIVE && !mission.isDeleted()) {
				missionResponseList.add(toMissionResponse(mission));
			}
		}
		missionResponseList.sort(Comparator.comparing(MissionResponse::getMissionDday));
		return missionResponseList;
	}

	// ????????? ?????? ?????? ??????
	public List<MissionResponse> getCompleteMissionList() {
		User user = findUser();
		List<MissionResponse> completeMissionResponseList = new ArrayList<>();

		List<UserAssignMission> userAssignMissionList = user.getUserAssignMissions();

		for (UserAssignMission userAssignMission : userAssignMissionList) {
			if (userAssignMission.getIsComplete()) {
				MissionResponse missionResponse = toMissionResponse(userAssignMission.getMission());

				MissionResponse.UserAssignMissionInfo userAssignMissionInfo = getUserAssignMissionInfo(user, userAssignMission.getMission(), userAssignMission);
				missionResponse.setUserAssignMissionInfo(userAssignMissionInfo);

				completeMissionResponseList.add(missionResponse);
			}
		}
		return completeMissionResponseList;
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
	}
}