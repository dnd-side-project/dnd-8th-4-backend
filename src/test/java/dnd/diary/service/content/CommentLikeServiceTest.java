package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.repository.content.CommentLikeRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.CommentLikeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentLikeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentLikeService commentLikeService;

    @DisplayName("유저가 해당 댓글에 최초로 좋아요를 남긴다.")
    @Test
    void processCommentLikeTransactionToAdd() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Comment comment = getCommentAndSave(user, content);

        // when
        CustomResponseEntity<CommentLikeResponse> response =
                commentLikeService.processCommentLikeTransaction(user.getId(), comment.getId());

        // then
        Long commentLikeId = response.getData().getId();
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(commentLikeId);

        assertThat(commentLikeOptional.isPresent()).isTrue();
        assertThat(commentLikeOptional.get().isCommentLikeYn()).isTrue();
    }

    @DisplayName("유저가 해당 댓글에 남긴 좋아요를 취소한다.")
    @Test
    void processCommentLikeTransactionToCancel() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Comment comment = getCommentAndSave(user, content);
        CommentLike commentLike = commentLikeRepository.save(CommentLike.builder()
                .user(user)
                .comment(comment)
                .commentLikeYn(true)
                .build()
        );

        // when
        commentLikeService.processCommentLikeTransaction(user.getId(), comment.getId());

        // then
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(commentLike.getId());

        assertThat(commentLikeOptional.isPresent()).isTrue();
        assertThat(commentLikeOptional.get().isCommentLikeYn()).isFalse();
    }

    @DisplayName("유저가 해당 댓글을 취소했던 상태에서 좋아요를 다시 남긴다.")
    @Test
    void processCommentLikeTransactionToCancelWillAdd() {
        // given
        User user = getUserAndSave();
        Group group = getGroupSave(user);
        Content content = getContentAndSave(user, group);
        Comment comment = getCommentAndSave(user, content);
        CommentLike commentLike = commentLikeRepository.save(CommentLike.builder()
                .user(user)
                .comment(comment)
                .commentLikeYn(false)
                .build()
        );

        // when
        commentLikeService.processCommentLikeTransaction(user.getId(), comment.getId());

        // then
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(commentLike.getId());

        assertThat(commentLikeOptional.isPresent()).isTrue();
        assertThat(commentLikeOptional.get().isCommentLikeYn()).isTrue();
    }

    // method

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