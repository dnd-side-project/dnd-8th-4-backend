package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.content.ContentDto;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.service.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ContentServiceTest {

    @MockBean
    private RedisService redisService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @DisplayName("유저가 피드를 작성한다.")
    @Test
    void contentCreate() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);

        // when
        ContentResponse.Create response =
                contentService.createContent(user.getId(), null, group.getId(), "테스트 내용", 1.0, 1.0, "삼성 서비스센터");

        // then
        assertThat(response)
                .extracting("userName", "content", "latitude", "longitude", "location")
                .contains("테스트 닉네임", "테스트 내용", 1.0, 1.0, "삼성 서비스센터");
    }

    @DisplayName("유저가 피드를 상세조회 한다.")
    @Test
    void contentDetail() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);

        given(redisService.getViewsAndRedisSave(anyLong(), anyString()))
                .willReturn(5);

        // when
        ContentResponse.Detail response = contentService.detailContent(user.getId(), content.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response)
                .extracting("groupName", "userName", "content", "location")
                .contains("테스트 그룹", "테스트 닉네임", "테스트 내용", "삼성 서비스 센터");
    }

    @DisplayName("유저가 선택한 그룹의 피드를 페이지로 조회한다.")
    @Test
    void contentGroupList() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        getContentAndSave(user, group);

        given(redisService.getValues(anyString()))
                .willReturn("23");

        given(redisService.isCheckAddBookmark(anyString(), anyLong()))
                .willReturn(false);

        // when
        Page<ContentResponse.GroupPage> response = contentService.groupListContent(user.getId(), group.getId(), 1);

        // then
        assertThat(response)
                .extracting("userName", "content", "views", "bookmarkAddStatus")
                .contains(
                        tuple("테스트 닉네임", "테스트 내용", 23L, false)
                );
    }

    @DisplayName("유저가 속한 전체 그룹의 피드를 페이지로 조회한다.")
    @Test
    void groupAllListContent() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        getContentAndSave(user, group);

        given(redisService.getValues(anyString()))
                .willReturn("23");

        given(redisService.isCheckAddBookmark(anyString(), anyLong()))
                .willReturn(false);

        List<Long> groups = List.of(group.getId());

        // when
        Page<ContentResponse.GroupPage> response = contentService.groupAllListContent(user.getId(), groups, 1);

        // then
        assertThat(response)
                .extracting("userName", "content", "views", "bookmarkAddStatus")
                .contains(
                        tuple("테스트 닉네임", "테스트 내용", 23L, false)
                );
    }

    @DisplayName("유저가 피드의 내용을 검색하여 조회한다.")
    @Test
    void searchContent() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        getContentAndSave(user, group);

        List<Long> groups = List.of(group.getId());

        // when
        Page<ContentResponse.Create> response = contentService.contentSearch(groups, "테스트", 1);

        // then
        assertThat(response)
                .hasSize(1)
                .extracting("userName", "content", "latitude", "longitude", "location")
                .contains(
                        tuple("테스트 닉네임", "테스트 내용", 0.0, 0.0, "삼성 서비스 센터")
                );
    }

    @DisplayName("유저가 자신이 작성한 글을 수정한다.")
    @Test
    void contentUpdate() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);

        given(redisService.getValues(anyString()))
                .willReturn("23");

        // when
        ContentResponse.Update response = contentService.updateContent(user.getId(), null, content.getId(), "하이", 2.0, 2.0, "명륜진사갈비");

        // then
        assertThat(response)
                .extracting("userName", "content", "location", "latitude", "longitude")
                .contains("테스트 닉네임", "하이", "명륜진사갈비", 2.0, 2.0);
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

    private User getUserAndSave(String email, String nickName) {
        User user = User.builder()
                .authorities(getAuthorities())
                .email(email)
                .password("abc123!")
                .name("테스트 계정")
                .nickName(nickName)
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

    private Comment getCommentAndSave(User user, Content content) {
        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .commentNote("테스트 내용")
                .build();

        return commentRepository.save(comment);
    }

}