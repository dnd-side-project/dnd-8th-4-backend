package dnd.diary.controller.content;

import dnd.diary.request.content.CommentDto;
import dnd.diary.request.content.ContentDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 피드 댓글 작성
    @PostMapping("content/{contentId}/comment")
    public CustomResponseEntity<CommentDto.AddCommentDto> addComment(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(name = "contentId") final Long contentId,
            @Valid @RequestBody final CommentDto.AddCommentDto request
    ) {
        return CustomResponseEntity.success(commentService.commentAdd(userDetails, contentId, request));
    }

    // 피드 댓글 조회
    @GetMapping("content/{contentId}/comment")
    public CustomResponseEntity<Page<CommentDto.pageCommentDto>> pageComment(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(name = "contentId") final Long contentId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(commentService.commentPage(userDetails, contentId, page));
    }

    // 피드 감정 리스트 조회
    @GetMapping("content/{contentId}/emotion")
    public CustomResponseEntity<List<ContentDto.EmotionResponseDto>> listEmotion(
            @PathVariable(name = "contentId") final Long contentId
    ){
        return CustomResponseEntity.success(commentService.emotionList(contentId));
    }
}
