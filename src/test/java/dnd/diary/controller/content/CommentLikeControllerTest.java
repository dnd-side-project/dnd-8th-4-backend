package dnd.diary.controller.content;

import dnd.diary.controller.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentLikeControllerTest extends ControllerTestSupport {

    @DisplayName("댓글 좋아요 프로세스 API")
    @Test
    void processCommentLikeTransaction() throws Exception {
        // when // then
        mockMvc.perform(
                MockMvcRequestBuilders.get("/content/comment/like")
                        .param("commentId","1")
        )
                .andDo(print())
                .andExpect(status().isOk());
    }
}