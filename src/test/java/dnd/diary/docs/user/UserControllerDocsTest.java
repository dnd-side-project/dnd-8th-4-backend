package dnd.diary.docs.user;

import dnd.diary.controller.user.UserController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.request.service.UserServiceRequest;
import dnd.diary.response.user.UserResponse;
import dnd.diary.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("회원가입 API")
    @Test
    void createUserAccount() throws Exception {
        // given
        UserRequest.CreateUser request =
                new UserRequest.CreateUser("test@test.com", "abc123", "테스트 계정", "테스트 닉네임", "010-1234-5678");

        given(userService.createUserAccount(any(UserServiceRequest.CreateUser.class)))
                .willReturn(UserResponse.Login.builder()
                        .id(1L)
                        .email("test@test.com")
                        .name("테스트 계정")
                        .nickName("테스트 닉네임")
                        .phoneNumber("010-1234-5678")
                        .profileImageUrl("default.png")
                        .accessToken("issued AccessToken")
                        .refreshToken("issued RefreshToken")
                        .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-createAccountUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("유저 비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("유저 전화번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("유저 ID / Long"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("유저 전화번호"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("유저 프로필 이미지 URL"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                        .description("발급된 AccessToken"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                        .description("발급된 RefreshToken")
                        )
                ));
    }

    @DisplayName("로그인 API")
    @Test
    void loginUser() throws Exception {
        // given
        UserRequest.Login request = new UserRequest.Login("test@test.com", "테스트 계정");

        given(userService.login(any(UserServiceRequest.Login.class)))
                .willReturn(UserResponse.Login.builder()
                        .id(1L)
                        .email("test@test.com")
                        .name("테스트 계정")
                        .nickName("테스트 닉네임")
                        .phoneNumber("010-1234-5678")
                        .profileImageUrl("default.png")
                        .accessToken("issued AccessToken")
                        .refreshToken("issued RefreshToken")
                        .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("유저 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("유저 ID / Long"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("유저 전화번호"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("유저 프로필 이미지 URL"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                        .description("발급된 AccessToken"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                        .description("발급된 RefreshToken")
                        )
                ));
    }

    @DisplayName("정보 조회 API")
    @Test
    @WithUserDetails()
    void myDetail() throws Exception {
        given(userService.findMyListUser(any()))
                .willReturn(UserResponse.Detail.builder()
                        .id(1L)
                        .email("test@test.com")
                        .name("테스트 계정")
                        .nickName("테스트 닉네임")
                        .phoneNumber("010-1234-5678")
                        .profileImageUrl("test.png")
                        .build());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/info")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-myDetail",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Insert the AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("유저 ID / Long"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("유저 전화번호"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("유저 프로필 이미지 URL")
                        )
                ));
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

        given(userService.userUpdateProfile(any(), anyString(), any(MultipartFile.class)))
                .willReturn(UserResponse.Update.builder()
                        .nickName("업데이트 닉네임")
                        .profileImageUrl("update.png")
                        .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PATCH, URI.create("/auth"))
                                .file(multipartFile)
                                .header("Authorization", "JWT AccessToken")
                                .param("nickName", "test")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("file")
                                        .description("변경할 이미지")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("nickName")
                                        .description("변경할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING)
                                        .description("변경된 닉네임"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("변경된 프로필 사진 URL")
                                )
                ));
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