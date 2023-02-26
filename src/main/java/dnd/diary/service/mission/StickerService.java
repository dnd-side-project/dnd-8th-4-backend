package dnd.diary.service.mission;

import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.sticker.UserSticker;
import dnd.diary.domain.user.User;
import dnd.diary.dto.mission.StickerCreateRequest;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.mission.UserStickerRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.mission.StickerMainResponse;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.s3.S3Service;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static dnd.diary.enumeration.Result.ALREADY_ACQUISITION_STICKER;
import static dnd.diary.enumeration.Result.NOT_FOUND_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;
    private final UserStickerRepository userStickerRepository;
    private final UserRepository userRepository;

    private final StickerValidator stickerValidator;

    private final S3Service s3Service;
    private final UserService userService;

    private final int LEVEL_UP_DEGREE = 3;

    // [관리자] 스티커 등록
    @Transactional
    public StickerResponse createSticker(StickerCreateRequest request, MultipartFile multipartFile) {

        // 이미 존재하는 스티커 이름인지 확인
        stickerValidator.existStickerName(request.getStickerName());
        // 이미 존재하는 스티커 레벨인지 확인
        stickerValidator.existStickerLevel(request.getStickerLevel());

        String stickerUrl = "";
        if (multipartFile != null) {
            stickerUrl = s3Service.uploadImage(multipartFile);
        }

        Sticker sticker = Sticker.toEntity(request.getStickerName(), request.getStickerLevel(), stickerUrl);
        stickerRepository.save(sticker);

        return StickerResponse.builder()
                .stickerId(sticker.getId())
                .stickerName(sticker.getStickerName())
                .stickerLevel(sticker.getStickerLevel())
                .stickerUrl(sticker.getStickerUrl())
                .build();
    }

    // [관리자] 획득 가능한 스티커 목록 조회
    public List<StickerResponse> getSickerList() {
        List<StickerResponse> stickerListResponses = new ArrayList<>();
        List<Sticker> stickerList = stickerRepository.findAll();
        stickerList.forEach(
                sticker -> stickerListResponses.add(
                        StickerResponse.builder()
                        .stickerId(sticker.getId())
                        .stickerName(sticker.getStickerName())
                        .stickerLevel(sticker.getStickerLevel())
                        .stickerUrl(sticker.getStickerUrl()).build()
                )
        );
        return stickerListResponses;
    }

    // 미션 달성 및 mainLevel 달성에 따른 스티커 획득 처리
    @Transactional
    public void acquisitionSticker(User user) {
        Long userMainLevel = user.getMainLevel();
        // userMainLevel 과 stickerLevel 이 같은 스티커 획득 처리
        Sticker targetSticker = stickerRepository.findByStickerLevel(userMainLevel);
        for (UserSticker userSticker : user.getUserStickers()) {
            if (targetSticker.getStickerLevel().equals(userSticker.getSticker().getStickerLevel())) {   // 이미 가진 스티커인지 확인
                throw new CustomException(ALREADY_ACQUISITION_STICKER);
            }
        }

        UserSticker userSticker = UserSticker.toEntity(user, targetSticker);
        userStickerRepository.save(userSticker);
    }

    // 미션 > 스티커 화면 정보 조회
    public StickerMainResponse getSickerMain() {
        User user = findUser();

        StickerMainResponse.CurrMissionInfo currMissionInfo = new StickerMainResponse.CurrMissionInfo();
        StickerMainResponse.AcquisitionStickerInfo acquisitionStickerInfo = new StickerMainResponse.AcquisitionStickerInfo();


        return StickerMainResponse
                .builder()
                .currMissionInfo(currMissionInfo)
                .acquisitionStickerInfo(acquisitionStickerInfo)
                .build();
    }

    // 댓글 작성 시 사용 가능한, 유저가 보유한 스티커 목록 조회
    public List<StickerResponse> getMyStickerList() {
        User user = findUser();
        List<StickerResponse> myStickerList = new ArrayList<>();
        List<UserSticker> userStickerList = user.getUserStickers();
        for (UserSticker userSticker : userStickerList) {
            Sticker sticker = userSticker.getSticker();
            myStickerList.add(
                    StickerResponse.builder()
                            .stickerId(sticker.getId())
                            .stickerName(sticker.getStickerName())
                            .stickerLevel(sticker.getStickerLevel())
                            .stickerUrl(sticker.getStickerUrl())
                            .build()

            );
        }
        return myStickerList;
    }

    private User findUser() {
        UserDto.InfoDto userInfo = userService.findMyListUser();
        return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}