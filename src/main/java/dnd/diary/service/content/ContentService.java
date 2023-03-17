package dnd.diary.service.content;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dnd.diary.config.GeometryUtil;
import dnd.diary.config.Location;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.enumeration.Direction;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.ContentImageRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.UserJoinGroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final RedisDao redisDao;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ContentImageRepository contentImageRepository;
    private final EmotionRepository emotionRepository;
    private final UserJoinGroupRepository userJoinGroupRepository;
    private final AmazonS3Client amazonS3Client;
    private final EntityManager em;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public Page<ContentDto.groupListPagePostsDto> groupListContent(
            UserDetails userDetails, Long groupId, Integer page
    ) {
        validateGroupListContent(groupId);

        Page<Content> contents = contentRepository.findByGroupIdAndDeletedYn(
                groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );
        return contents.map(
                (Content content) -> {
                    Emotion findEmotionStatus = emotionRepository.findByContentIdAndUserId(content.getId(), getUser(userDetails).getId());
                    Long emotionStatus = findEmotionStatus == null ? -1 : findEmotionStatus.getEmotionStatus();
                    Boolean myBookmarkStatus = redisDao.getValuesList("bookmark" + userDetails.getUsername())
                            .contains(content.getId().toString());
                    String views = redisDao.getValues(content.getId().toString());

                    return ContentDto.groupListPagePostsDto.response(
                            content,
                            emotionStatus,
                            views,
                            myBookmarkStatus
                    );
                }
        );
    }

    @Transactional
    public Page<ContentDto.groupListPagePostsDto> groupAllListContent(
            UserDetails userDetails, List<Long> groupId, Integer page
    ) {
        validateGroupAllListContent(groupId);

        Page<Content> contents = contentRepository.findByGroupIdInAndDeletedYn(
                groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return contents.map(
                (Content content) -> {
                    Emotion findEmotionStatus = emotionRepository.findByContentIdAndUserId(content.getId(), getUser(userDetails).getId());
                    Long emotionStatus = findEmotionStatus == null ? -1 : findEmotionStatus.getEmotionStatus();
                    Boolean myBookmarkStatus = redisDao.getValuesList("bookmark" + userDetails.getUsername())
                            .contains(content.getId().toString());
                    String views = redisDao.getValues(content.getId().toString());

                    return ContentDto.groupListPagePostsDto.response(
                            content,
                            emotionStatus,
                            views,
                            myBookmarkStatus
                    );
                }
        );
    }

    @Transactional
    public ContentDto.CreateDto createContent(
            UserDetails userDetails, List<MultipartFile> multipartFile, Long groupId,
            String contentNote, Double latitude, Double longitude, String location
    ) throws ParseException {

        Group group = getGroup(groupId);
        Point point =
                latitude != null && longitude != null ?
                        (Point) new WKTReader().read(String.format("POINT(%s %s)", latitude, longitude))
                        : null;
        Content content = contentRepository.save(
                Content.builder()
                        .content(contentNote)// 이미 삭제된 게시물일 경우
                        .latitude(latitude)
                        .longitude(longitude)
                        .point(point)
                        .location(location)
                        .views(0L)
                        .contentLink("test")
                        .deletedYn(false)
                        .user(getUser(userDetails))
                        .group(group)
                        .build()
        );

        if (multipartFile != null) {
            content.updateContentImages(uploadFiles(multipartFile, content));
        }

        group.updateRecentModifiedAt();
        redisDao.setValues(content.getId().toString(), "0");

        return ContentDto.CreateDto.response(content);
    }

    @Transactional
    @Cacheable(value = "Contents", key = "#contentId", cacheManager = "testCacheManager")
    public ContentDto.detailDto detailContent(UserDetails userDetails, Long contentId) {
        Content content = getContent(contentId);

        String redisKey = contentId.toString();
        String redisUserKey = getUser(userDetails).getNickName();
        String values = redisDao.getValues(redisKey);
        int views = Integer.parseInt(values);

        if (!redisDao.getValuesList(redisUserKey).contains(redisKey)) {
            redisDao.setValuesList(redisUserKey, redisKey);
            views = Integer.parseInt(values) + 1;
            redisDao.setValues(redisKey, String.valueOf(views));
        }

        List<String> bookmarkStatusList = redisDao.getValuesList("bookmark" + userDetails.getUsername());
        boolean bookmarkAddStatus = bookmarkStatusList.contains(contentId.toString());

        Emotion byContentIdAndUserId = emotionRepository.findByContentIdAndUserId(content.getId(), getUser(userDetails).getId());
        Long emotionStatus;
        if (byContentIdAndUserId == null) {
            emotionStatus = -1L;
        } else {
            emotionStatus = byContentIdAndUserId.getEmotionStatus();
        }

        return ContentDto.detailDto.response(
                content,
                views,
                getContentImageResponse(content),
                bookmarkAddStatus,
                emotionStatus
        );
    }

    @Transactional
    @CacheEvict(value = "Contents", key = "#contentId", cacheManager = "testCacheManager")
    public ContentDto.UpdateDto updateContent(
            UserDetails userDetails, List<MultipartFile> multipartFile, Long contentId,
            String contentNote, Double latitude, Double longitude, String location
    ) {
        validateUpdateContent(contentId);
        Content content = existsContentAndUser(
                contentId, getUser(userDetails).getId()
        );

        // 이미 삭제 처리된 게시물일 경우
        if (content.isDeletedYn()) {
            throw new CustomException(Result.NOT_FOUND_CONTENT);
        }

        List<ContentImage> contentImages = deleteAndSaveContentImage(
                multipartFile, contentImageRepository.findImageNameList(contentId), content
        );

        content.updateContent(contentNote, latitude, longitude, location, contentImages);

        String redisKey = content.getId().toString();
        return ContentDto.UpdateDto.response(
                content,
                Integer.parseInt(redisDao.getValues(redisKey))
        );
    }

    @Transactional
    public CustomResponseEntity<ContentDto.deleteContent> deleteContent(
            UserDetails userDetails, Long contentId
    ) {
//        contentRepository.delete(
//                existsContentAndUser(contentId, getUser(userDetails).getId())
//        );
        Content content = existsContentAndUser(contentId, getUser(userDetails).getId());
        content.deleteContent();   // 게시물 삭제 시 상태값만 변경
        return CustomResponseEntity.successDeleteContent();
    }

    @Transactional
    public List<ContentDto.mapListContent> listMyMap(UserDetails userDetails, Double x, Double y) {
        Location northEast = GeometryUtil.calculate(x, y, 2.0, Direction.NORTHEAST.getBearing());
        Location southWest = GeometryUtil.calculate(x, y, 2.0, Direction.SOUTHWEST.getBearing());

        String pointFormat = String.format(
                "'LINESTRING(%f %f, %f %f)'",
                northEast.getLatitude(), northEast.getLongitude(), southWest.getLatitude(), southWest.getLongitude()
        );

        List<Long> groupIdList = userJoinGroupRepository.findGroupIdList(getUser(userDetails).getId());

        String join = String.join(
                ",", groupIdList.stream().map(Object::toString).toList()
        );

        Query query = em.createNativeQuery(
                "" +
                        "SELECT * \n" +
                        "FROM content AS c \n" +
                        "WHERE c.group_id IN (" + join + ") " +
                        "AND " +
                        "MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + "), c.point)"
                , Content.class
        ).setMaxResults(10);


        List<Content> contents = query.getResultList();

        return contents.stream()
                .filter(content -> !content.isDeletedYn())   // 삭제 처리되지 않은 게시물만 조회
                .map((Content content) ->
                ContentDto.mapListContent.response(
                        content,
                        getContentImageResponse(content),
                        contentRepository.countByLocationAndGroupIdInAndDeletedYn(content.getLocation(), groupIdList, false)
                )
        ).toList();
    }

    @Transactional
    public List<ContentDto.mapListContentDetail> listDetailMyMap(String location, UserDetails userDetails) {
        List<Long> groupId = userJoinGroupRepository.findGroupIdList(getUser(userDetails).getId());
        List<Content> contentList = contentRepository.findByLocationAndGroupIdInAndDeletedYn(location, groupId, false);
        return contentList.stream()
                .filter(content -> !content.isDeletedYn())   // 삭제 처리되지 않은 게시물만 조회
                .map((Content content) ->
                ContentDto.mapListContentDetail.response(
                        content,
                        getContentImageResponse(content)
                )
        ).toList();
    }

    @Transactional
    public CustomResponseEntity<Page<ContentDto.ContentSearchDto>> contentSearch(
            List<Long> groupId, String word, Integer page
    ) {
        // 삭제 처리되지 않은 게시물만 조회
        Page<Content> contentPage = contentRepository
                .findByContentContainingAndGroupIdInAndDeletedYn(
                        word, groupId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
                );

        return CustomResponseEntity.success(
                contentPage.map((Content content) -> ContentDto.ContentSearchDto.response(
                                content,
                                getContentImageResponse(content)
                        )
                )
        );
    }


    // method
    private List<ContentDto.ImageResponseDto> getContentImageResponse(Content content) {
        return content.getContentImages()
                .stream()
                .map(ContentDto.ImageResponseDto::response)
                .toList();
    }

    private List<ContentImage> uploadFiles(List<MultipartFile> multipartFile, Content content) {
        List<ContentImage> contentImages = new ArrayList<>();

        multipartFile.forEach(file -> {
            String fileName = saveImage(file);
            ContentImage contentSaveImage = contentImageRepository.save(
                    ContentImage.builder()
                            .content(content)
                            .imageName(fileName)
                            .imageUrl(amazonS3Client.getUrl(bucket, fileName).toString())
                            .build()
            );
            contentImages.add(contentSaveImage);
        });
        return contentImages;
    }

    private Content existsContentAndUser(Long contentId, Long userId) {
        return contentRepository.findByIdAndUserIdAndDeletedYn(contentId, userId, false)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_MATCHED_USER_CONTENT)
                );
    }

    private String saveImage(MultipartFile file) {
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        return fileName;
    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                );
    }

    private User getUser(UserDetails userDetails) {
        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );
        return user;
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
            deleteContentImageName.forEach(this::deleteFile);
            deleteContentImageName.forEach(imageName ->
                    contentImageRepository.delete(contentImageRepository.findByImageName(imageName)
                            .orElseThrow(
                                    () -> new CustomException(Result.FAIL)
                            )
                    )
            );
        }

        if (multipartFile != null) {
            List<ContentImage> contentImages = uploadFiles(multipartFile, content);
            return contentImages;
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
}
