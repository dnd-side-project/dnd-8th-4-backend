package dnd.diary.service.content;

import dnd.diary.config.redis.RedisDao;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.request.controller.BookmarkResponse;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RedisDao redisDao;

    @Transactional
    @CacheEvict(value = "Contents", key = "#contentId", cacheManager = "testCacheManager")
    public CustomResponseEntity<BookmarkResponse> bookmarkAdd(
            Long userId, Long contentId
    ) {
        User user = getUser(userId);
        Bookmark bookmark = bookmarkRepository.findByUserIdAndContentId(
                user.getId(), contentId
        );

        // 이미 북마크를 등록했다면 북마크 취소
        if (bookmark != null) {
            bookmarkRepository.delete(bookmark);
            return CustomResponseEntity.successDeleteBookmark();
        }

        // 북마크 추가
        return CustomResponseEntity.success(
                BookmarkResponse.response(
                        bookmarkRepository.save(
                                Bookmark.builder()
                                        .content(getContent(contentId))
                                        .user(user)
                                        .build()
                        )
                )
        );

    }

    // method
    private Content getContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
        return content;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
    }
}
