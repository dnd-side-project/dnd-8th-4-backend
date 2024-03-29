package dnd.diary.docs.content;

import dnd.diary.controller.content.ContentController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.response.content.EmotionResponse;
import dnd.diary.service.content.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContentControllerDocsTest extends RestDocsSupport {
    private final ContentService contentService = mock(ContentService.class);

    @Override
    protected Object initController() {
        return new ContentController(contentService);
    }

    @DisplayName("피드 작성 API")
    @Test
    void contentCreate() throws Exception {
        // given
        given(contentService.createContent(any(), any(), anyLong(), anyString(), anyDouble(), anyDouble(), anyString()))
                .willReturn(ContentResponse.Create.builder()
                        .id(1L)
                        .userName("테스트 계정")
                        .profileImageUrl("profile.png")
                        .content("테스트 내용")
                        .latitude(1.0)
                        .longitude(1.0)
                        .location("삼성 서비스 센터")
                        .views(0L)
                        .contentLink("contentLink")
                        .deletedYn(false)
                        .userId(1L)
                        .groupId(1L)
                        .collect(getImageDetails())
                        .build()
                );

        MockMultipartFile file1 = new MockMultipartFile("file", "test", "text/plain", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2", "text/plain", "test2".getBytes());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.POST, URI.create("/content"))
                                .file(file1)
                                .file(file2)
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", "1")
                                .param("content", "테스트 내용")
                                .param("latitude", "1.0")
                                .param("longitude", "1.0")
                                .param("location", "삼성 서비스 센터")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-create",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParts(
                                partWithName("file").optional()
                                        .description("피드 이미지 List")
                        ),
                        requestParameters(
                                parameterWithName("groupId")
                                        .description("그룹 ID"),
                                parameterWithName("content")
                                        .description("피드 내용"),
                                parameterWithName("latitude")
                                        .optional()
                                        .description("위도"),
                                parameterWithName("longitude")
                                        .optional()
                                        .description("경도"),
                                parameterWithName("location")
                                        .description("위치 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 ID"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 그룹 ID"),
                                fieldWithPath("data.collect[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.collect[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.collect[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.collect[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.collect[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID")
                        )
                ));
    }

    @DisplayName("피드 조회 API")
    @Test
    void contentDetail() throws Exception {
        given(contentService.detailContent(any(), anyLong()))
                .willReturn(ContentResponse.Detail.builder()
                        .id(1L)
                        .userId(1L)
                        .groupId(1L)
                        .groupName("테스트 그룹")
                        .userName("테스트 계정")
                        .profileImageUrl("profile.png")
                        .content("테스트 내용")
                        .latitude(1.0)
                        .longitude(1.0)
                        .location("삼성 서비스 센터")
                        .views(0L)
                        .bookmarkAddStatus(false)
                        .emotionStatus(2L)
                        .contentLink("contentLink")
                        .deletedYn(false)
                        .createAt("2023-02-13")
                        .collect(getImageDetails())
                        .build());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content")
                                .header("Authorization", "JWT AccessToken")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-detail",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("contentId")
                                        .description("피드 Id")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 ID"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 그룹 ID"),
                                fieldWithPath("data.groupName").type(JsonFieldType.STRING)
                                        .description("피드 작성자 그룹 이름"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자 이름"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.bookmarkAddStatus").type(JsonFieldType.BOOLEAN)
                                        .description("피드 북마크 여부"),
                                fieldWithPath("data.emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("피드 공감 타입"),
                                fieldWithPath("data.contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.createAt").type(JsonFieldType.STRING)
                                        .description("피드 생성 날짜"),
                                fieldWithPath("data.collect[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.collect[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.collect[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.collect[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.collect[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID")
                        )
                ));
    }

    @DisplayName("피드 수정 API")
    @Test
    void contentUpdate() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("file", "test", "text/plain", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2", "text/plain", "test2".getBytes());

        given(contentService.updateContent(any(), any(), anyLong(), anyString(), anyDouble(), anyDouble(), anyString()))
                .willReturn(ContentResponse.Update.builder()
                        .id(1L)
                        .userId(1L)
                        .groupId(1L)
                        .userName("테스트 계정")
                        .profileImageUrl("profile.png")
                        .content("테스트 내용")
                        .latitude(1.0)
                        .longitude(1.0)
                        .location("삼성 서비스 센터")
                        .views(0L)
                        .contentLink("contentLink")
                        .deletedYn(false)
                        .collect(getImageDetails())
                        .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PUT, URI.create("/content"))
                                .file(file1)
                                .file(file2)
                                .header("Authorization", "JWT AccessToken")
                                .param("contentId", "1")
                                .param("content", "테스트")
                                .param("latitude", "0.0")
                                .param("longitude", "0.0")
                                .param("location", "테스트 장소")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-update",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParts(
                                partWithName("file").optional()
                                        .description("피드 이미지 List")
                        ),
                        requestParameters(
                                parameterWithName("contentId")
                                        .description("피드 ID"),
                                parameterWithName("content")
                                        .description("피드 내용"),
                                parameterWithName("latitude")
                                        .optional()
                                        .description("위도"),
                                parameterWithName("longitude")
                                        .optional()
                                        .description("경도"),
                                parameterWithName("location")
                                        .description("위치 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 ID"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 그룹 ID"),
                                fieldWithPath("data.collect[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.collect[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.collect[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.collect[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.collect[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID")
                        )
                ));
    }

    @DisplayName("피드 삭제 API")
    @Test
    void contentDelete() throws Exception {
        // given
        given(contentService.deleteContent(any(), anyLong()))
                .willReturn(true);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/content")
                                .header("Authorization", "JWT AccessToken")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("board-delete",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("contentId")
                                        .description("피드 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.BOOLEAN)
                                        .description("삭제 여부")
                        )
                ));
    }

    @DisplayName("그룹 피드 조회 API")
    @Test
    void contentGroupList() throws Exception {
        // given
        ContentResponse.GroupPage content1 = ContentResponse.GroupPage.builder()
                .id(1L)
                .userId(1L)
                .groupId(1L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .groupName("테스트 그룹")
                .content("테스트 내용")
                .latitude(1.0)
                .longitude(1.0)
                .location("삼성 서비스 센터")
                .createAt(LocalDateTime.of(2023, 12, 25, 17, 25))
                .views(0L)
                .contentLink("contentLink")
                .deletedYn(false)
                .comments(12L)
                .emotions(23L)
                .emotionStatus(3L)
                .bookmarkAddStatus(true)
                .imageDetails(getImageDetails())
                .emotionDetails(getEmotionDetails())
                .build();

        ContentResponse.GroupPage content2 = ContentResponse.GroupPage.builder()
                .id(2L)
                .userId(2L)
                .groupId(1L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .groupName("테스트 그룹")
                .content("테스트 내용")
                .latitude(2.0)
                .longitude(2.0)
                .location("삼성 서비스 센터")
                .createAt(LocalDateTime.of(2023, 11, 25, 17, 25))
                .views(3L)
                .contentLink("contentLink")
                .deletedYn(false)
                .comments(15L)
                .emotions(12L)
                .emotionStatus(1L)
                .bookmarkAddStatus(true)
                .imageDetails(getImageDetails())
                .emotionDetails(getEmotionDetails())
                .build();

        List<ContentResponse.GroupPage> lists = List.of(content1, content2);

        given(contentService.groupListContent(any(), anyLong(), anyInt()))
                .willReturn(new PageImpl<>(lists, PageRequest.of(0, 2), 2));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", "1")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-groupContent",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("groupId")
                                        .description("그룹 Id"),
                                parameterWithName("page")
                                        .description("요청 페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("피드 목록"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("유저 ID"),
                                fieldWithPath("data.content[].groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data.content[].userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자"),
                                fieldWithPath("data.content[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content[].groupName").type(JsonFieldType.STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.content[].latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.content[].longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.content[].location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.content[].createAt").type(JsonFieldType.ARRAY)
                                        .description("피드 생성날짜"),
                                fieldWithPath("data.content[].views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.content[].contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.content[].deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.content[].comments").type(JsonFieldType.NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("data.content[].emotions").type(JsonFieldType.NUMBER)
                                        .description("공감수"),
                                fieldWithPath("data.content[].emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("내 공감 타입"),
                                fieldWithPath("data.content[].bookmarkAddStatus").type(JsonFieldType.BOOLEAN)
                                        .description("북마크 여부"),
                                fieldWithPath("data.content[].imageDetails[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.content[].imageDetails[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.content[].imageDetails[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.content[].imageDetails[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.content[].imageDetails[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID"),
                                fieldWithPath("data.content[].emotionDetails[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.content[].emotionDetails[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 공감 ID"),
                                fieldWithPath("data.content[].emotionDetails[].emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("피드 공감 타입"),
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

    @DisplayName("그룹 전체 피드 조회 API")
    @Test
    void contentGroupAllList() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        ContentResponse.GroupPage content1 = ContentResponse.GroupPage.builder()
                .id(1L)
                .userId(1L)
                .groupId(1L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .groupName("테스트 그룹")
                .content("테스트 내용")
                .latitude(1.0)
                .longitude(1.0)
                .location("삼성 서비스 센터")
                .createAt(LocalDateTime.of(2023, 12, 25, 17, 25))
                .views(0L)
                .contentLink("contentLink")
                .deletedYn(false)
                .comments(12L)
                .emotions(23L)
                .emotionStatus(3L)
                .bookmarkAddStatus(true)
                .imageDetails(getImageDetails())
                .emotionDetails(getEmotionDetails())
                .build();

        ContentResponse.GroupPage content2 = ContentResponse.GroupPage.builder()
                .id(2L)
                .userId(2L)
                .groupId(1L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .groupName("테스트 그룹")
                .content("테스트 내용")
                .latitude(2.0)
                .longitude(2.0)
                .location("삼성 서비스 센터")
                .createAt(LocalDateTime.of(2023, 11, 25, 17, 25))
                .views(3L)
                .contentLink("contentLink")
                .deletedYn(false)
                .comments(15L)
                .emotions(12L)
                .emotionStatus(1L)
                .bookmarkAddStatus(true)
                .imageDetails(getImageDetails())
                .emotionDetails(getEmotionDetails())
                .build();

        List<ContentResponse.GroupPage> lists = List.of(content1, content2);

        given(contentService.groupAllListContent(any(), anyList(), anyInt()))
                .willReturn(new PageImpl<>(lists, PageRequest.of(0, 2), 2));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/all")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", groupIdArray)
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-allGroupContent",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("groupId")
                                        .description("조회하려는 그룹 id / List<Long>"),
                                parameterWithName("page")
                                        .description("요청 페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("피드 목록"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("유저 ID"),
                                fieldWithPath("data.content[].groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data.content[].userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자"),
                                fieldWithPath("data.content[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content[].groupName").type(JsonFieldType.STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.content[].latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.content[].longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.content[].location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.content[].createAt").type(JsonFieldType.ARRAY)
                                        .description("피드 생성날짜"),
                                fieldWithPath("data.content[].views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.content[].contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.content[].deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.content[].comments").type(JsonFieldType.NUMBER)
                                        .description("댓글수"),
                                fieldWithPath("data.content[].emotions").type(JsonFieldType.NUMBER)
                                        .description("공감수"),
                                fieldWithPath("data.content[].emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("내 공감 타입"),
                                fieldWithPath("data.content[].bookmarkAddStatus").type(JsonFieldType.BOOLEAN)
                                        .description("북마크 여부"),
                                fieldWithPath("data.content[].imageDetails[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.content[].imageDetails[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.content[].imageDetails[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.content[].imageDetails[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.content[].imageDetails[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID"),
                                fieldWithPath("data.content[].emotionDetails[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.content[].emotionDetails[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 공감 ID"),
                                fieldWithPath("data.content[].emotionDetails[].emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("피드 공감 타입"),
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

    @DisplayName("피드 검색 조회 API")
    @Test
    void searchContent() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        ContentResponse.Create content1 = ContentResponse.Create.builder()
                .id(1L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .content("테스트 내용")
                .latitude(1.0)
                .longitude(1.0)
                .location("삼성 서비스 센터")
                .views(0L)
                .contentLink("contentLink")
                .deletedYn(false)
                .userId(1L)
                .groupId(1L)
                .collect(getImageDetails())
                .build();

        ContentResponse.Create content2 = ContentResponse.Create.builder()
                .id(2L)
                .userName("테스트 계정")
                .profileImageUrl("profile.png")
                .content("테스트 내용")
                .latitude(2.0)
                .longitude(2.0)
                .location("삼성 서비스 센터")
                .views(2L)
                .contentLink("contentLink")
                .deletedYn(false)
                .userId(1L)
                .groupId(1L)
                .collect(getImageDetails())
                .build();

        List<ContentResponse.Create> list = List.of(content1, content2);

        given(contentService.contentSearch(anyList(), anyString(), anyInt()))
                .willReturn(new PageImpl<>(list, PageRequest.of(0, 2), list.size()));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/search")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", groupIdArray)
                                .param("word", "a")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-search",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("groupId")
                                        .description("조회하려는 그룹 id / List<Long>"),
                                parameterWithName("word")
                                        .description("검색할 단어"),
                                parameterWithName("page")
                                        .description("요청 페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("피드 목록"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data.content[].userName").type(JsonFieldType.STRING)
                                        .description("피드 작성자"),
                                fieldWithPath("data.content[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 작성자 프로필 사진"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data.content[].latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data.content[].longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data.content[].location").type(JsonFieldType.STRING)
                                        .description("피드 위치"),
                                fieldWithPath("data.content[].views").type(JsonFieldType.NUMBER)
                                        .description("피드 조회수"),
                                fieldWithPath("data.content[].contentLink").type(JsonFieldType.STRING)
                                        .description("피드 링크 주소"),
                                fieldWithPath("data.content[].deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 ID"),
                                fieldWithPath("data.content[].groupId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성자 그룹 ID"),
                                fieldWithPath("data.content[].collect[]").type(JsonFieldType.ARRAY)
                                        .description("피드 이미지 목록"),
                                fieldWithPath("data.content[].collect[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 이미지 ID"),
                                fieldWithPath("data.content[].collect[].imageName").type(JsonFieldType.STRING)
                                        .description("피드 이미지 이름"),
                                fieldWithPath("data.content[].collect[].imageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data.content[].collect[].contentId").type(JsonFieldType.NUMBER)
                                        .description("이미지가 삽입된 피드 ID"),
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

    @DisplayName("지도 피드 검색 API")
    @Test
    void myMapList() throws Exception {
        ContentResponse.LocationSearch content1 = ContentResponse.LocationSearch.builder()
                .id(1L)
                .location("삼성 서비스 센터")
                .latitude(1.0)
                .longitude(1.0)
                .userId(1L)
                .groupId(1L)
                .counts(1L)
                .contentImageUrl("default.png")
                .deletedYn(false)
                .build();

        ContentResponse.LocationSearch content2 = ContentResponse.LocationSearch.builder()
                .id(2L)
                .location("삼성 서비스 센터")
                .latitude(1.0)
                .longitude(1.0)
                .userId(2L)
                .groupId(1L)
                .counts(1L)
                .contentImageUrl("default.png")
                .deletedYn(false)
                .build();

        given(contentService.listMyMap(any(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(List.of(content1, content2));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map")
                                .header("Authorization", "JWT AccessToken")
                                .param("startLatitude", "1")
                                .param("startLongitude", "1")
                                .param("endLatitude", "1")
                                .param("endLongitude", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-myMapList",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("startLatitude")
                                        .description("좌상단 위도"),
                                parameterWithName("startLongitude")
                                        .description("좌상단 경도"),
                                parameterWithName("endLatitude")
                                        .description("우하단 위도"),
                                parameterWithName("endLongitude")
                                        .description("우하단 경도")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                        .description("피드 목록"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data[].location").type(JsonFieldType.STRING)
                                        .description("피드 지역 이름"),
                                fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER)
                                        .description("피드 경도"),
                                fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER)
                                        .description("피드 위도"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성 유저 ID"),
                                fieldWithPath("data[].groupId").type(JsonFieldType.NUMBER)
                                        .description("피드 작성 그룹 ID"),
                                fieldWithPath("data[].counts").type(JsonFieldType.NUMBER)
                                        .description("중복된 피드 수"),
                                fieldWithPath("data[].contentImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지 URL"),
                                fieldWithPath("data[].deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부")
                        )
                ));
    }

    @DisplayName("지도 피드 상세 조회 API")
    @Test
    void myMapListDetail() throws Exception {
        // given
        ContentResponse.LocationDetail content1 = ContentResponse.LocationDetail.builder()
                .contentId(1L)
                .userProfileImage("default.png")
                .groupId(1L)
                .location("삼성 서비스 센터")
                .content("테스트 내용")
                .groupImage("default.png")
                .groupName("테스트 그룹")
                .createAt("2023-12-24")
                .contentImageSize(2)
                .contentImageUrl("피드 대표 이미지 URL")
                .deletedYn(false)
                .build();

        ContentResponse.LocationDetail content2 = ContentResponse.LocationDetail.builder()
                .contentId(2L)
                .userProfileImage("default.png")
                .groupId(2L)
                .location("삼성 서비스 센터")
                .content("테스트 내용")
                .groupImage("default.png")
                .groupName("테스트 그룹")
                .createAt("2023-11-24")
                .contentImageSize(1)
                .contentImageUrl("피드 대표 이미지 URL")
                .deletedYn(false)
                .build();

        given(contentService.listDetailMyMap(anyString(), any()))
                .willReturn(List.of(content1, content2));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map/detail")
                                .header("Authorization", "JWT AccessToken")
                                .param("location", "테스트")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("content-mapDetail",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("location")
                                        .description("중복되는 지역의 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                        .description("피드 목록"),
                                fieldWithPath("data[].contentId").type(JsonFieldType.NUMBER)
                                        .description("피드 ID"),
                                fieldWithPath("data[].userProfileImage").type(JsonFieldType.STRING)
                                        .description("피드 작성 유저 프로필 사진"),
                                fieldWithPath("data[].groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data[].location").type(JsonFieldType.STRING)
                                        .description("피드 장소 이름"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING)
                                        .description("피드 내용"),
                                fieldWithPath("data[].groupImage").type(JsonFieldType.STRING)
                                        .description("피드가 작성된 그룹 이미지"),
                                fieldWithPath("data[].groupName").type(JsonFieldType.STRING)
                                        .description("피드가 작성된 그룹 이름"),
                                fieldWithPath("data[].createAt").type(JsonFieldType.STRING)
                                        .description("작성 날짜"),
                                fieldWithPath("data[].contentImageSize").type(JsonFieldType.NUMBER)
                                        .description("피드 총 이미지 수 / Integer"),
                                fieldWithPath("data[].contentImageUrl").type(JsonFieldType.STRING)
                                        .description("피드 이미지"),
                                fieldWithPath("data[].deletedYn").type(JsonFieldType.BOOLEAN)
                                        .description("피드 삭제 여부")
                                )
                ));
    }

    // method

    private static List<ContentResponse.ImageDetail> getImageDetails() {
        return List.of(ContentResponse.ImageDetail.builder()
                .id(1L)
                .contentId(1L)
                .imageName("테스트 이미지 이름")
                .imageUrl("테스트 이미지 URL")
                .build()
        );
    }

    private List<EmotionResponse.Detail> getEmotionDetails() {
        EmotionResponse.Detail emotion1 = EmotionResponse.Detail.builder()
                .id(1L)
                .emotionStatus(3L)
                .build();

        EmotionResponse.Detail emotion2 = EmotionResponse.Detail.builder()
                .id(2L)
                .emotionStatus(1L)
                .build();
        return List.of(emotion1, emotion2);
    }
}
