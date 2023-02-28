package dnd.diary.controller.mission;

import dnd.diary.dto.mission.StickerCreateRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.StickerGroupResponse;
import dnd.diary.response.mission.StickerMainResponse;
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

    @GetMapping("/list/my")
    public CustomResponseEntity<List<StickerResponse>> getMyStickerList() {
        return CustomResponseEntity.success(stickerService.getMyStickerList());
    }

    @GetMapping("/main")
    public CustomResponseEntity<StickerMainResponse> getSickerMain() {
        return CustomResponseEntity.success(stickerService.getSickerMain());
    }
}
