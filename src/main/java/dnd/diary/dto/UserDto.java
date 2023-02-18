package dnd.diary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
}
