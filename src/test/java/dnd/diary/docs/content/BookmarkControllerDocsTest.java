package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.BookmarkController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.request.controller.BookmarkResponse;
import dnd.diary.service.content.BookmarkService;
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

class BookmarkControllerDocsTest extends RestDocsSupport {

    private final BookmarkService bookmarkService = mock(BookmarkService.class);

    @Override
    protected Object initController() {
        return new BookmarkController(bookmarkService);
    }

    @DisplayName("북마크 프로세스 API (등록)")
    @Test
    void processBookmarkTransaction() throws Exception {
        // given
        given(bookmarkService.processBookmarkTransaction(any(), anyLong()))
                .willReturn(BookmarkResponse.builder()
                        .id(1L)
                        .userId(1L)
                        .contentId(1L)
                        .build());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("bookmark-add",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("contentId").description("피드 id")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크한 유저 ID"),
                                fieldWithPath("data.contentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크한 피드 ID")
                        )
                ));
    }

    @DisplayName("북마크 프로세스 API (취소)")
    @Test
    void processBookmarkTransactionToCancel() throws Exception {
        // given
        given(bookmarkService.processBookmarkTransaction(any(), anyLong()))
                .willReturn(new BookmarkResponse());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("bookmark-cancel",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("contentId").description("피드 id")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("북마크 취소시 NULL"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크한 유저 ID"),
                                fieldWithPath("data.contentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("북마크한 피드 ID")
                        )
                ));
    }
}