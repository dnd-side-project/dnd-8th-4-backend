package dnd.diary.response.content;

import dnd.diary.domain.content.Content;
import dnd.diary.request.content.ContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
}
