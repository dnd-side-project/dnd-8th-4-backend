package dnd.diary.controller.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.request.controller.content.CommentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends ControllerTestSupport {

    @DisplayName("피드 댓글 작성 API")
    @Test
    void addComment() throws Exception {
        // given
        CommentRequest.Add request = new CommentRequest.Add("test", 1L);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/content/{contentId}/comment", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
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