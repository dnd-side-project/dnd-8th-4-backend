package dnd.diary.dto.userDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.bookmark.Bookmark;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class BookmarkDto {
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
        List<ContentDto.EmotionResponseDto> emotionResponseDtos;

     public static UserDto.BookmarkDto response(
             Bookmark bookmark, Integer views, Long emotionStatus,
             List<ContentDto.ImageResponseDto> images,
             List<ContentDto.EmotionResponseDto> emotionResponseDtos
     ){
         return BookmarkDto.builder()
                 .id(bookmark.getContent().getId())
                 .userId(bookmark.getContent().getUser().getId())
                 .groupId(bookmark.getContent().getGroup().getId())
                 .userName(bookmark.getContent().getUser().getNickName())
                 .groupName(bookmark.getContent().getGroup().getGroupName())
                 .content(bookmark.getContent().getContent())
                 .latitude(bookmark.getContent().getLatitude())
                 .longitude(bookmark.getContent().getLongitude())
                 .createAt(bookmark.getContent().getCreatedAt())
                 .views(views)
                 .contentLink(bookmark.getContent().getContentLink())
                 .comments((long) bookmark.getContent().getComments().size())
                 .emotions((long) bookmark.getContent().getEmotions().size())
                 .emotionStatus(emotionStatus)
                 .Images(images)
                 .emotionResponseDtos(emotionResponseDtos)
                 .build();
     }
    }
}
