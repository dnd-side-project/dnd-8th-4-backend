package dnd.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dnd.diary.config.Jwt.JwtAccessDeniedHandler;
import dnd.diary.config.Jwt.JwtAuthenticationEntryPoint;
import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.redis.RedisDao;
import dnd.diary.controller.content.*;
import dnd.diary.controller.user.UserController;
import dnd.diary.service.content.CommentLikeService;
import dnd.diary.service.content.CommentService;
import dnd.diary.service.content.ContentService;
import dnd.diary.service.content.EmotionService;
import dnd.diary.service.mission.MissionService;
import dnd.diary.service.user.UserService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.CorsFilter;

@WebMvcTest(controllers = {
        UserController.class,
        ContentController.class,
        CommentLikeController.class,
        CommentController.class,
        EmotionController.class,
        BookmarkController.class
})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected BookmarkController bookmarkController;

    @MockBean
    protected EmotionService emotionService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected MissionService missionService;

    @MockBean
    protected ContentService contentService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected CommentLikeService commentLikeService;

    @MockBean
    private RedisDao redisDao;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CorsFilter corsFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

}