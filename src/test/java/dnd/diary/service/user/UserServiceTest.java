package dnd.diary.service.user;

import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.response.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @DisplayName("유저가 회원가입을 한다.")
    @Test
    void createUserAccount() {
        // given
        UserRequest.CreateUser request = new UserRequest.CreateUser(
                "test@test.com", "abc123!", "test",
                "testNickName", "010-1234-5678", ""
        );

        // when
        UserResponse.CreateUser response = userService.createUserAccount(request.toServiceRequest());

        // then
        assertThat(response)
                .extracting("email","name","nickName","phoneNumber")
                .contains("test@test.com","test","testNickName","010-1234-5678");

        assertThat(response.getPassword()).isNotEqualTo(request.getPassword());
        assertThat(response.getProfileImageUrl()).isNotBlank();
        assertThat(response.getAtk()).isNotBlank();
        assertThat(response.getRtk()).isNotBlank();
    }
}