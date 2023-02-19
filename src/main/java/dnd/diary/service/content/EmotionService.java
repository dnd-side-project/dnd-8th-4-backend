package dnd.diary.service.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.EmotionDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.UserRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private final ContentRepository contentRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;

    public CustomResponseEntity<EmotionDto.AddEmotionDto> addEmotion(
            UserDetails userDetails, Long contentId, EmotionDto.AddEmotionDto request
    ) {

        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        return CustomResponseEntity.success(
                EmotionDto.AddEmotionDto.response(
                emotionRepository.save(
                        Emotion.builder()
                                .emotionStatus(request.getEmotionStatus())
                                .content(content)
                                .user(user)
                                .build()
                )
            )
        );
    }
}
