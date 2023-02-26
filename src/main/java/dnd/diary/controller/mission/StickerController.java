package dnd.diary.controller.mission;

import dnd.diary.dto.mission.StickerCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.mission.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/sticker")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;

    // [관리자] 스티커 등록
    @PostMapping
    public CustomResponseEntity<StickerResponse> createSticker(
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestPart StickerCreateRequest request
    ) {
        return CustomResponseEntity.success(stickerService.createSticker(request, multipartFile));
    }

    // [관리자] 획득 가능한 스티커 목록 조회
    @GetMapping("/list")
    public CustomResponseEntity<List<StickerResponse>> getStickerList() {
        return CustomResponseEntity.success(stickerService.getSickerList());
    }

    @GetMapping("/list/my")
    public CustomResponseEntity<List<StickerResponse>> getMyStickerList() {
        return CustomResponseEntity.success(stickerService.getMyStickerList());
    }
}
