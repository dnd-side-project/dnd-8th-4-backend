package dnd.diary.controller.mission;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.StickerMainResponse;
import dnd.diary.response.mission.StickerResponse;
import dnd.diary.service.mission.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sticker")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;

    // 유저가 보유한 스티커 그룹 조회
    @GetMapping("/list/my/group")
    public CustomResponseEntity<List<StickerResponse>> getMyStickerGroupList() {
        return CustomResponseEntity.success(stickerService.getMyStickerGroupList());
    }

    // 유저가 보유한 스티커 그룹 별 전체 스티커 조회
    @GetMapping("/list/my")
    public CustomResponseEntity<StickerResponse> getMyStickerGroupList(
        @RequestPart Long stickerGroupId
    ) {
        return CustomResponseEntity.success(stickerService.getMyStickerListByGroup(stickerGroupId));
    }

    // 유저가 보유한 스티커 그룹 별 개별 스티커 목록 전체 조회
    @GetMapping("/list/my/all")
    public CustomResponseEntity<List<StickerResponse>> getMyStickerList() {
        return CustomResponseEntity.success(stickerService.getMyStickerList());
    }

    // 미션 > 스티커 메인 화면 조회
    @GetMapping("/main")
    public CustomResponseEntity<StickerMainResponse> getSickerMain() {
        return CustomResponseEntity.success(stickerService.getSickerMain());
    }
}
