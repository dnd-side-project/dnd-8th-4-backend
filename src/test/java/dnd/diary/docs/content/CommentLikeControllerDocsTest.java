package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.CommentLikeController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.response.content.CommentLikeResponse;
import dnd.diary.service.content.CommentLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentLikeControllerDocsTest extends RestDocsSupport {

    private final CommentLikeService commentLikeService = mock(CommentLikeService.class);

    @Override
    protected Object initController() {
        return new CommentLikeController(commentLikeService);
    }

    @DisplayName("댓글 좋아요 프로세스 API / 좋아요 등록")
    @Test
    void processCommentLikeTransaction() throws Exception {
        // given
        given(commentLikeService.processCommentLikeTransaction(any(), anyLong()))
                .willReturn(CommentLikeResponse.builder()
                        .id(1L)
                        .userId(1L)
                        .commentId(1L)
                        .build());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/comment/like")
                                .header("Authorization", "JWT AccessToken")
                                .param("commentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("commentLike-add",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요한 유저 ID"),
                                fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요가 남겨진 댓글 ID")
                        )
                ));
    }

    @DisplayName("댓글 좋아요 프로세스 API / 좋아요 취소")
    @Test
    void processCommentLikeTransactionToCancel() throws Exception {
        // given
        given(commentLikeService.processCommentLikeTransaction(any(), anyLong()))
                .willReturn(new CommentLikeResponse());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/comment/like")
                                .header("Authorization", "JWT AccessToken")
                                .param("commentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("commentLike-cancel",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("좋아요 취소시 NULL"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요한 유저 ID"),
                                fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("좋아요가 남겨진 댓글 ID")
                        )
                ));
    }

}