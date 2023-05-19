package dnd.diary.request.controller.user;

import dnd.diary.request.UserDto;
import dnd.diary.request.service.UserServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @NoArgsConstructor
    @Getter
    public static class CreateUser {
        private String email;
        private String password;
        private String name;
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
