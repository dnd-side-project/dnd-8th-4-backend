package dnd.diary.dto.userDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.ContentDto;
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
    @Getter
    @Builder
    public static class RegisterDto {
        private Long id;
        @NotNull(message = "이메일이 입력되지 않았습니다.")
        private String email;
        @NotNull(message = "비밀번호가 입력되지 않았습니다.")
        private String password;
        @NotNull(message = "이름이 입력되지 않았습니다.")
        private String name;
        @NotNull(message = "닉네임이 입력되지 않았습니다.")
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;
        private String atk;
        private String rtk;

        public static RegisterDto response(User user, String atk, String rtk) {
            return RegisterDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .password("암호화 되었습니다.")
                    .name(user.getName())
                    .nickName(user.getNickName())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImageUrl(user.getProfileImageUrl())
                    .atk(atk)
                    .rtk(rtk)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class UpdateDto {
        private String nickname;
        private String profileImageUrl;

        public static UserDto.UpdateDto response(User user) {
            return UpdateDto.builder()
                    .nickname(user.getNickName())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class LoginDto{
        private Long id;
        @NotNull(message = "이메일이 입력되지 않았습니다.")
        private String email;
        @NotNull(message = "비밀번호가 입력되지 않았습니다.")
        private String password;
        private String name;
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;
        private String atk;
        private String rtk;

        public static LoginDto response(User user,String atk, String rtk){
            return LoginDto.builder()
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
    public static class BookmarkDto {
        private Long contentId;
        private Long userId;
        private Long groupId;
        private String groupName;
        private String content;
        private LocalDateTime createAt;
        private Integer views;
        private Integer comments;
        private Integer imageSize;
        List<ContentDto.ImageResponseDto> Images;

     public static UserDto.BookmarkDto response(
             Content content,
             List<ContentDto.ImageResponseDto> images,
             Integer views
     ){
         return BookmarkDto.builder()
                 .contentId(content.getId())
                 .userId(content.getUser().getId())
                 .groupId(content.getGroup().getId())
                 .groupName(content.getGroup().getGroupName())
                 .content(content.getContent())
                 .createAt(content.getCreatedAt())
                 .views(views)
                 .comments(content.getComments().size())
                 .imageSize(images.size())
                 .Images(images)
                 .build();
     }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class myContentListDto {
        private Long contentId;
        private Long groupId;
        private String groupName;
        private String content;
        private LocalDateTime createAt;
        private Integer views;
        private Integer comments;
        private Integer imageSize;
        List<ContentDto.ImageResponseDto> Images;

        public static UserDto.myContentListDto response(
                Content content,
                List<ContentDto.ImageResponseDto> images,
                Integer views
        ){
            return myContentListDto.builder()
                    .contentId(content.getId())
                    .groupId(content.getGroup().getId())
                    .groupName(content.getGroup().getGroupName())
                    .content(content.getContent())
                    .createAt(content.getCreatedAt())
                    .views(views)
                    .comments(content.getComments().size())
                    .imageSize(images.size())
                    .Images(images)
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
