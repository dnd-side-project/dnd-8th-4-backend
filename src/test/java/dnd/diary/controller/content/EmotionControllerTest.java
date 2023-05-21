package dnd.diary.controller.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.request.controller.content.EmotionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmotionControllerTest extends ControllerTestSupport {

    @DisplayName("공감 프로세스 API")
    @Test
    void processEmotionTransaction() throws Exception {
        // given
        EmotionRequest.Add request = new EmotionRequest.Add(2L);
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/content/{contentId}/emotion", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시글 공감 조회 API")
    @Test
    void listEmotion() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/{contentId}/emotion", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}