package dnd.diary.service.mission;

import dnd.diary.domain.comment.Sticker;
import dnd.diary.dto.mission.StickerCreateRequest;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.mission.UserStickerRepository;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;
    private final UserStickerRepository userStickerRepository;

    private final StickerValidator stickerValidator;

    private final S3Service s3Service;

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

    // 미션 > 스티커 화면 정보 조회
    public void acquisitionSticker() {

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
}