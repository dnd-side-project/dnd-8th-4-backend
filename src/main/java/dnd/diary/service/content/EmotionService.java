package dnd.diary.service.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.request.service.content.EmotionServiceRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.response.content.EmotionResponse;
import dnd.diary.service.group.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionService {
    private final ContentRepository contentRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Transactional
    @CacheEvict(value = "Contents", key = "#contentId", cacheManager = "testCacheManager")
    public CustomResponseEntity<EmotionResponse.Add> saveEmotion(
            Long userId, Long contentId, EmotionServiceRequest.Add request
    ) {
        validateAddEmotion(request,contentId);

        User user = getUser(userId);
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

            // 자신을 제외한 게시물 생성자에게 알림 생성
            notificationService.sendToNotification(contentId, user, emotion);

            return CustomResponseEntity.success(EmotionResponse.Add.response(emotion));

        } else {
            // 공감이 존재하는 상태에서
            if (existsEmotionUser.isEmotionYn()) {

                // 같은 공감을 누를 경우 -> 취소
                if (existsEmotionUser.getEmotionStatus().equals(request.getEmotionStatus())) {
                    existsEmotionUser.cancelEmotion();
                    return CustomResponseEntity.successDeleteEmotion();
                }

                // 다른 공감을 누를 경우 -> 변경
                else {
                    existsEmotionUser.updateEmotion(request.getEmotionStatus());
                    return CustomResponseEntity.success(EmotionResponse.Add.response(existsEmotionUser));
                }

            // 공감이 취소되었던 상태에서
            } else {
                existsEmotionUser.updateEmotion(request.getEmotionStatus());
                return CustomResponseEntity.success(EmotionResponse.Add.response(existsEmotionUser));
            }
        }
    }

    public List<ContentResponse.EmotionDetail> emotionList(Long contentId) {
        return emotionRepository.findByContentId(contentId)
                .stream()
                .filter(Emotion::isEmotionYn)
                .map(ContentResponse.EmotionDetail::response)
                .toList();
    }

    // method
    private User getUser(Long userId) {
        return userRepository.findById(userId)
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
    private void validateAddEmotion(EmotionServiceRequest.Add request, Long contentId) {
        if (!contentRepository.existsByIdAndDeletedYn(contentId, false)){
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
        if(request.getEmotionStatus()>=6){
            throw new CustomException(Result.NOT_SUPPORTED_EMOTION_STATUS);
        }
    }
}