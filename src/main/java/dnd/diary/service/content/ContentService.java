package dnd.diary.service.content;

import dnd.diary.config.redis.RedisDao;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.ContentImageRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.service.redis.RedisService;
import dnd.diary.service.s3.S3Service;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final UserService userService;
    private final RedisService redisService;
    private final RedisDao redisDao;
    private final S3Service s3Service;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;
    private final ContentImageRepository contentImageRepository;
    private final EmotionRepository emotionRepository;

    @Transactional
    public ContentResponse.Create createContent(
            Long userId, List<MultipartFile> multipartFile, Long groupId,
            String contentNote, Double latitude, Double longitude, String location
    ) {
        User user = userService.getUser(userId);
        Group group = getGroup(groupId);
        Content content = contentRepository.save(
                contentToEntity(contentNote, latitude, longitude, location, user, group)
        );

        if (multipartFile != null) {
            List<ContentImage> contentImages = s3Service.uploadFiles(multipartFile, content);
            content.updateContentImages(contentImages);
        }

        group.updateRecentModifiedAt();
        redisService.setValues(content.getId().toString(), "0");

        return ContentResponse.Create.response(content);
    }

    @Transactional
    public ContentResponse.Detail detailContent(Long userId, Long contentId) {
        Content content = getContent(contentId);
        User user = userService.getUser(userId);

        int views = redisService.getViewsAndRedisSave(contentId, user.getNickName());
        boolean isBookmarked = isCheckBookmark(contentId, user);
        Emotion myEmotionOnContent = isCheckMyEmotionAddContent(content, user);

        Long emotionStatus = (myEmotionOnContent == null) ?
                -1 : myEmotionOnContent.getEmotionStatus();

        return ContentResponse.Detail.response(
                content,
                views,
                getContentImageResponse(content),
                isBookmarked,
                emotionStatus
        );
    }

    @Transactional
    @CacheEvict(value = "Contents", key = "#contentId", cacheManager = "testCacheManager")
    public ContentResponse.Update updateContent(
            Long userId, List<MultipartFile> multipartFile, Long contentId,
            String contentNote, Double latitude, Double longitude, String location
    ) {
        validateUpdateContent(contentId);
        Content content = existsContentAndUser(contentId, userId);

        // 이미 삭제 처리된 게시물일 경우
        if (content.isDeletedYn()) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }

        List<String> imageNameList = contentImageRepository.findImageNameList(contentId);
        List<ContentImage> contentImages = deleteAndSaveContentImage(multipartFile, imageNameList, content);
        content.updateContent(contentNote, latitude, longitude, location, contentImages);

        List<ContentResponse.ImageDetail> imageList = getContentImageResponse(content);
        int views = Integer.parseInt(redisService.getValues(content.getId().toString()));

        return ContentResponse.Update.response(
                content,
                views,
                imageList
        );
    }

    @Transactional
    public Boolean deleteContent(
            Long userId, Long contentId
    ) {
        Content content = existsContentAndUser(contentId, userId);
        content.deleteContent();   // 게시물 삭제 시 상태값만 변경
        return true;
    }

    @Transactional
    public Page<ContentResponse.GroupPage> groupListContent(
            Long userId, Long groupId, Integer page
    ) {
        validateGroupListContent(groupId);

        Page<Content> contents = contentRepository.findByGroupIdAndDeletedYn(
                groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return getMyGroupPages(userId, contents);
    }

    @Transactional
    public Page<ContentResponse.GroupPage> groupAllListContent(
            Long userId, List<Long> groupId, Integer page
    ) {
        validateGroupAllListContent(groupId);

        Page<Content> contents = contentRepository.findByGroupIdInAndDeletedYn(
                groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return getMyGroupPages(userId, contents);
    }

    @Transactional
    public List<ContentResponse.LocationSearch> listMyMap(
            Long userId, Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude
    ) {
        User user = userService.getUser(userId);

        List<Long> myGroupIdList = user.getUserJoinGroups().stream()
                .map(userJoinGroup -> userJoinGroup.getGroup().getId()).toList();

        List<Content> myMapContents = contentRepository.findByMapList(
                myGroupIdList, endLatitude, startLatitude, startLongitude, endLongitude);

        return myMapContents.stream()
                .filter(content -> !content.isDeletedYn())
                .map((Content content) -> ContentResponse.LocationSearch.response(
                                content,
                                getContentImageResponse(content),
                                isCountDuplicateLocation(myGroupIdList, content)
                        )
                ).toList();
    }

    @Transactional
    public List<ContentResponse.LocationDetail> listDetailMyMap(String location, Long userId) {
        User user = userService.getUser(userId);
        List<Long> myGroupIdList = user.getUserJoinGroups().stream()
                .map(userJoinGroup -> userJoinGroup.getGroup().getId()).toList();

        List<Content> contentList = contentRepository.findByLocationAndGroupIdInAndDeletedYn(location, myGroupIdList, false);

        return contentList.stream()
                .filter(content -> !content.isDeletedYn())   // 삭제 처리되지 않은 게시물만 조회
                .map((Content content) ->
                        ContentResponse.LocationDetail.response(
                                content,
                                getContentImageResponse(content)
                        )
                ).toList();
    }

    @Transactional
    public Page<ContentResponse.Create> contentSearch(
            List<Long> groupId, String word, Integer page
    ) {
        // 삭제 처리되지 않은 게시물만 조회
        Page<Content> contentPage = contentRepository
                .findByContentContainingAndGroupIdInAndDeletedYn(
                        word, groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
                );

        return contentPage.map(ContentResponse.Create::response);
    }


    // method
    private List<ContentResponse.ImageDetail> getContentImageResponse(Content content) {
        return content.getContentImages()
                .stream()
                .map(ContentResponse.ImageDetail::response)
                .toList();
    }

    private Content existsContentAndUser(Long contentId, Long userId) {
        return contentRepository.findByIdAndUserIdAndDeletedYn(contentId, userId, false)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_MATCHED_USER_CONTENT)
                );
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                );
    }

    private Content getContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_CONTENT)
                );
        // 이미 삭제된 게시물일 경우
        if (content.isDeletedYn()) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
        return content;
    }

    private List<ContentImage> deleteAndSaveContentImage(List<MultipartFile> multipartFile, List<String> deleteContentImageName, Content content) {
        if (deleteContentImageName != null) {
            deleteContentImageName.forEach(s3Service::deleteFile);
            deleteContentImageName.forEach(imageName ->
                    contentImageRepository.delete(contentImageRepository.findByImageName(imageName)
                            .orElseThrow(
                                    () -> new CustomException(Result.FAIL)
                            )
                    )
            );
        }

        if (multipartFile != null) {
            return s3Service.uploadFiles(multipartFile, content);
        }
        return null;
    }


    // validate
    private void validateUpdateContent(Long contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }
    }

    private void validateGroupAllListContent(List<Long> groupId) {
        groupId.forEach(
                id -> groupRepository.findById(id).orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                )
        );
    }

    private void validateGroupListContent(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new CustomException(Result.NOT_FOUND_GROUP);
        }
    }

    private static Content contentToEntity(String contentNote, Double latitude, Double longitude, String location, User user, Group group) {
        return Content.builder()
                .content(contentNote)
                .latitude(latitude)
                .longitude(longitude)
                .location(location)
                .views(0L)
                .contentLink("test")
                .deletedYn(false)
                .user(user)
                .group(group)
                .build();
    }

    private static Emotion isCheckMyEmotionAddContent(Content content, User user) {
        return content.getEmotions().stream()
                .filter(emotion -> emotion.getUser() == user)
                .filter(Emotion::isEmotionYn)
                .findFirst()
                .orElse(null);
    }

    private static boolean isCheckBookmark(Long contentId, User user) {
        return user.getBookmarks()
                .stream()
                .map(bookmark -> bookmark.getContent().getId())
                .anyMatch(x -> x.equals(contentId));
    }

    private Page<ContentResponse.GroupPage> getMyGroupPages(Long userId, Page<Content> contents) {
        User user = userService.getUser(userId);

        return contents.map(
                (Content content) -> {
                    Long emotionStatus = isCheckAddEmotionAndGetStatus(user, content);
                    Boolean myBookmarkStatus = redisService.isCheckAddBookmark(user.getEmail(), content.getId());
                    String views = redisService.getValues(content.getId().toString());

                    return ContentResponse.GroupPage.response(
                            content,
                            emotionStatus,
                            views,
                            myBookmarkStatus
                    );
                }
        );
    }

    private Long isCheckAddEmotionAndGetStatus(User user, Content content) {
        Optional<Emotion> emotionOptional = emotionRepository.findByContentIdAndUserIdAndEmotionYn(
                content.getId(), user.getId(), true
        );

        return (emotionOptional.isEmpty()) ? -1 : emotionOptional.get().getEmotionStatus();
    }

    private Long isCountDuplicateLocation(List<Long> myGroupIdList, Content content) {
        return contentRepository.countByLocationAndGroupIdInAndDeletedYn(
                content.getLocation(), myGroupIdList, false
        );
    }
}
