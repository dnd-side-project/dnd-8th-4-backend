package dnd.diary.controller.content;

import dnd.diary.controller.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerTest extends ControllerTestSupport {

    @DisplayName("북마크 프로세스 API")
    @Test
    void processBookmarkTransaction() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookmark")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}