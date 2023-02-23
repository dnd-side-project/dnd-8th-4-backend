package dnd.diary.service.content;

import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.BookmarkDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public CustomResponseEntity<BookmarkDto.addBookmarkDto> bookmarkAdd(
            UserDetails userDetails, Long contentId
    ) {
        Bookmark bookmark = bookmarkRepository.existsByUserIdAndContentId(
                getUser(userDetails).getId(), contentId
        );
        if (bookmark != null){
            bookmarkRepository.delete(bookmark);
            return CustomResponseEntity.successDeleteBookmark();
        } else {
            return CustomResponseEntity.success(
                    BookmarkDto.addBookmarkDto.response(
                            bookmarkRepository.save(
                                    Bookmark.builder()
                                            .content(getContent(contentId))
                                            .user(getUser(userDetails))
                                            .build()
                            )
                    )
            );
        }
    }

    // method
    private Content getContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
        return content;
    }

    private User getUser(UserDetails userDetails) {
        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
        return user;
    }
}
