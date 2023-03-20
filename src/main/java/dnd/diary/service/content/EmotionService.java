package dnd.diary.service.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.EmotionDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dnd.diary.enumeration.Result.FAIL;
import static dnd.diary.enumeration.Result.NOT_SAVE_EMOTION_DELETE;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionService {
    private final ContentRepository contentRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public CustomResponseEntity<EmotionDto.AddEmotionDto> saveEmotion(
            UserDetails userDetails, Long contentId, EmotionDto.AddEmotionDto request
    ) {
        validateAddEmotion(request,contentId);

        User user = getUser(userDetails);
        Emotion existsEmotionUser = emotionRepository.findByContentIdAndUserId(contentId, user.getId());

        if (existsEmotionUser == null) {
            // 감정 표현 등록
            Emotion emotion = Emotion.builder()
                .emotionStatus(request.getEmotionStatus())
                .content(getContent(contentId))
                .user(user)
                .emotionYn(true)
                .build();
            emotionRepository.save(emotion);

            // 게시물 생성자에게 알림 생성
            Content content = getContent(contentId);
            Notification notification = Notification.toContentEmotionEntity(
                content, emotion, content.getUser(), NotificationType.CONTENT_EMOTION);
            notificationRepository.save(notification);

            return CustomResponseEntity.success(EmotionDto.AddEmotionDto.response(emotion));

        } else if (existsEmotionUser.getEmotionStatus().equals(request.getEmotionStatus())) {
//            emotionRepository.deleteById(existsEmotionUser.getId());
            existsEmotionUser.cancelEmotion();   // 감정 표현 취소
//            Notification notification = notificationRepository
//                    .findByContentIdAndEmotionIdAndUserId(contentId, existsEmotionUser.getId(), user.getId())
//                    .orElseThrow(
//                            () -> new CustomException(FAIL)
//                    );
            return CustomResponseEntity.successDeleteEmotion();
        } else {
            // 감정 표현 업데이트
            existsEmotionUser.updateEmotion(request.getEmotionStatus());
            return CustomResponseEntity.success(EmotionDto.AddEmotionDto.response(existsEmotionUser));
        }
    }

    // method
    private User getUser(UserDetails userDetails) {
        return userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
    }

    private Content getContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
    }

    // validate
    private void validateAddEmotion(EmotionDto.AddEmotionDto request, Long contentId) {
        if (!contentRepository.existsByIdAndDeletedYn(contentId, false)){
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
        if(request.getEmotionStatus()>=6){
            throw new CustomException(Result.NOT_SUPPORTED_EMOTION_STATUS);
        }
    }
}
