package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;

import dnd.diary.domain.sticker.Sticker;
import dnd.diary.domain.user.User;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.CommentLikeRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.mission.StickerRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.service.content.CommentServiceRequest;
import dnd.diary.response.content.CommentResponse;
import dnd.diary.service.group.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse.Add commentAdd(
            Long userId, Long contentId, CommentServiceRequest.Add request
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
            .user(getUser(userId))
            .content(getContent(contentId))
            .sticker(sticker)
            .deletedYn(false)
            .build();
        commentRepository.save(comment);

        // 자신을 제외한 게시물 생성자에게 알림 생성
        notificationService.sendToNotification(contentId, comment);

        return CommentResponse.Add.response(comment);

    }

    @Transactional(readOnly = true)
    public Page<CommentResponse.Detail> commentPage(
            Long userId, Long contentId, Integer page
    ) {
        validateCommentPage(contentId);
        Page<Comment> comments = commentRepository.findByContentIdAndDeletedYn(
                contentId, false, PageRequest.of(page - 1, 10, Sort.Direction.ASC, "createdAt")
        );

        return getPageCommentsResponse(userId, comments);
    }

    // method
    private Page<CommentResponse.Detail> getPageCommentsResponse(Long userId, Page<Comment> comments) {
        return comments.map((Comment comment) -> CommentResponse.Detail.response(
                        comment,
                        commentLikeRepository.existsByCommentIdAndUserIdAndCommentLikeYn(
                            comment.getId(), getUser(userId).getId(), true
                        )
                )
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
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
}
