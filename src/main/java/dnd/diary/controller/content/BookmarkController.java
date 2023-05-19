package dnd.diary.controller.content;

import dnd.diary.request.content.BookmarkDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("bookmark")
    public CustomResponseEntity<BookmarkDto.addBookmarkDto> addBookmark(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long contentId
    ) {
        return bookmarkService.bookmarkAdd(userDetails, contentId);
    }
}
