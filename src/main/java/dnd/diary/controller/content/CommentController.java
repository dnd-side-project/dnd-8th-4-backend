package dnd.diary.controller.content;

import dnd.diary.dto.content.CommentDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        return commentService.commentAdd(userDetails, contentId, request);
    }

    // 피드 댓글 조회
    @GetMapping("content/{contentId}/comment")
    public CustomResponseEntity<CommentDto.pagePostsCommentDto> pageComment(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(name = "contentId") final Long contentId,
            @RequestParam final Integer page
    ) {
        return commentService.commentPage(userDetails, contentId, page);
    }
}
