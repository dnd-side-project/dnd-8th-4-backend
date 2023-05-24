package dnd.diary.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.request.controller.user.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입 API")
    @Test
    void createUserAccount() throws Exception {
        // given
        UserRequest.CreateUser request =
                new UserRequest.CreateUser("test@test.com", "abc123", "테스트 계정", "테스트 닉네임", "010-1234-5678");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원가입시 이메일을 입력하지 않으면 400 예외가 발생한다.")
    @Test
    void createUserAccountToNotEnteredEmail() throws Exception {
        // given
        UserRequest.CreateUser request =
                new UserRequest.CreateUser(" ", "abc123", "테스트 계정", "테스트 닉네임", "010-1234-5678");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인 API")
    @Test
    void loginUser() throws Exception {
        // given
        UserRequest.Login request = new UserRequest.Login(".", ".");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("정보조회 API")
    @Test
    void findMyListUser() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/info")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저 정보 수정 API")
    @Test
    void userUpdateProfile() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PATCH, URI.create("/auth"))
                                .file(multipartFile)
                                .param("nickName", "test")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저 로그아웃 API")
    @Test
    void logoutUser() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/logout")
                                .header("Authorization", "accessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저 회원탈퇴 API")
    @Test
    void userDelete() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/auth")
                                .header("Authorization", "accessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저 검색 API")
    @Test
    void searchUserList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/search")
                                .param("keyword", "test")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("북마크한 글 조회 API")
    @Test
    void myBookmarkList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/bookmark")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("작성한 글 조회 API")
    @Test
    void searchMyContentList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/content")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("작성한 댓글의 글 조회 API")
    @Test
    void searchMyCommentList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/comment")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("이메일 검증 API")
    @Test
    void checkMatchEmail() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/check")
                                .param("email", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}