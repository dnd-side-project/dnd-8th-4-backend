package dnd.diary.controller.content;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.CommentLikeResponse;
import dnd.diary.service.content.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @GetMapping("content/comment/like")
    public CustomResponseEntity<CommentLikeResponse> processCommentLikeTransaction(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Long commentId
    ) {
        return CustomResponseEntity.success(commentLikeService.processCommentLikeTransaction(userId, commentId));
    }
}
