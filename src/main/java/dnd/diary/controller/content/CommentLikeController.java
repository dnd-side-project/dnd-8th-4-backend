package dnd.diary.controller.content;

import dnd.diary.dto.content.CommentLikeDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @GetMapping("content/comment/like")
    public CustomResponseEntity<CommentLikeDto.SaveCommentLike> likeSaveComment(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long commentId
    ) {
        return commentLikeService.commentLikeSave(userDetails, commentId);
    }
}
