package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.CommentController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.request.controller.content.CommentRequest;
import dnd.diary.request.service.content.CommentServiceRequest;
import dnd.diary.response.content.CommentResponse;
import dnd.diary.service.content.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerDocsTest extends RestDocsSupport {

    private final CommentService commentService = mock(CommentService.class);

    @Override
    protected Object initController() {
        return new CommentController(commentService);
    }

    @DisplayName("피드 댓글 작성 API")
    @Test
    void addComment() throws Exception {
        // given
        CommentRequest.Add request = new CommentRequest.Add("테스트 댓글", null);

        given(commentService.commentAdd(any(), anyLong(), any(CommentServiceRequest.Add.class)))
                .willReturn(CommentResponse.Add.builder()
                        .id(1L)
                        .commentNote("테스트 댓글")
                        .userId(1L)
                        .contentId(1L)
                        .stickerId(null)
                        .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/content/{contentId}/comment", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("contentId")
                                        .description("피드 ID")
                        ),
                        requestFields(
                                fieldWithPath("commentNote").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("stickerId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("댓글 스티커 / 스티커 미사용시 NULL 허용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.commentNote").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("댓글 작성한 유저 ID"),
                                fieldWithPath("data.contentId").type(JsonFieldType.NUMBER)
                                        .description("댓글이 작성된 피드 ID"),
                                fieldWithPath("data.stickerId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("스티커 ID / Long")
                        )
                ));
    }

    @DisplayName("피드 댓글 조회 API")
    @Test
    void pageComment() throws Exception {
        // given
        CommentResponse.Detail comment1 = CommentResponse.Detail.builder()
                .id(1L)
                .userId(1L)
                .stickerImageUrl(null)
                .commentNote("테스트 댓글1")
                .profileImageUrl("default.png")
                .userName("테스트 계정1")
                .createdAt(LocalDateTime.of(2023, 11, 22, 13, 12))
                .likesExists(false)
                .build();

        CommentResponse.Detail comment2 = CommentResponse.Detail.builder()
                .id(2L)
                .userId(2L)
                .stickerImageUrl(null)
                .commentNote("테스트 댓글2")
                .profileImageUrl("default.png")
                .userName("테스트 계정2")
                .createdAt(LocalDateTime.of(2023, 11, 23, 11, 37))
                .likesExists(true)
                .build();


        given(commentService.commentPage(any(), anyLong(), anyInt()))
                .willReturn(new PageImpl<>(List.of(comment1, comment2), PageRequest.of(0, 2), 2));

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/content/{contentId}/comment", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-detailPage",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("contentId").description("피드 ID")
                        ),
                        requestParameters(
                                parameterWithName("page").description("요청 페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("댓글 목록"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("댓글 작성 유저 ID"),
                                fieldWithPath("data.content[].stickerImageUrl").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("스티커 이미지 URL"),
                                fieldWithPath("data.content[].commentNote").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("data.content[].profileImageUrl").type(JsonFieldType.STRING)
                                        .description("댓글 작성 유저 프로필 이미지 URL"),
                                fieldWithPath("data.content[].userName").type(JsonFieldType.STRING)
                                        .description("댓글 작성 유저 이름"),
                                fieldWithPath("data.content[].createdAt").type(JsonFieldType.ARRAY)
                                        .description("작성 날짜"),
                                fieldWithPath("data.content[].likesExists").type(JsonFieldType.BOOLEAN)
                                        .description("댓글 좋아요 여부"),
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
}