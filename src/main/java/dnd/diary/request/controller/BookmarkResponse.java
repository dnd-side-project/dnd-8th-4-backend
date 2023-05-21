package dnd.diary.request.controller;

import dnd.diary.domain.bookmark.Bookmark;
import lombok.*;

@NoArgsConstructor
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BookmarkResponse {
    private Long id;
    private Long contentId;
    private Long userId;

    public static BookmarkResponse response(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .contentId(bookmark.getContent().getId())
                .userId(bookmark.getUser().getId())
                .build();
    }
}
