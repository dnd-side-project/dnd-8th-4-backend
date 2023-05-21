package dnd.diary.response.content;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.content.Emotion;
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
        List<ContentResponse.ImageDetail> collect;

        @Builder
        private Create(Long id, String userName, String profileImageUrl, String content, Double latitude, Double longitude, String location, long views, String contentLink, boolean deletedYn, Long userId, Long groupId, List<ContentResponse.ImageDetail> collect) {
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
                            .map(ContentResponse.ImageDetail::response)
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
        List<ContentResponse.ImageDetail> collect;

        @Builder
        private Detail(Long id, Long userId, Long groupId, String groupName, String userName, String profileImageUrl, String content, Double latitude, Double longitude, String location, long views, Boolean bookmarkAddStatus, Long emotionStatus, String contentLink, boolean deletedYn, String createAt, List<ContentResponse.ImageDetail> collect) {
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
                Content content, Integer views, List<ContentResponse.ImageDetail> collect,
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
        List<ContentResponse.ImageDetail> imageDetails;
        List<ContentResponse.EmotionDetail> emotionDetails;

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
                    .imageDetails(content.getContentImages()
                            .stream()
                            .map(ContentResponse.ImageDetail::response)
                            .toList())
                    .emotionDetails(content.getEmotions()
                            .stream()
                            .filter(Emotion::isEmotionYn)   // 공감이 추가된 상태인 경우에만 응답으로 추가
                            .map(ContentResponse.EmotionDetail::response)
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
        private List<ContentResponse.ImageDetail> collect;

        public static ContentResponse.Update response(Content content, Integer views, List<ContentResponse.ImageDetail> collect) {
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

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class LocationSearch {
        private Long id;
        private String location;
        private Double latitude;
        private Double longitude;
        private Long userId;
        private Long groupId;
        private Long counts;
        private String contentImageUrl;
        private boolean deletedYn;

        public static ContentResponse.LocationSearch response(
                Content content, List<ContentResponse.ImageDetail> collect, Long counts
        ) {
            if (collect.size() != 0){
                return ContentResponse.LocationSearch.builder()
                        .id(content.getId())
                        .location(content.getLocation())
                        .latitude(content.getLatitude())
                        .longitude(content.getLongitude())
                        .userId(content.getUser().getId())
                        .groupId(content.getGroup().getId())
                        .counts(counts)
                        .contentImageUrl(collect.get(0).imageUrl)
                        .deletedYn(content.isDeletedYn())
                        .build();
            } else {
                return ContentResponse.LocationSearch.builder()
                        .id(content.getId())
                        .location(content.getLocation())
                        .latitude(content.getLatitude())
                        .longitude(content.getLongitude())
                        .userId(content.getUser().getId())
                        .groupId(content.getGroup().getId())
                        .counts(counts)
                        .contentImageUrl("https://dnd-diary-image-bucket.s3.ap-northeast-2.amazonaws.com/6f6b761a-8481-45b6-a6cc-9b48ff73c679.png")
                        .deletedYn(content.isDeletedYn())
                        .build();
            }
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class LocationDetail {
        private Long contentId;
        private String userProfileImage;
        private Long groupId;
        private String location;
        private String content;
        private String groupImage;
        private String groupName;
        private String createAt;
        private Integer contentImageSize;
        private String contentImageUrl;
        private boolean deletedYn;

        public static ContentResponse.LocationDetail response(
                Content content, List<ContentResponse.ImageDetail> collect
        ) {
            if (collect.size() != 0){
                return ContentResponse.LocationDetail.builder()
                        .contentId(content.getId())
                        .userProfileImage(content.getUser().getProfileImageUrl())
                        .groupId(content.getGroup().getId())
                        .location(content.getLocation())
                        .content(content.getContent())
                        .groupImage(content.getGroup().getGroupImageUrl())
                        .groupName(content.getGroup().getGroupName())
                        .createAt(content.getCreatedAt().toString().substring(2, 10).replace("-", "."))
                        .contentImageSize(collect.size())
                        .contentImageUrl(collect.get(0).imageUrl)
                        .deletedYn(content.isDeletedYn())
                        .build();
            } else {
                return ContentResponse.LocationDetail.builder()
                        .contentId(content.getId())
                        .userProfileImage(content.getUser().getProfileImageUrl())
                        .groupId(content.getId())
                        .location(content.getLocation())
                        .content(content.getContent())
                        .groupImage(content.getGroup().getGroupImageUrl())
                        .groupName(content.getGroup().getGroupName())
                        .createAt(content.getCreatedAt().toString().substring(2, 10).replace("-", "."))
                        .contentImageSize(0)
                        .contentImageUrl("https://dnd-diary-image-bucket.s3.ap-northeast-2.amazonaws.com/6f6b761a-8481-45b6-a6cc-9b48ff73c679.png")
                        .deletedYn(content.isDeletedYn())
                        .build();
            }
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class ImageDetail {
        private Long id;
        private String imageName;
        private String imageUrl;
        private Long contentId;

        public static ContentResponse.ImageDetail response(ContentImage contentImage) {
            return ContentResponse.ImageDetail.builder()
                    .id(contentImage.getId())
                    .imageUrl(contentImage.getImageUrl())
                    .imageName(contentImage.getImageName())
                    .contentId(contentImage.getContent().getId())
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class EmotionDetail {
        private Long id;
        private Long emotionStatus;

        public static ContentResponse.EmotionDetail response(Emotion emotion) {
            return ContentResponse.EmotionDetail.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class EmotionDetails {
        private Long id;
        private Long emotionStatus;
        private String profileImageUrl;
        private Long userId;

        public static ContentResponse.EmotionDetails response(Emotion emotion) {
            return EmotionDetails.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .profileImageUrl(emotion.getUser().getProfileImageUrl())
                    .userId(emotion.getUser().getId())
                    .build();
        }
    }
}
