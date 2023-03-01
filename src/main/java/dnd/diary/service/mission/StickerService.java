package dnd.diary.service.mission;

import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.sticker.StickerGroup;
import dnd.diary.domain.sticker.UserStickerGroup;
import dnd.diary.domain.user.User;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.mission.StickerGroupRepository;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.mission.UserStickerGroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.mission.StickerMainResponse;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.s3.S3Service;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static dnd.diary.enumeration.Result.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StickerService {

    private final StickerGroupRepository stickerGroupRepository;
    private final StickerRepository stickerRepository;
    private final UserStickerGroupRepository userStickerGroupRepository;
    private final UserRepository userRepository;

    private final StickerValidator stickerValidator;

    private final S3Service s3Service;
    private final UserService userService;

    private final int LEVEL_UP_DEGREE = 3;


    // 미션 달성 및 mainLevel 달성에 따른 스티커 (그룹) 획득 처리
    @Transactional
    public void acquisitionSticker(User user) {
        Long userMainLevel = user.getMainLevel();
        // userMainLevel 과 stickerLevel 이 같은 스티커 획득 처리
        StickerGroup targetStickerGroup = stickerGroupRepository.findByStickerGroupLevel(userMainLevel);

        for (UserStickerGroup userStickerGroup : user.getUserStickerGroups()) {
            if (targetStickerGroup.getStickerGroupLevel().equals(userStickerGroup.getStickerGroup().getStickerGroupLevel())) {   // 이미 가진 스티커인지 확인
                throw new CustomException(ALREADY_ACQUISITION_STICKER);
            }
        }

        UserStickerGroup userStickerGroup = UserStickerGroup.toEntity (user, targetStickerGroup);
        userStickerGroupRepository.save(userStickerGroup);
    }

    // 미션 > 스티커 화면 정보 조회
    public StickerMainResponse getSickerMain() {
        User user = findUser();

        StickerMainResponse.CurrMissionInfo currMissionInfo = StickerMainResponse.CurrMissionInfo.builder()
                .subLevel(user.getSubLevel())
                .mainLevel(user.getMainLevel())
                .remainToUpMainLevel(LEVEL_UP_DEGREE - user.getSubLevel())
                .build();

        List<StickerMainResponse.AcquisitionStickerInfo> acquisitionStickerInfoList = new ArrayList<>();

        List<StickerGroup> stickerList = stickerGroupRepository.findAll();
        for (StickerGroup stickerGroup : stickerList) {
            StickerMainResponse.AcquisitionStickerInfo acquisitionStickerInfo = StickerMainResponse.AcquisitionStickerInfo.builder()
                    .stickerGroupId(stickerGroup.getId())
                    .stickerGroupName(stickerGroup.getStickerGroupName())
                    .stickerGroupLevel(stickerGroup.getStickerGroupLevel())
                    .stickerGroupThumbnailUrl(stickerGroup.getStickerGroupThumbnailUrl())
                    .isAcquisitionStickerGroup(
                        userStickerGroupRepository.existsByUserIdAndStickerGroupId(user.getId(), stickerGroup.getId()))
                    .build();

            acquisitionStickerInfoList.add(acquisitionStickerInfo);
        }

        return StickerMainResponse
                .builder()
                .currMissionInfo(currMissionInfo)
                .acquisitionStickerInfo(acquisitionStickerInfoList)
                .build();
    }

    // 유저가 보유한 스티커 그룹 조회
    public List<StickerResponse> getMyStickerGroupList() {
        User user = findUser();
        List<StickerResponse> myStickerList = new ArrayList<>();
        List<UserStickerGroup> userStickerGroupList = user.getUserStickerGroups();
        for (UserStickerGroup userStickerGroup : userStickerGroupList) {
            StickerGroup stickerGroup = userStickerGroup.getStickerGroup();
            myStickerList.add(
                    StickerResponse.builder()
                            .stickerGroupId(stickerGroup.getId())
                            .stickerGroupName(stickerGroup.getStickerGroupName())
                            .stickerGroupLevel(stickerGroup.getStickerGroupLevel())
                            .stickerGroupThumbnailUrl(stickerGroup.getStickerGroupThumbnailUrl())
                            .build()

            );
        }
        return myStickerList;
    }

    // 유저가 보유한 스티커 그룹 별 전체 스티커 조회
    public StickerResponse getMyStickerListByGroup(Long stickerGroupId) {
        User user = findUser();

        UserStickerGroup userStickerGroup = userStickerGroupRepository.findByUserIdAndStickerGroupId(user.getId(), stickerGroupId);
        if (userStickerGroup == null) {
            throw new CustomException(HAVE_NOT_STICKER);
        }
        StickerGroup targetStickerGroup = userStickerGroup.getStickerGroup();

        List<StickerResponse.StickerInfo> stickerInfoList = new ArrayList<>();
        for (Sticker sticker : targetStickerGroup.getStickers()) {
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

    // 유저가 보유한 스티커 그룹 별 개별 스티커 목록 전체 조회
    public List<StickerResponse> getMyStickerList() {

        User user = findUser();

        List<StickerResponse> stickerResponses = new ArrayList<>();

        List<UserStickerGroup> userStickerGroupList = user.getUserStickerGroups();
        for (UserStickerGroup userStickerGroup : userStickerGroupList) {
            StickerGroup targetStickerGroup = userStickerGroup.getStickerGroup();
            List<StickerResponse.StickerInfo> stickerInfoList = new ArrayList<>();
            for (Sticker sticker : targetStickerGroup.getStickers()) {
                stickerInfoList.add(
                    StickerResponse.StickerInfo.builder()
                        .stickerId(sticker.getId())
                        .stickerImageUrl(sticker.getStickerImageUrl())
                        .build()
                );
            }

            stickerResponses.add(
                StickerResponse.builder()
                    .stickerGroupId(targetStickerGroup.getId())
                    .stickerGroupName(targetStickerGroup.getStickerGroupName())
                    .stickerGroupLevel(targetStickerGroup.getStickerGroupLevel())
                    .stickerGroupThumbnailUrl(targetStickerGroup.getStickerGroupThumbnailUrl())
                    .stickerInfoList(stickerInfoList)
                    .build()
            );
        }
        return stickerResponses;
    }

    private User findUser() {
        UserDto.InfoDto userInfo = userService.findMyListUser();
        return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}