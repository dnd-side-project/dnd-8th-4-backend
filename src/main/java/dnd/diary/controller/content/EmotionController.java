package dnd.diary.controller.content;

import dnd.diary.request.content.EmotionDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class EmotionController {
    private final EmotionService emotionService;

    @PostMapping("/content/{contentId}/emotion")
    public CustomResponseEntity<EmotionDto.AddEmotionDto> emotionSave(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(name = "contentId") final Long contentId,
            @Valid @RequestBody final EmotionDto.AddEmotionDto request
    ) {
        return emotionService.saveEmotion(userDetails, contentId, request);
    }

}
