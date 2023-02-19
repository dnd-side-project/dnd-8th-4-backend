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

    @PostMapping("content/{contentId}/comment")
    public CustomResponseEntity<CommentDto.AddCommentDto> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(name = "contentId") Long contentId,
            @Valid @RequestBody CommentDto.AddCommentDto request
    ){
        return commentService.commentAdd(userDetails,contentId,request);
    }
}
