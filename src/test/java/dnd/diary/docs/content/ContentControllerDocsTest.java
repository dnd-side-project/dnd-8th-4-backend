package dnd.diary.docs.content;

import dnd.diary.controller.content.ContentController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.service.content.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group")
                                .param("groupId", "1")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("그룹 전체 피드 조회 API")
    @Test
    void contentGroupAllList() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/all")
                                .param("groupId", groupIdArray)
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 검색 조회 API")
    @Test
    void searchContent() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/search")
                                .param("groupId", groupIdArray)
                                .param("word", "a")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("지도 피드 검색 API")
    @Test
    void myMapList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map")
                                .param("startLatitude", "1")
                                .param("startLongitude", "1")
                                .param("endLatitude", "1")
                                .param("endLongitude", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("지도 피드 상세 조회 API")
    @Test
    void myMapListDetail() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map/detail")
                                .param("location", "테스트")
                )
                .andDo(print())
                .andExpect(status().isOk());
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
}
