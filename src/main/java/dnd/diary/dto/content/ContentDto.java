package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ContentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class ImageResponseDto{
        private Long id;
        private String imageUrl;
        private Long contentId;

        public static ContentDto.ImageResponseDto response(ContentImage contentImage){
            return ImageResponseDto.builder()
                    .imageUrl(contentImage.getImageUrl())
                    .contentId(contentImage.getContent().getId())
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

        public static ContentDto.CreateDto response(Content content,List<ContentDto.ImageResponseDto> collect){
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
}
