package dnd.diary.controller;

import dnd.diary.common.ApiDocumentationTest;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static dnd.diary.utils.ApiDocumentUtils.getDocumentRequest;
import static dnd.diary.utils.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckControllerTest extends ApiDocumentationTest {

    @Test
    void testCheck() throws Exception {

        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/test")
        );

        resultActions.andExpect(status().isOk())
                .andDo(document("testCheck"
                        , getDocumentRequest()
                        , getDocumentResponse()
                        , responseFields(
                            beneathPath("data").withSubsectionId("data"),
                            fieldWithPath("result").type(JsonFieldType.STRING).description("테스트 메세지")
                        ))
                );
    }
}
