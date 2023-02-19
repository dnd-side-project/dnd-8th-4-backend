package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.bookmark.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class addBookmarkDto {

        private Long id;
        private Long contentId;
        private Long userId;

        public static BookmarkDto.addBookmarkDto response(Bookmark bookmark) {
            return addBookmarkDto.builder()
                    .id(bookmark.getId())
                    .contentId(bookmark.getContent().getId())
                    .userId(bookmark.getUser().getId())
                    .build();
        }
    }
}
