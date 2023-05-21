package dnd.diary.controller.content;

import dnd.diary.request.controller.BookmarkResponse;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("bookmark")
    public CustomResponseEntity<BookmarkResponse> addBookmark(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Long contentId
    ) {
        return bookmarkService.bookmarkAdd(userId, contentId);
    }
}
