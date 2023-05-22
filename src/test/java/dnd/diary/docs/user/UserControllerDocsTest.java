package dnd.diary.docs.user;

import dnd.diary.controller.user.UserController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.request.service.UserServiceRequest;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.response.user.UserResponse;
import dnd.diary.response.user.UserSearchResponse;
import dnd.diary.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

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
        given(userService.logout(any(), anyString()))
                .willReturn(true);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/logout")
                                .header("Authorization", "accessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-logout",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data").type(JsonFieldType.BOOLEAN)
                                        .description("로그아웃 성공 여부")
                        )
                ));
    }

    @DisplayName("유저 회원탈퇴 API")
    @Test
    void userDelete() throws Exception {
        given(userService.deleteUser(any(), anyString()))
                .willReturn(true);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/auth")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-delete",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data").type(JsonFieldType.BOOLEAN)
                                        .description("회원탈퇴 성공 여부")
                        )
                ));
    }

    @DisplayName("유저 검색 API")
    @Test
    void searchUserList() throws Exception {
        // given
        given(userService.searchUserList(anyString()))
                .willReturn(List.of(
                        new UserSearchResponse.UserSearchInfo(1L, "test1@test.com", "테스트 닉네임1", "test1.png"),
                        new UserSearchResponse.UserSearchInfo(2L, "test2@test.com", "테스트 닉네임2", "test2.png")
                ));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/search")
                                .header("Authorization", "JWT AccessToken")
                                .param("keyword", "테스트")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-search",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("keyword")
                                        .description("검색할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                        .description("검색된 유저 목록"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER)
                                        .description("검색된 유저 ID / Long"),
                                fieldWithPath("data[].userEmail").type(JsonFieldType.STRING)
                                        .description("검색된 유저 이메일"),
                                fieldWithPath("data[].userNickName").type(JsonFieldType.STRING)
                                        .description("검색된 유저 닉네임"),
                                fieldWithPath("data[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("검색된 유저 프로필 사진 URL")
                        )
                ));
    }

    @DisplayName("북마크한 글 조회 API")
    @Test
    void myBookmarkList() throws Exception {

        List<ContentResponse.ImageDetail> images = List.of(ContentResponse.ImageDetail.builder()
                .id(1L)
                .contentId(1L)
                .imageName("테스트 이미지 이름")
                .imageUrl("테스트 이미지 URL")
                .build()
        );

        List<UserResponse.ContentList> contentLists = List.of(
                UserResponse.ContentList.builder()
                        .contentId(1L)
                        .userId(1L)
                        .profileImageUrl("test.png")
                        .groupId(1L)
                        .groupName("테스트 그룹")
                        .groupImage("test.png")
                        .content("테스트 내용")
                        .createAt(LocalDateTime.of(2023, 4, 12, 13, 11))
                        .views(23)
                        .comments(3)
                        .imageSize(1)
                        .images(images)
                        .build(),
                UserResponse.ContentList.builder()
                        .contentId(2L)
                        .userId(2L)
                        .profileImageUrl("test.png")
                        .groupId(2L)
                        .groupName("테스트 그룹")
                        .groupImage("test.png")
                        .content("테스트 내용")
                        .createAt(LocalDateTime.of(2023, 7, 15, 13, 11))
                        .views(2)
                        .comments(1)
                        .imageSize(1)
                        .images(images)
                        .build()
        );

        Page<UserResponse.ContentList> response =
                new PageImpl<>(contentLists, PageRequest.of(0, 2), contentLists.size());

        given(userService.listMyBookmark(any(), anyInt()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/my/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-myBookmark",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("page")
                                        .description("요청할 페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("북마크한 피드 목록"),
                                fieldWithPath("data.content[].contentId").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드 ID / Long"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 유저 ID / Long"),
                                fieldWithPath("data.content[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 유저 프로필 사진 URL"),
                                fieldWithPath("data.content[].groupId").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 그룹 ID / Long"),
                                fieldWithPath("data.content[].groupName").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 그룹 이름"),
                                fieldWithPath("data.content[].groupImage").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 그룹 이미지"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 피드 내용"),
                                fieldWithPath("data.content[].createAt").type(JsonFieldType.ARRAY)
                                        .description("북마크한 피드의 작성 날짜"),
                                fieldWithPath("data.content[].views").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 조회수 / Integer"),
                                fieldWithPath("data.content[].comments").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 댓글 수"),
                                fieldWithPath("data.content[].imageSize").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 이미지 개수"),
                                fieldWithPath("data.content[].images[]").type(JsonFieldType.ARRAY)
                                        .description("북마크한 피드의 이미지 목록"),
                                fieldWithPath("data.content[].images[].id").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드의 이미지 ID / Long"),
                                fieldWithPath("data.content[].images[].imageName").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 이미지 이름"),
                                fieldWithPath("data.content[].images[].imageUrl").type(JsonFieldType.STRING)
                                        .description("북마크한 피드의 이미지 URL"),
                                fieldWithPath("data.content[].images[].contentId").type(JsonFieldType.NUMBER)
                                        .description("북마크한 피드 ID / Long"),
                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                        .description("정렬 정보가 비었는지 여부"),
                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬이 되었는지 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬이 안 되었는지 여부"),
                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                                        .description("페이지 시작점"),
                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                                        .description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                        .description("페이징이 안된 여부"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                        .description("마지막 페이지 여부"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                        .description("전체 페이지 수"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                        .description("전체 요소 수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                        .description("페이지 번호"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                                        .description("정렬 정보가 비었는지 여부"),
                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬이 되었는지 여부"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬이 안 되었는지 여부"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                        .description("첫 페이지 여부"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지의 요소 수"),
                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                        .description("데이터가 비었는지 여부")

                        )
                ));
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