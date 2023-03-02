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
import java.util.ArrayList;
import java.util.List;

public class ContentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class groupListPagePostsDto {

        private Long id;
        private Long userId;
        private Long groupId;
        private String userName;
        private String profileImageUrl;
        private String groupName;
        private String content;
        private Double latitude;
        private Double longitude;
        private String location;
        private LocalDateTime createAt;
        private long views;
        private String contentLink;
        private Long comments;
        private Long emotions;
        private Long emotionStatus;
        private Boolean bookmarkAddStatus;
        List<ContentDto.ImageResponseDto> Images;
        List<EmotionResponseGroupListDto> emotionResponseDtos;

        public static ContentDto.groupListPagePostsDto response(
                Content content, Long emotionStatus, String views, Boolean bookmarkAddStatus
        ) {
            return groupListPagePostsDto.builder()
                    .id(content.getId())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .groupName(content.getGroup().getGroupName())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
                    .createAt(content.getCreatedAt())
                    .views(Long.parseLong(views))
                    .contentLink(content.getContentLink())
                    .comments((long) content.getComments().size())
                    .emotions((long) content.getEmotions().size())
                    .emotionStatus(emotionStatus)
                    .bookmarkAddStatus(bookmarkAddStatus)
                    .Images(content.getContentImages()
                            .stream()
                            .map(ContentDto.ImageResponseDto::response)
                            .toList())
                    .emotionResponseDtos(content.getEmotions()
                            .stream().limit(2)
                            .map(ContentDto.EmotionResponseGroupListDto::response)
                            .toList())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
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
    @Getter
    @Builder
    public static class EmotionResponseDto {
        private Long id;
        private Long emotionStatus;
        private String profileImageUrl;
        private Long userId;

        public static ContentDto.EmotionResponseDto response(Emotion emotion) {
            return EmotionResponseDto.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .profileImageUrl(emotion.getUser().getProfileImageUrl())
                    .userId(emotion.getUser().getId())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class EmotionResponseGroupListDto {
        private Long id;
        private Long emotionStatus;

        public static ContentDto.EmotionResponseGroupListDto response(Emotion emotion) {
            return EmotionResponseGroupListDto.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class CreateDto {

        private Long id;
        private String userName;
        private String profileImageUrl;
        @NotNull(message = "내용을 입력하지 않았습니다.")
        private String content;
        private Double latitude;
        private Double longitude;
        private String location;
        private long views;
        private String contentLink;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;

        public static ContentDto.CreateDto response(Content content, List<ContentDto.ImageResponseDto> collect) {
            return CreateDto.builder()
                    .id(content.getId())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
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
    @Getter
    @Builder
    public static class detailDto {

        private Long id;
        private Long userId;
        private Long groupId;
        private String groupName;
        private String userName;
        private String profileImageUrl;
        private String content;
        private Double latitude;
        private Double longitude;
        private String location;
        private long views;
        private Boolean bookmarkAddStatus;
        private Long emotionStatus;
        private String contentLink;
        List<ContentDto.ImageResponseDto> collect;

        public static ContentDto.detailDto response(
                Content content, Integer views, List<ContentDto.ImageResponseDto> collect,
                boolean bookmarkAddStatus, Long emotionStatus
        ) {
            return detailDto.builder()
                    .id(content.getId())
                    .groupName(content.getGroup().getGroupName())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
                    .views(views)
                    .contentLink(content.getContentLink())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .bookmarkAddStatus(bookmarkAddStatus)
                    .emotionStatus(emotionStatus)
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
        private String userName;
        private String profileImageUrl;
        @NotNull(message = "내용을 입력하지 않았습니다.")
        private String content;
        private Double latitude;
        private Double longitude;
        private String location;
        private long views;
        private String contentLink;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;
        private List<deleteImageNameDto> deleteContentImageName;

        public static ContentDto.UpdateDto response(Content content, List<ContentDto.ImageResponseDto> collect, Integer views) {
            return UpdateDto.builder()
                    .id(content.getId())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
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
    @Getter
    @Builder
    public static class deleteImageNameDto {
        private String imageName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class deleteContent {
        private Long contentid;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class mapListContent {
        private Long id;
        private String location;
        private Double latitude;
        private Double longitude;
        private Long userId;
        private Long groupId;
        private Long counts;
        private String contentImageUrl;

        public static ContentDto.mapListContent response(
                Content content, List<ContentDto.ImageResponseDto> collect, Long counts
        ) {
            if (collect.size() != 0){
                return mapListContent.builder()
                        .id(content.getId())
                        .location(content.getLocation())
                        .latitude(content.getLatitude())
                        .longitude(content.getLongitude())
                        .userId(content.getUser().getId())
                        .groupId(content.getGroup().getId())
                        .counts(counts)
                        .contentImageUrl(collect.get(0).imageUrl)
                        .build();
            } else {
                return mapListContent.builder()
                        .id(content.getId())
                        .location(content.getLocation())
                        .latitude(content.getLatitude())
                        .longitude(content.getLongitude())
                        .userId(content.getUser().getId())
                        .groupId(content.getGroup().getId())
                        .counts(counts)
                        .contentImageUrl("https://dnd-diary-image-bucket.s3.ap-northeast-2.amazonaws.com/%EC%BF%B5%EC%95%BC.png")
                        .build();
            }
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class mapListContentDetail {
        private Long contentId;
        private Long groupId;
        private String location;
        private String content;
        private String groupImage;
        private String groupName;
        private String createAt;
        private Integer contentImageSize;
        private String contentImageUrl;

        public static ContentDto.mapListContentDetail response(
                Content content, List<ContentDto.ImageResponseDto> collect
        ) {
            if (collect.size() != 0){
                return mapListContentDetail.builder()
                        .contentId(content.getId())
                        .groupId(content.getGroup().getId())
                        .location(content.getLocation())
                        .content(content.getContent())
                        .groupImage(content.getGroup().getGroupImageUrl())
                        .groupName(content.getGroup().getGroupName())
                        .createAt(content.getCreatedAt().toString().substring(2, 10).replace("-", "."))
                        .contentImageSize(collect.size())
                        .contentImageUrl(collect.get(0).imageUrl)
                        .build();
            } else {
                return mapListContentDetail.builder()
                        .contentId(content.getId())
                        .groupId(content.getId())
                        .location(content.getLocation())
                        .content(content.getContent())
                        .groupImage(content.getGroup().getGroupImageUrl())
                        .groupName(content.getGroup().getGroupName())
                        .createAt(content.getCreatedAt().toString().substring(2, 10).replace("-", "."))
                        .contentImageSize(0)
                        .contentImageUrl("https://dnd-diary-image-bucket.s3.ap-northeast-2.amazonaws.com/%EC%BF%B5%EC%95%BC.png")
                        .build();
            }
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ContentSearchDto {
        private Long userId;
        private String userName;
        private String profileImageUrl;
        private Long contentId;
        private Long groupId;
        private String content;
        private String groupImage;
        private String groupName;
        private LocalDateTime createAt;
        private Integer contentImageListSize;
        List<ContentDto.ImageResponseDto> contentImageList;

        public static ContentDto.ContentSearchDto response(
                Content content,
                List<ContentDto.ImageResponseDto> collect
        ) {
            return ContentSearchDto.builder()
                    .userId(content.getUser().getId())
                    .userName(content.getUser().getName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .contentId(content.getId())
                    .groupId(content.getId())
                    .content(content.getContent())
                    .groupImage(content.getGroup().getGroupImageUrl())
                    .groupName(content.getGroup().getGroupName())
                    .createAt(content.getCreatedAt())
                    .contentImageListSize(collect.size())
                    .contentImageList(collect)
                    .build();
        }
    }
}
