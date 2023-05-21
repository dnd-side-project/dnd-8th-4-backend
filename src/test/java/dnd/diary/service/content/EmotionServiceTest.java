package dnd.diary.service.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.controller.content.EmotionRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.EmotionResponse;
import dnd.diary.service.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmotionServiceTest {

    @MockBean
    private RedisService redisService;

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EmotionRepository emotionRepository;

    @DisplayName("유저가 해당 피드에 공감을 남기지 않은 상태에서 최초로 공감을 남긴다.")
    @Test
    void processEmotionTransaction() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);

        EmotionRequest.Add request = new EmotionRequest.Add(3L);
        // when
        CustomResponseEntity<EmotionResponse.Add> response =
                emotionService.processEmotionTransaction(user.getId(), content.getId(), request.toServiceRequest());

        // then
        assertThat(response.getData().getEmotionStatus())
                .isEqualTo(3L);
    }

    @DisplayName("유저가 해당 피드에 공감을 남긴 상태에서 다른 공감으로 변경한다.")
    @Test
    void processEmotionTransactionToChangeEmotion() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Emotion emotion = getEmotionAndSave(user, content, 3L);

        EmotionRequest.Add request = new EmotionRequest.Add(2L);

        // when
        CustomResponseEntity<EmotionResponse.Add> response =
                emotionService.processEmotionTransaction(user.getId(), content.getId(), request.toServiceRequest());

        // then
        assertThat(response.getData().getEmotionStatus())
                .isEqualTo(2L);
    }

    @DisplayName("유저가 해당 피드에 공감을 남긴 상태에서 공감을 취소한다.")
    @Test
    void processEmotionTransactionToDeleteEmotion() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Emotion emotion = getEmotionAndSave(user, content, 2L);

        EmotionRequest.Add request = new EmotionRequest.Add(2L);

        // when
        emotionService.processEmotionTransaction(user.getId(), content.getId(), request.toServiceRequest());

        // then
        Optional<Emotion> emotionOptional = emotionRepository.findById(emotion.getId());
        assertThat(emotionOptional.isPresent()).isTrue();
        assertThat(emotionOptional.get().isEmotionYn()).isFalse();
    }

    @DisplayName("유저가 해당 공감을 취소한 상태에서 다시 공감을 남긴다.")
    @Test
    void processEmotionTransactionToCancelWillAddEmotion() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Emotion emotion = getEmotionAndSave(user, content, Boolean.FALSE);

        EmotionRequest.Add request = new EmotionRequest.Add(2L);

        // when
        CustomResponseEntity<EmotionResponse.Add> response =
                emotionService.processEmotionTransaction(user.getId(), content.getId(), request.toServiceRequest());

        // then
        Optional<Emotion> emotionOptional = emotionRepository.findById(emotion.getId());
        assertThat(emotionOptional.isPresent()).isTrue();
        assertThat(emotionOptional.get().isEmotionYn()).isTrue();
        assertThat(emotionOptional.get().getEmotionStatus()).isEqualTo(2L);
    }

    @DisplayName("유저가 해당 피드의 남겨진 공감을 조회한다.")
    @Test
    void emotionList() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Emotion emotion = getEmotionAndSave(user, content, 2L);

        // when
        List<EmotionResponse.Detail> response = emotionService.emotionList(content.getId());

        // then
        assertThat(response)
                .extracting("id", "emotionStatus")
                .contains(
                        tuple(emotion.getId(), 2L)
                );
    }

    // method
    private Emotion getEmotionAndSave(User user, Content content, Long emotionStatus) {
        Emotion emotion = Emotion.builder()
                .user(user)
                .content(content)
                .emotionStatus(emotionStatus)
                .emotionYn(true)
                .build();
        return emotionRepository.save(emotion);
    }

    private Emotion getEmotionAndSave(User user, Content content, Boolean emotionYn) {
        Emotion emotion = Emotion.builder()
                .user(user)
                .content(content)
                .emotionStatus(1L)
                .emotionYn(emotionYn)
                .build();
        return emotionRepository.save(emotion);
    }

    private User getUserAndSave() {
        User user = User.builder()
                .authorities(getAuthorities())
                .email("test@test.com")
                .password("abc123!")
                .name("테스트 계정")
                .nickName("테스트 닉네임")
                .phoneNumber("010-1234-5678")
                .profileImageUrl("test.png")
                .mainLevel(0L)
                .subLevel(0.0)
                .isNewNotification(Boolean.FALSE)
                .build();

        return userRepository.save(user);
    }

    private static Set<Authority> getAuthorities() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private Content getContentAndSave(User user, Group group) {
        Content content = Content.builder()
                .id(1L)
                .user(user)
                .group(group)
                .content("테스트 내용")
                .latitude(0.0)
                .longitude(0.0)
                .location("삼성 서비스 센터")
                .views(0)
                .contentLink("test.com")
                .build();

        return contentRepository.save(content);
    }

    private Group getGroupSave(User user) {
        Group group = Group.builder()
                .groupName("테스트 그룹")
                .groupCreateUser(user)
                .groupImageUrl("테스트 이미지")
                .groupNote("테스트 내용")
                .build();

        groupRepository.save(group);
        return group;
    }
}