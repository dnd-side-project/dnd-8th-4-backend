package dnd.diary.service;

import static dnd.diary.enumeration.Result.*;

import java.util.ArrayList;
import java.util.List;

import dnd.diary.response.mission.StickerPerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import dnd.diary.domain.group.GroupImage;
import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.sticker.StickerGroup;
import dnd.diary.domain.user.UserImage;
import dnd.diary.request.mission.StickerGroupCreateRequest;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupImageRepository;
import dnd.diary.repository.mission.StickerGroupRepository;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.mission.UserStickerGroupRepository;
import dnd.diary.repository.user.UserImageRepository;
import dnd.diary.response.mission.StickerGroupResponse;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.mission.StickerValidator;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

	private final UserImageRepository userImageRepository;
	private final GroupImageRepository groupImageRepository;
	private final StickerGroupRepository stickerGroupRepository;
	private final StickerRepository stickerRepository;
	private final UserStickerGroupRepository userStickerGroupRepository;
	private final StickerValidator stickerValidator;
	private final S3Service s3Service;

	// 사용자 기본 프로필 등록
	public void createUserProfileImage(List<MultipartFile> multipartFiles) {
		List<String> userProfileImageList = s3Service.uploadImageList(multipartFiles);
		for (String userProfileImage : userProfileImageList) {
			UserImage userImage = UserImage.toEntity(userProfileImage);
			userImageRepository.save(userImage);
		}
	}

	// 그룹 기본 이미지 등록
	public void createGroupProfileImage(List<MultipartFile> multipartFiles) {
		List<String> groupProfileImageList = s3Service.uploadImageList(multipartFiles);
		for (String groupProfileImage : groupProfileImageList) {
			GroupImage groupImage = GroupImage.toEntity(groupProfileImage);
			groupImageRepository.save(groupImage);
		}
	}

	// [관리자] 스티커 그룹 등록
	@Transactional
	public StickerGroupResponse createStickerGroup(StickerGroupCreateRequest request, MultipartFile multipartFile) {

		// 이미 존재하는 스티커 이름인지 확인
		stickerValidator.existStickerThumbnailName(request.getStickerGroupName());
		// 이미 존재하는 스티커 레벨인지 확인
		stickerValidator.existStickerLevel(request.getStickerGroupLevel());

		String stickerUrl = "";
		if (multipartFile != null) {
			stickerUrl = s3Service.uploadImage(multipartFile);
		}

		StickerGroup sticker = StickerGroup.toEntity(request.getStickerGroupName(), request.getStickerGroupLevel(), stickerUrl);
		stickerGroupRepository.save(sticker);

		return StickerGroupResponse.builder()
			.stickerGroupId(sticker.getId())
			.stickerGroupName(sticker.getStickerGroupName())
			.stickerGroupLevel(sticker.getStickerGroupLevel())
			.stickerGroupThumbnailUrl(sticker.getStickerGroupThumbnailUrl())
			.build();
	}

	// [관리자] 스티커 그룹 별 개별 스티커 등록
	@Transactional
	public StickerResponse createSticker(Long stickerGroupId, List<MultipartFile> multipartFiles) {
		// 존재하는 스티커 그룹인지 확인
		StickerGroup targetStickerGroup = stickerGroupRepository.findById(stickerGroupId).orElseThrow(() -> new CustomException(NOT_FOUND_STICKER_GROUP));

		List<String> stickerImageUrlList = s3Service.uploadImageList(multipartFiles);
		List<StickerResponse.StickerInfo> stickerInfoList = new ArrayList<>();
		for (String stickerImageUrl : stickerImageUrlList) {
			Sticker sticker = Sticker.toEntity(stickerImageUrl, targetStickerGroup, false);
			stickerRepository.save(sticker);

			stickerInfoList.add(
				StickerResponse.StickerInfo.builder()
					.stickerId(sticker.getId())
					.stickerImageUrl(sticker.getStickerImageUrl())
					.build()
			);
		}

		return StickerResponse.builder()
			.stickerGroupId(targetStickerGroup.getId())
			.stickerGroupName(targetStickerGroup.getStickerGroupName())
			.stickerGroupLevel(targetStickerGroup.getStickerGroupLevel())
			.stickerGroupThumbnailUrl(targetStickerGroup.getStickerGroupThumbnailUrl())
			.stickerInfoList(stickerInfoList)
			.build();
	}

	@Transactional
	public StickerPerResponse createStickerOne(Long stickerGroupId, MultipartFile multipartFiles, boolean mainStickerYn) {
		// 존재하는 스티커 그룹인지 확인
		StickerGroup targetStickerGroup = stickerGroupRepository.findById(stickerGroupId).orElseThrow(() -> new CustomException(NOT_FOUND_STICKER_GROUP));

		String stickerImageUrl = s3Service.uploadImage(multipartFiles);
		Sticker sticker = Sticker.toEntity(stickerImageUrl, targetStickerGroup, mainStickerYn);
		stickerRepository.save(sticker);

		StickerPerResponse.StickerInfo stickerInfo = StickerPerResponse.StickerInfo.builder()
				.stickerId(sticker.getId())
				.stickerImageUrl(sticker.getStickerImageUrl())
				.mainStickerYn(mainStickerYn)
				.build();

		return StickerPerResponse.builder()
				.stickerGroupId(targetStickerGroup.getId())
				.stickerGroupName(targetStickerGroup.getStickerGroupName())
				.stickerGroupLevel(targetStickerGroup.getStickerGroupLevel())
				.stickerGroupThumbnailUrl(targetStickerGroup.getStickerGroupThumbnailUrl())
				.stickerInfo(stickerInfo)
				.build();
	}

	// [관리자] 획득 가능한 스티커 그룹 목록 조회
	public List<StickerGroupResponse> getSickerGroupList() {
		List<StickerGroupResponse> stickerListResponses = new ArrayList<>();
		List<StickerGroup> stickerGroupList = stickerGroupRepository.findAll();
		stickerGroupList.forEach(
			stickerGroup -> stickerListResponses.add(
				StickerGroupResponse.builder()
					.stickerGroupId(stickerGroup.getId())
					.stickerGroupName(stickerGroup.getStickerGroupName())
					.stickerGroupLevel(stickerGroup.getStickerGroupLevel())
					.stickerGroupThumbnailUrl(stickerGroup.getStickerGroupThumbnailUrl())
					.build()
			)
		);
		return stickerListResponses;
	}

	// [관리자] 획득 가능한 스티커 그룹 별 전체 스티커 목록 조회
	public List<StickerResponse> getStickerList() {
		List<StickerResponse> stickerResponses = new ArrayList<>();
		List<StickerGroup> stickerGroupList = stickerGroupRepository.findAll();
		for (StickerGroup stickerGroup : stickerGroupList) {
			List<Sticker> stickers = stickerGroup.getStickers();

			List<StickerResponse.StickerInfo> stickerInfoList = new ArrayList<>();
			for (Sticker sticker : stickers) {
				stickerInfoList.add(
					StickerResponse.StickerInfo.builder()
						.stickerId(sticker.getId())
						.stickerImageUrl(sticker.getStickerImageUrl())
						.build()
				);
			}

			stickerResponses.add(
				StickerResponse.builder()
					.stickerGroupId(stickerGroup.getId())
					.stickerGroupName(stickerGroup.getStickerGroupName())
					.stickerGroupLevel(stickerGroup.getStickerGroupLevel())
					.stickerGroupThumbnailUrl(stickerGroup.getStickerGroupThumbnailUrl())
					.stickerInfoList(stickerInfoList)
					.build()
			);
		}

		return stickerResponses;
	}
}