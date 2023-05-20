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
        UserRequest.CreateUser request = new UserRequest.CreateUser(".", ".", ".", ".", " ", " ");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
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
                                .param("nickName","test")
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
                                .header("Authorization","testToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}