package dnd.diary.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.request.content.ContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class UserDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class InfoDto {
        private Long id;
        private String email;
        private String password;
        private String name;
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;

        public static InfoDto response(User user){
            return InfoDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickName())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class myCommentListDto {
        private Long contentId;
        private Long groupId;
        private String groupName;
        private String groupImage;
        private String content;
        private LocalDateTime createAt;
        private Integer views;
        private Integer comments;
        private Integer imageSize;
        List<ContentDto.ImageResponseDto> Images;

        public static UserDto.myCommentListDto response(
                Content content, List<ContentDto.ImageResponseDto> images, Integer views
        ){
            return myCommentListDto.builder()
                    .contentId(content.getId())
                    .groupId(content.getGroup().getId())
                    .groupName(content.getGroup().getGroupName())
                    .groupImage(content.getGroup().getGroupImageUrl())
                    .content(content.getContent())
                    .createAt(content.getCreatedAt())
                    .views(views)
                    .comments(content.getComments().size())
                    .imageSize(images.size())
                    .Images(images)
                    .build();
        }
    }

}
