package dnd.diary.controller.content;

import dnd.diary.request.controller.content.CommentRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.CommentResponse;
import dnd.diary.service.content.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 피드 댓글 작성
    @PostMapping("content/{contentId}/comment")
    public CustomResponseEntity<CommentResponse.Add> addComment(
            @AuthenticationPrincipal final Long userId,
            @PathVariable(name = "contentId") final Long contentId,
            @Valid @RequestBody final CommentRequest.Add request
    ) {
        return CustomResponseEntity.success(commentService.commentAdd(userId, contentId, request.toServiceRequest()));
    }

    // 피드 댓글 조회
    @GetMapping("content/{contentId}/comment")
    public CustomResponseEntity<Page<CommentResponse.Detail>> pageComment(
            @AuthenticationPrincipal final Long userId,
            @PathVariable(name = "contentId") final Long contentId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(commentService.commentPage(userId, contentId, page));
    }
}
