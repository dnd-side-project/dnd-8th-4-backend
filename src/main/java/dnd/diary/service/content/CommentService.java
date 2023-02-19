package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.CommentDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.UserRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final EmotionRepository emotionRepository;

    public CustomResponseEntity<CommentDto.AddCommentDto> commentAdd(
            UserDetails userDetails, Long contentId, CommentDto.AddCommentDto request
    ) {
        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        return CustomResponseEntity.success(
                CommentDto.AddCommentDto.response(
                        commentRepository.save(
                                Comment.builder()
                                        .commentNote(request.getCommentNote())
                                        .user(user)
                                        .content(content)
                                        .sticker(null)
                                        .build()
                        )
                )
        );
    }

    public CustomResponseEntity<CommentDto.pagePostsCommentDto> commentPage(
            UserDetails userDetails, Long contentId, Integer page
    ) {
        long commentCount = commentRepository.countByContentId(contentId);
        long emotionCount = emotionRepository.countByContentId(contentId);
        Page<Comment> comments = commentRepository.findAll(PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt"));
        Page<CommentDto.pageCommentDto> collect;
        collect = comments.map(
                (Comment comment) -> CommentDto.pageCommentDto.response(
                        comment
                )
        );
        return CustomResponseEntity.success(
                CommentDto.pagePostsCommentDto.response(
                        collect,emotionCount,commentCount
                )
        );
    }
}
