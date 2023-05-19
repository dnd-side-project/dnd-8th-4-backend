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
    public static class CreateUser {
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

        @Builder
        private CreateUser(Long id, String email, String password, String name, String nickName, String phoneNumber, String profileImageUrl, String atk, String rtk) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickName = nickName;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
            this.atk = atk;
            this.rtk = rtk;
        }

        public static UserResponse.CreateUser response(User user, String atk, String rtk) {
            return UserResponse.CreateUser.builder()
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
}
