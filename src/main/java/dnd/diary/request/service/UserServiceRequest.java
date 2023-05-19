package dnd.diary.request.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserServiceRequest {

    @NoArgsConstructor
    @Getter
    public static class CreateUser {
        private String email;
        private String password;
        private String name;
        private String nickName;
        private String phoneNumber;
        private String profileImageUrl;

        @Builder
        private CreateUser(String email, String password, String name, String nickName, String phoneNumber, String profileImageUrl) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickName = nickName;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
        }
    }
}