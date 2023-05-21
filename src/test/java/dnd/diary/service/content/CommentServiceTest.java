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
import dnd.diary.request.controller.comment.CommentRequest;
import dnd.diary.request.service.CommentServiceRequest;
import dnd.diary.response.content.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @DisplayName("유저가 상대방의 피드에 댓글을 작성한다.")
    @Test
    void commentAdd() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);

        CommentRequest.Add request = new CommentRequest.Add("테스트 내용", null);

        // when
        CommentResponse.Add response =
                commentService.commentAdd(user.getId(), content.getId(), request.toServiceRequest());

        // then
        assertThat(response.getCommentNote()).isEqualTo("테스트 내용");
        assertThat(response.getStickerId()).isNull();
    }

    @DisplayName("유저가 피드의 댓글을 페이지 조회한다.")
    @Test
    void commentPage() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Comment comment = getCommentAndSave(user, content);

        // when
        Page<CommentResponse.Detail> response = commentService.commentPage(user.getId(), content.getId(), 1);

        // then
        assertThat(response).hasSize(1)
                .extracting("commentNote","userName")
                .contains(
                        tuple("테스트 댓글","테스트 닉네임")
                );
    }

    private Comment getCommentAndSave(User user, Content content) {
        Comment comment = Comment.builder()
                .commentNote("테스트 댓글")
                .user(user)
                .content(content)
                .sticker(null)
                .commentLikes(null)
                .build();
        return commentRepository.save(comment);
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