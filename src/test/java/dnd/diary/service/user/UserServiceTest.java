package dnd.diary.service.user;

import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.UserDto;
import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.response.user.UserResponse;
import dnd.diary.service.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저가 회원가입을 하고 토큰을 발급 받는다.")
    @Test
    void createUserAccount() {
        // given
        UserRequest.CreateUser request = new UserRequest.CreateUser(
                "test@test.com", "abc123!", "테스트 계정",
                "테스트 닉네임", "010-1234-5678", ""
        );

        // when
        UserResponse.Login response = userService.createUserAccount(request.toServiceRequest());

        // then
        assertThat(response)
                .extracting("email", "name", "nickName", "phoneNumber")
                .contains("test@test.com", "테스트 계정", "테스트 닉네임", "010-1234-5678");

        assertThat(response.getProfileImageUrl()).isNotBlank();
        assertThat(response.getAtk()).isNotBlank();
        assertThat(response.getRtk()).isNotBlank();
    }

    @DisplayName("유저가 로그인을 하고 토큰을 발급 받는다.")
    @Test
    void login() {
        getUserAndSave();

        // given
        UserRequest.Login request = new UserRequest.Login("test@test.com", "abc123!");

        // when
        UserResponse.Login response = userService.login(request.toServiceRequest());

        // then
        assertThat(response)
                .extracting("email", "name", "nickName", "phoneNumber")
                .contains("test@test.com", "테스트 계정", "테스트 닉네임", "010-1234-5678");

        assertThat(response.getProfileImageUrl()).isNotBlank();
        assertThat(response.getAtk()).isNotBlank();
        assertThat(response.getRtk()).isNotBlank();
    }

    @DisplayName("유저가 자신의 정보를 조회한다.")
    @Test
    void findMyListUser() {
        // given
        User user = getUserAndSave();

        // when
        UserResponse.Detail response = userService.findMyListUser(user.getId());

        // then
        assertThat(response)
                .extracting("email", "name", "nickName", "phoneNumber")
                .contains("test@test.com", "테스트 계정", "테스트 닉네임", "010-1234-5678");
    }

    @DisplayName("유저가 자신의 정보를 수정한다.")
    @Test
    void userUpdateProfile() {
        // given
        User user = getUserAndSave();
        MultipartFile file = new MockMultipartFile("file", "text", "text/plain", "Spring Framework".getBytes());

        given(s3Service.saveProfileImage(any(MultipartFile.class)))
                .willReturn("updateImage");
        // when
        UserResponse.Update response = userService.userUpdateProfile(user.getId(), "업데이트 계정", file);

        // then
        assertThat(response)
                .extracting("nickName","profileImageUrl")
                .contains("업데이트 계정","updateImage");
    }

    // method
    private User getUserAndSave() {
        User user = User.builder()
                .id(1L)
                .authorities(getAuthorities())
                .email("test@test.com")
                .password(passwordEncoder.encode("abc123!"))
                .name("테스트 계정")
                .nickName("테스트 닉네임")
                .phoneNumber("010-1234-5678")
                .profileImageUrl("test.png")
                .mainLevel(0L)
                .subLevel(0.0)
                .isNewNotification(Boolean.FALSE)
                .build();

        return userRepository.save(user);
    }

    private static Set<Authority> getAuthorities() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }
}