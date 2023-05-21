package dnd.diary.controller.content;

import dnd.diary.request.controller.content.EmotionRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.response.content.EmotionResponse;
import dnd.diary.service.content.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmotionController {
    private final EmotionService emotionService;

    @PostMapping("/content/{contentId}/emotion")
    public CustomResponseEntity<EmotionResponse.Add> processEmotionTransaction(
            @AuthenticationPrincipal final Long userId,
            @PathVariable(name = "contentId") final Long contentId,
            @Valid @RequestBody final EmotionRequest.Add request
    ) {
        return emotionService.processEmotionTransaction(userId, contentId, request.toServiceRequest());
    }

    // 피드 감정 리스트 조회
    @GetMapping("content/{contentId}/emotion")
    public CustomResponseEntity<List<ContentResponse.EmotionDetail>> listEmotion(
            @PathVariable(name = "contentId") final Long contentId
    ) {
        return CustomResponseEntity.success(emotionService.emotionList(contentId));
    }
}
