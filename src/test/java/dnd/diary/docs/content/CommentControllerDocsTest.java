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
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/{contentId}/comment", 1L)
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}