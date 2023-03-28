package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.content.Content;

import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;

import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.CommentDto;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.CommentLikeRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final UserRepository userRepository;
    private final StickerRepository stickerRepository;
    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final EmotionRepository emotionRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public CommentDto.AddCommentDto commentAdd(
            UserDetails userDetails, Long contentId, CommentDto.AddCommentDto request
    ) {
        validateCommentAdd(contentId);

        Sticker sticker = null;

        if (request.getStickerId() != null) {
            sticker = stickerRepository.findById(request.getStickerId())
                    .orElseThrow(
                            () -> new CustomException(Result.FAIL)
                    );
        }

        Comment comment = Comment.builder()
            .commentNote(request.getCommentNote())
            .user(getUser(userDetails))
            .content(getContent(contentId))
            .sticker(sticker)
            .deletedYn(false)
            .build();

        commentRepository.save(comment);

        // 자신을 제외한 게시물 생성자에게 알림 생성
        Content content = getContent(contentId);
        if (!comment.getUser().getId().equals(content.getUser().getId())) {
            Notification notification = Notification.toContentCommentEntity(
                    content, comment, content.getUser(), NotificationType.CONTENT_COMMENT);
            notificationRepository.save(notification);
        }

        return CommentDto.AddCommentDto.response(comment);

    }

    @Transactional
    public Page<CommentDto.pageCommentDto> commentPage(
            UserDetails userDetails, Long contentId, Integer page
    ) {
        validateCommentPage(contentId);
        return getPageCommentDtos(userDetails, getPageComments(contentId, page));
    }

    // method
    private Page<Comment> getPageComments(Long contentId, Integer page) {
        return commentRepository.findByContentIdAndDeletedYn(
                contentId, false, PageRequest.of(page - 1, 10, Sort.Direction.ASC, "createdAt")
        );
    }

    private Page<CommentDto.pageCommentDto> getPageCommentDtos(UserDetails userDetails, Page<Comment> comments) {
        return comments.map((Comment comment) -> CommentDto.pageCommentDto.response(
                        comment,
                        commentLikeRepository.existsByCommentIdAndUserIdAndCommentLikeYn(
                            comment.getId(), getUser(userDetails).getId(), true
                        ),
                        // commentLikeRepository.existsByCommentIdAndUserId(
                        //         comment.getId(), getUser(userDetails).getId()
                        // ),
                        getUser(userDetails).getId()
                )
        );
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_USER)
                );
    }

    private Content getContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_CONTENT)
                );
        // 이미 삭제된 게시물일 경우
        if (content.isDeletedYn()) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
        return content;
    }

    // validate
    private void validateCommentPage(Long contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
    }

    private void validateCommentAdd(Long contentId) {
        if (!contentRepository.existsByIdAndDeletedYn(contentId, false)) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
    }

    public List<ContentDto.EmotionResponseDto> emotionList(Long contentId) {
        return emotionRepository.findByContentId(contentId)
            .stream()
            .filter(Emotion::isEmotionYn)
            .map(ContentDto.EmotionResponseDto::response)
            .toList();
    }
}
