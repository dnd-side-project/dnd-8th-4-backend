package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.BookmarkController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.service.content.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerDocsTest extends RestDocsSupport {

    private final BookmarkService bookmarkService = mock(BookmarkService.class);

    @Override
    protected Object initController() {
        return new BookmarkController(bookmarkService);
    }

    @DisplayName("북마크 프로세스 API")
    @Test
    void processBookmarkTransaction() throws Exception {
        // given
        given(bookmarkService.processBookmarkTransaction(any(),anyLong()))
                .willReturn()

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookmark")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}