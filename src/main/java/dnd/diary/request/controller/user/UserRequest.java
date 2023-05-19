package dnd.diary.request.controller.user;

import dnd.diary.request.service.UserServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class UserRequest {

    @NoArgsConstructor
    @Getter
    public static class CreateUser {

        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickName;

        private String phoneNumber;
        private String profileImageUrl;

        public UserServiceRequest.CreateUser toServiceRequest() {
            return UserServiceRequest.CreateUser.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .nickName(nickName)
                    .phoneNumber(phoneNumber)
                    .profileImageUrl(profileImageUrl)
                    .build();
        }

        // 테스트 생성자
        public CreateUser(String email, String password, String name, String nickName, String phoneNumber, String profileImageUrl) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickName = nickName;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
        }
    }
}
