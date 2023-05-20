package dnd.diary.response.user;

import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.request.UserDto;
import dnd.diary.request.content.ContentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {

    @NoArgsConstructor
    @Getter
    public static class Login {
        private Long id;
        private String email;
        private String name;
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;
        private String atk;
        private String rtk;

        @Builder
        private Login(Long id, String email, String name, String nickName, String phoneNumber, String profileImageUrl, String atk, String rtk) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.nickName = nickName;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
            this.atk = atk;
            this.rtk = rtk;
        }

        public static UserResponse.Login response(User user, String atk, String rtk) {
            return UserResponse.Login.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickName())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImageUrl(user.getProfileImageUrl())
                    .atk(atk)
                    .rtk(rtk)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Detail {
        private Long id;
        private String email;
        private String password;
        private String name;
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;

        @Builder
        private Detail(Long id, String email, String password, String name, String nickName, String phoneNumber, String profileImageUrl) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickName = nickName;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
        }

        public static UserResponse.Detail response(User user){
            return UserResponse.Detail.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickName())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Update {
        private String nickName;
        private String profileImageUrl;

        @Builder
        private Update(String nickName, String profileImageUrl) {
            this.nickName = nickName;
            this.profileImageUrl = profileImageUrl;
        }

        public static UserResponse.Update response(User user) {
            return UserResponse.Update.builder()
                    .nickName(user.getNickName())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ContentList {
        private Long contentId;
        private Long userId;
        private String profileImageUrl;
        private Long groupId;
        private String groupName;
        private String groupImage;
        private String content;
        private LocalDateTime createAt;
        private Integer views;
        private Integer comments;
        private Integer imageSize;
        List<ContentDto.ImageResponseDto> images;

        @Builder
        private ContentList(Long contentId, Long userId, String profileImageUrl, Long groupId, String groupName, String groupImage, String content, LocalDateTime createAt, Integer views, Integer comments, Integer imageSize, List<ContentDto.ImageResponseDto> images) {
            this.contentId = contentId;
            this.userId = userId;
            this.profileImageUrl = profileImageUrl;
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupImage = groupImage;
            this.content = content;
            this.createAt = createAt;
            this.views = views;
            this.comments = comments;
            this.imageSize = imageSize;
            this.images = images;
        }

        public static UserResponse.ContentList response(
                Content content,
                List<ContentDto.ImageResponseDto> images,
                Integer views
        ) {
            return ContentList.builder()
                    .contentId(content.getId())
                    .userId(content.getUser().getId())
                    .profileImageUrl(content.getUser().getProfileImageUrl())
                    .groupId(content.getGroup().getId())
                    .groupName(content.getGroup().getGroupName())
                    .groupImage(content.getGroup().getGroupImageUrl())
                    .content(content.getContent())
                    .createAt(content.getCreatedAt())
                    .views(views)
                    .comments(content.getComments().size())
                    .imageSize(images.size())
                    .images(images)
                    .build();
        }
    }
}
