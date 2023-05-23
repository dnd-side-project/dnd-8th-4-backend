package dnd.diary.docs.content;

import dnd.diary.controller.ControllerTestSupport;
import dnd.diary.controller.content.CommentLikeController;
import dnd.diary.docs.RestDocsSupport;
import dnd.diary.service.content.CommentLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentLikeControllerDocsTest extends RestDocsSupport {

    private final CommentLikeService commentLikeService = mock(CommentLikeService.class);

    @Override
    protected Object initController() {
        return new CommentLikeController(commentLikeService);
    }

    @DisplayName("댓글 좋아요 프로세스 API")
    @Test
    void processCommentLikeTransaction() throws Exception {
        // given
        given(commentLikeService.processCommentLikeTransaction())

        // when // then
        mockMvc.perform(
                MockMvcRequestBuilders.get("/content/comment/like")
                        .param("commentId","1")
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

}