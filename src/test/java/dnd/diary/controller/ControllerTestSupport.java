package dnd.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dnd.diary.config.Jwt.JwtAccessDeniedHandler;
import dnd.diary.config.Jwt.JwtAuthenticationEntryPoint;
import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.redis.RedisDao;
import dnd.diary.controller.user.UserController;
import dnd.diary.service.mission.MissionService;
import dnd.diary.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.CorsFilter;

@WebMvcTest(controllers = {
        UserController.class
})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected MissionService missionService;

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