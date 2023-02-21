package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.content.Emotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class ContentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class groupListPagePostsDto {

        private Long id;
        private Long userId;
        private Long groupId;
        private String userName;
        private String groupName;
        private String content;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createAt;
        private long views;
        private String contentLink;
        private Long comments;
        private Long emotions;
        private Long emotionStatus;
        List<ContentDto.ImageResponseDto> Images;
        List<EmotionResponseDto> emotionResponseDtos;

        public static ContentDto.groupListPagePostsDto response(
                Content content, List<ImageResponseDto> imageResponseDtos,
                Long comments, Long emotions, List<EmotionResponseDto> emotionResponseDtos,
                Long emotionStatus, Integer views
        ) {
            return groupListPagePostsDto.builder()
                    .id(content.getId())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .userName(content.getUser().getNickName())
                    .groupName(content.getGroup().getGroupName())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .createAt(content.getCreatedAt())
                    .views(views)
                    .contentLink(content.getContentLink())
                    .comments(comments)
                    .emotions(emotions)
                    .emotionStatus(emotionStatus)
                    .Images(imageResponseDtos)
                    .emotionResponseDtos(emotionResponseDtos)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class ImageResponseDto {
        private Long id;
        private String imageName;
        private String imageUrl;
        private Long contentId;

        public static ContentDto.ImageResponseDto response(ContentImage contentImage) {
            return ImageResponseDto.builder()
                    .id(contentImage.getId())
                    .imageUrl(contentImage.getImageUrl())
                    .imageName(contentImage.getImageName())
                    .contentId(contentImage.getContent().getId())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class EmotionResponseDto {
        private Long id;
        private Long emotionStatus;
        private String profileImage;
        private Long userId;

        public static ContentDto.EmotionResponseDto response(Emotion emotion) {
            return EmotionResponseDto.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .profileImage("이미지 공사중")
                    .userId(emotion.getUser().getId())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class CreateDto {

        private Long id;
        @NotNull(message = "내용을 입력하지 않았습니다.")
        private String content;
        private Double latitude;
        private Double longitude;
        private long views;
        private String contentLink;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;

        public static ContentDto.CreateDto response(Content content, List<ContentDto.ImageResponseDto> collect) {
            return CreateDto.builder()
                    .id(content.getId())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .views(content.getViews())
                    .contentLink(content.getContentLink())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class detailDto {

        private Long id;
        private String content;
        private Double latitude;
        private Double longitude;
        private long views;
        private String contentLink;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;


        public static ContentDto.detailDto response(Content content, Integer views, List<ContentDto.ImageResponseDto> collect) {
            return detailDto.builder()
                    .id(content.getId())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .views(views)
                    .contentLink(content.getContentLink())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class UpdateDto {

        private Long id;
        @NotNull(message = "내용을 입력하지 않았습니다.")
        private String content;
        private Double latitude;
        private Double longitude;
        private long views;
        private String contentLink;
        private Long userId;
        private Long groupId;
        private List<deleteImageNameDto> deleteContentImageName;
        List<ContentDto.ImageResponseDto> collect;

        public static ContentDto.UpdateDto response(Content content, List<ContentDto.ImageResponseDto> collect, Integer views) {
            return UpdateDto.builder()
                    .id(content.getId())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .views(views)
                    .contentLink(content.getContentLink())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class deleteImageNameDto {
        private String imageName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class deleteContent {
        private Long contentid;
    }
}
