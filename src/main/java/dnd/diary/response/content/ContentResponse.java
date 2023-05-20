package dnd.diary.response.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.request.content.ContentDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ContentResponse {

    @NoArgsConstructor
    @Getter
    public static class Create {

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
        private boolean deletedYn;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;

        @Builder
        private Create(Long id, String userName, String profileImageUrl, String content, Double latitude, Double longitude, String location, long views, String contentLink, boolean deletedYn, Long userId, Long groupId, List<ContentDto.ImageResponseDto> collect) {
            this.id = id;
            this.userName = userName;
            this.profileImageUrl = profileImageUrl;
            this.content = content;
            this.latitude = latitude;
            this.longitude = longitude;
            this.location = location;
            this.views = views;
            this.contentLink = contentLink;
            this.deletedYn = deletedYn;
            this.userId = userId;
            this.groupId = groupId;
            this.collect = collect;
        }

        public static ContentResponse.Create response(Content content) {
            return ContentResponse.Create.builder()
                    .id(content.getId())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
                    .views(content.getViews())
                    .contentLink(content.getContentLink())
                    .deletedYn(content.isDeletedYn())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(content.getContentImages()
                            .stream()
                            .map(ContentDto.ImageResponseDto::response)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Detail {
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
        private boolean deletedYn;
        private String createAt;
        List<ContentDto.ImageResponseDto> collect;

        @Builder
        private Detail(Long id, Long userId, Long groupId, String groupName, String userName, String profileImageUrl, String content, Double latitude, Double longitude, String location, long views, Boolean bookmarkAddStatus, Long emotionStatus, String contentLink, boolean deletedYn, String createAt, List<ContentDto.ImageResponseDto> collect) {
            this.id = id;
            this.userId = userId;
            this.groupId = groupId;
            this.groupName = groupName;
            this.userName = userName;
            this.profileImageUrl = profileImageUrl;
            this.content = content;
            this.latitude = latitude;
            this.longitude = longitude;
            this.location = location;
            this.views = views;
            this.bookmarkAddStatus = bookmarkAddStatus;
            this.emotionStatus = emotionStatus;
            this.contentLink = contentLink;
            this.deletedYn = deletedYn;
            this.createAt = createAt;
            this.collect = collect;
        }

        public static ContentResponse.Detail response(
                Content content, Integer views, List<ContentDto.ImageResponseDto> collect,
                boolean bookmarkAddStatus, Long emotionStatus
        ) {
            return ContentResponse.Detail.builder()
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
                    .deletedYn(content.isDeletedYn())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .bookmarkAddStatus(bookmarkAddStatus)
                    .emotionStatus(emotionStatus)
                    .createAt(content.getCreatedAt().toString().substring(2, 10).replace("-", "."))
                    .build();
        }
    }


    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class GroupPage {
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
        private Boolean deletedYn;
        private Long comments;
        private Long emotions;
        private Long emotionStatus;
        private Boolean bookmarkAddStatus;
        List<ContentDto.ImageResponseDto> Images;
        List<ContentDto.EmotionResponseGroupListDto> emotionResponseDtos;

        public static ContentResponse.GroupPage response(
                Content content, Long emotionStatus, String views, Boolean bookmarkAddStatus
        ) {
            return ContentResponse.GroupPage.builder()
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
                    .deletedYn(content.isDeletedYn())
                    .comments((long) content.getComments().size())
                    .emotions((long) content.getEmotions().size())
                    .emotionStatus(emotionStatus)
                    .bookmarkAddStatus(bookmarkAddStatus)
                    .Images(content.getContentImages()
                            .stream()
                            .map(ContentDto.ImageResponseDto::response)
                            .toList())
                    .emotionResponseDtos(content.getEmotions()
                            .stream()
                            .filter(Emotion::isEmotionYn)   // 공감이 추가된 상태인 경우에만 응답으로 추가
                            .map(ContentDto.EmotionResponseGroupListDto::response)
                            .toList())
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Builder
    public static class Update {
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
        private boolean deletedYn;
        private Long userId;
        private Long groupId;
        List<ContentDto.ImageResponseDto> collect;
        private List<ContentDto.deleteImageNameDto> deleteContentImageName;

        public static ContentResponse.Update response(Content content, Integer views, List<ContentDto.ImageResponseDto> collect) {
            return ContentResponse.Update.builder()
                    .id(content.getId())
                    .userName(content.getUser().getNickName())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .content(content.getContent())
                    .latitude(content.getLatitude())
                    .longitude(content.getLongitude())
                    .location(content.getLocation())
                    .views(views)
                    .contentLink(content.getContentLink())
                    .deletedYn(content.isDeletedYn())
                    .userId(content.getUser().getId())
                    .groupId(content.getGroup().getId())
                    .collect(collect)
                    .build();
        }
    }
}
