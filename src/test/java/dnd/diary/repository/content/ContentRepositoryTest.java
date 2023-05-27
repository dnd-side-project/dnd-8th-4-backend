package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ContentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserJoinGroupRepository userJoinGroupRepository;

    @DisplayName("사용자의 좌상단 우하단 값의 위치 데이터를 받아 해당 위치에 포함되어있는 피드 데이터를 가져온다.")
    @Test
    void findByMapList() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);

        UserJoinGroup userJoinGroup = UserJoinGroup.builder()
                .user(user)
                .group(group)
                .build();

        userJoinGroupRepository.save(userJoinGroup);

        getContentAndSave(user, group, 37.802508, 127.076286);
        getContentAndSave(user, group, 37.802508, 127.076286);
        getContentAndSave(user, group, 37.802508, 127.076286);

        // when
        List<Content> byMapList = contentRepository.mapSearchMyGroupContent(37.806840, 37.798631, 127.071024, 127.081482, user.getId());

        // then
        assertThat(byMapList).hasSize(3)
                .extracting("content")
                .contains("테스트 내용");
    }

    // method
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

    private Content getContentAndSave(User user, Group group, Double latitude, Double longitude) {
        Content content = Content.builder()
                .user(user)
                .group(group)
                .content("테스트 내용")
                .latitude(latitude)
                .longitude(longitude)
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