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
import dnd.diary.repository.UserRepository;
import dnd.diary.repository.content.ContentImageRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.content.EmotionRepository;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ContentImageRepository contentImageRepository;
    private final EmotionRepository emotionRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public CustomResponseEntity<ContentDto.CreateDto> createContent(
            UserDetails userDetails,
            Long groupId,
            List<MultipartFile> multipartFile,
            ContentDto.CreateDto request
    ) {
        List<String> fileNameList = new ArrayList<>();

        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        Group group = groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        Content content = contentRepository.save(
                Content.builder()
                        .content(request.getContent())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .views(0L)
                        .contentLink("test")
                        .user(user)
                        .group(group)
                        .build()
        );

        group.updateRecentModifiedAt(LocalDateTime.now());

        multipartFile.forEach(file -> {
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

            fileNameList.add(fileName);

            ContentImage contentImage = ContentImage.builder()
                    .content(content)
                    .imageUrl(amazonS3Client.getUrl(bucket, fileName).toString())
                    .build();

            contentImageRepository.save(contentImage);
        });

        List<ContentImage> contentImages = contentImageRepository.findByContentId(content.getId());
        List<ContentDto.ImageResponseDto> collect = contentImages.stream().map(ContentDto.ImageResponseDto::response).toList();


        return CustomResponseEntity.success(
                ContentDto.CreateDto.response(content, collect)
        );
    }

    public CustomResponseEntity<ContentDto.detailDto> detailContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        List<ContentImage> contentImages = contentImageRepository.findByContentId(content.getId());
        List<ContentDto.ImageResponseDto> collect = contentImages.stream().map(ContentDto.ImageResponseDto::response).toList();

        return CustomResponseEntity.success(
                ContentDto.detailDto.response(
                        content, collect
                )
        );
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
}
