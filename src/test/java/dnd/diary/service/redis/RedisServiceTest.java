package dnd.diary.service.redis;

import dnd.diary.config.redis.RedisDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisDao redisDao;

    @DisplayName("Redis에 값을 삭제 및 로그아웃 처리를 한다.")
    @Test
    void logoutFromRedis() {
        // given
        redisDao.setValues("test_email", "1");

        // when
        Boolean response = redisService.logoutFromRedis("test_email", "accessToken", 10000L);

        // then
        assertThat(response).isTrue();

        String deleteValues = redisDao.getValues("test_email");
        assertThat(deleteValues == null).isTrue();

        String insertValues = redisDao.getValues("accessToken");
        assertThat(insertValues).isEqualTo("logout");
    }
}