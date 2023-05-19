package dnd.diary.response.user;

import dnd.diary.domain.user.User;
import dnd.diary.request.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
}
