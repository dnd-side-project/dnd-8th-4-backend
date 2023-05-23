package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.EmotionController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.request.controller.content.EmotionRequest;
import dnd.diary.request.service.content.EmotionServiceRequest;
import dnd.diary.response.content.EmotionResponse;
import dnd.diary.service.content.EmotionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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

class EmotionControllerDocsTest extends RestDocsSupport {

    private final EmotionService emotionService = mock(EmotionService.class);

    @Override
    protected Object initController() {
        return new EmotionController(emotionService);
    }

    @DisplayName("공감 프로세스 API / (등록 및 변경시)")
    @Test
    void processEmotionTransactionToAddAndUpdate() throws Exception {
        // given
        EmotionRequest.Add request = new EmotionRequest.Add(2L);

        given(emotionService.processEmotionTransaction(any(), anyLong(), any(EmotionServiceRequest.Add.class)))
                .willReturn(EmotionResponse.Add.builder()
                        .id(1L)
                        .userId(1L)
                        .contentId(1L)
                        .emotionYn(true)
                        .emotionStatus(2L)
                        .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/content/{contentId}/emotion", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("emotion-addAndUpdate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("contentId").description("피드 Id")
                        ),
                        requestFields(
                                fieldWithPath("emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("공감 유형 / Long")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감 ID"),
                                fieldWithPath("data.emotionStatus").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감 유형"),
                                fieldWithPath("data.contentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감된 피드 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감한 유저 ID"),
                                fieldWithPath("data.emotionYn").type(JsonFieldType.BOOLEAN)
                                        .optional()
                                        .description("공감 여부")
                        )
                ));
    }

    @DisplayName("공감 프로세스 API / (등록 및 변경시)")
    @Test
    void processEmotionTransactionToCancel() throws Exception {
        // given
        EmotionRequest.Add request = new EmotionRequest.Add(2L);

        given(emotionService.processEmotionTransaction(any(), anyLong(), any(EmotionServiceRequest.Add.class)))
                .willReturn(new EmotionResponse.Add());

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/content/{contentId}/emotion", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("emotion-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("contentId").description("피드 Id")
                        ),
                        requestFields(
                                fieldWithPath("emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("공감 유형 / Long")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("공감 취소시 NULL"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감 ID"),
                                fieldWithPath("data.emotionStatus").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감 유형"),
                                fieldWithPath("data.contentId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감된 피드 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("공감한 유저 ID"),
                                fieldWithPath("data.emotionYn").type(JsonFieldType.BOOLEAN)
                                        .optional()
                                        .description("공감 여부")
                        )
                ));
    }

    @DisplayName("게시글 공감 조회 API")
    @Test
    void listEmotion() throws Exception {
        EmotionResponse.Detail emotion1 = EmotionResponse.Detail.builder()
                .id(1L)
                .emotionStatus(2L)
                .build();

        EmotionResponse.Detail emotion2 = EmotionResponse.Detail.builder()
                .id(2L)
                .emotionStatus(1L)
                .build();

        // given
        given(emotionService.emotionList(anyLong()))
                .willReturn(List.of(emotion1, emotion2));

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/content/{contentId}/emotion", 1L)
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("emotion-list",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("contentId").description("피드 id")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                        .description("공감 목록"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("공감 ID"),
                                fieldWithPath("data[].emotionStatus").type(JsonFieldType.NUMBER)
                                        .description("공감 유형 / Long")
                        )
                ));
    }
}