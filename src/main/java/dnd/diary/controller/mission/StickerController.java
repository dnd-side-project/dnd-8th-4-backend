package dnd.diary.controller.mission;

import dnd.diary.dto.mission.StickerCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.StickerCreateResponse;
import dnd.diary.service.mission.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sticker")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;

    @PostMapping
    public CustomResponseEntity<StickerCreateResponse> createSticker(
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestPart StickerCreateRequest request
    ) {
        return CustomResponseEntity.success(stickerService.createSticker(request, multipartFile));
    }
}
