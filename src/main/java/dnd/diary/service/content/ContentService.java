package dnd.diary.service.content;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentImageRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ContentImageRepository contentImageRepository;
    private final EmotionRepository emotionRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> groupAllListContent(UserDetails userDetails, List<Long> groupId, Integer page) {
        User user = getUser(userDetails);

        Page<Content> contents = contentRepository.findByGroupIdIn(
                groupId, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        Page<ContentDto.groupListPagePostsDto> collect = contents.map(
                (Content content) -> {
                    Emotion byContentIdAndUserId = emotionRepository.findByContentIdAndUserId(content.getId(), user.getId());
                    Long emotionStatus;
                    if (byContentIdAndUserId == null) {
                        emotionStatus = -1L;
                    } else {
                        emotionStatus = byContentIdAndUserId.getEmotionStatus();
                    }
                    return ContentDto.groupListPagePostsDto.response(
                            content, contentImageRepository.findByContentId(
                                    content.getId()
                            ).stream().map(ContentDto.ImageResponseDto::response).toList(),
                            commentRepository.countByContentId(content.getId()),
                            emotionRepository.countByContentId(content.getId()),
                            getEmotionResponseDtos(content.getId()),
                            emotionStatus
                    );
                }
        );
        return CustomResponseEntity.success(collect);
    }

    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> groupListContent(
            UserDetails userDetails, Long groupId, Integer page
    ) {
        User user = getUser(userDetails);

        Page<Content> contents = contentRepository.findByGroupId(
                groupId, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        Page<ContentDto.groupListPagePostsDto> collect = contents.map(
                (Content content) -> {
                    Emotion byContentIdAndUserId = emotionRepository.findByContentIdAndUserId(content.getId(), user.getId());
                    Long emotionStatus;
                    if (byContentIdAndUserId == null) {
                        emotionStatus = -1L;
                    } else {
                        emotionStatus = byContentIdAndUserId.getEmotionStatus();
                    }
                    return ContentDto.groupListPagePostsDto.response(
                            content, contentImageRepository.findByContentId(
                                    content.getId()
                            ).stream().map(ContentDto.ImageResponseDto::response).toList(),
                            commentRepository.countByContentId(content.getId()),
                            emotionRepository.countByContentId(content.getId()),
                            getEmotionResponseDtos(content.getId()),
                            emotionStatus
                    );
                }
        );
        return CustomResponseEntity.success(collect);
    }

    @Transactional
    public CustomResponseEntity<ContentDto.CreateDto> createContent(
            UserDetails userDetails, Long groupId, List<MultipartFile> multipartFile, ContentDto.CreateDto request
    ) {
        Group group = getGroup(groupId);
        Content content = contentRepository.save(
                Content.builder()
                        .content(request.getContent())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .views(0L)
                        .contentLink("test")
                        .user(getUser(userDetails))
                        .group(group)
                        .build()
        );
        group.updateRecentModifiedAt(LocalDateTime.now());

        if (multipartFile == null) {
            return CustomResponseEntity.success(
                    ContentDto.CreateDto.response(
                            content,
                            null
                    )
            );
        } else {
            uploadFiles(multipartFile, content);
            return CustomResponseEntity.success(
                    ContentDto.CreateDto.response(
                            content,
                            contentImageRepository.findByContentId(content.getId())
                                    .stream()
                                    .map(ContentDto.ImageResponseDto::response)
                                    .toList()
                    )
            );
        }
    }

    private void uploadFiles(List<MultipartFile> multipartFile, Content content) {
        multipartFile.forEach(file -> {
            String fileName = saveImage(file);
            ContentImage contentImage = ContentImage.builder()
                    .content(content)
                    .imageName(fileName)
                    .imageUrl(amazonS3Client.getUrl(bucket, fileName).toString())
                    .build();
            contentImageRepository.save(contentImage);
        });
    }

    @Transactional
    public CustomResponseEntity<ContentDto.detailDto> detailContent(Long contentId) {
        Content content = getContent(contentId);
        return CustomResponseEntity.success(
                ContentDto.detailDto.response(
                        content,
                        contentImageRepository.findByContentId(content.getId())
                                .stream()
                                .map(ContentDto.ImageResponseDto::response)
                                .toList()
                )
        );
    }

    public CustomResponseEntity<ContentDto.UpdateDto> updateContent(
            Long contentId, List<MultipartFile> multipartFile, ContentDto.UpdateDto request
    ) {
        Content content = getContent(contentId);
        if (request.getDeleteContentImageName() != null) {
            request.getDeleteContentImageName().forEach(deleteImageNameDto ->
                    deleteFile(deleteImageNameDto.getImageName())
            );
            request.getDeleteContentImageName().forEach(deleteImageNameDto ->
                    contentImageRepository.delete(contentImageRepository.findByImageName(deleteImageNameDto.getImageName())
                            .orElseThrow(
                                    () -> new CustomException(Result.FAIL)
                            )
                    )
            );
        }

        if (multipartFile != null) {
            uploadFiles(multipartFile, content);
        }

        Content updateContent = contentRepository.save(
                Content.builder()
                        .id(content.getId())
                        .content(request.getContent())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .views(content.getViews())
                        .contentLink(content.getContentLink())
                        .user(content.getUser())
                        .group(content.getGroup())
                        .build()
        );

        List<ContentImage> contentImages = contentImageRepository.findByContentId(content.getId());
        List<ContentDto.ImageResponseDto> collect = contentImages.stream().map(ContentDto.ImageResponseDto::response).toList();

        return CustomResponseEntity.success(
                ContentDto.UpdateDto.response(
                        updateContent, collect
                )
        );
    }

    public CustomResponseEntity<ContentDto.deleteContent> deleteContent(
            UserDetails userDetails, Long contentId
    ) {
        User user = getUser(userDetails);
        Content content = contentRepository.findByIdAndUserId(contentId, user.getId())
                .orElseThrow(
                        () -> new CustomException(Result.DELETE_CONTENT_FAIL)
                );
        contentRepository.delete(content);
        return CustomResponseEntity.successDeleteContent();
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

    private List<ContentDto.EmotionResponseDto> getEmotionResponseDtos(Long contentId) {
        List<Emotion> byContentId = emotionRepository.findByContentId(contentId);
        List<ContentDto.EmotionResponseDto> emotion = byContentId.stream().map(ContentDto.EmotionResponseDto::response).toList();
        return emotion;
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
        return content;
    }
}
