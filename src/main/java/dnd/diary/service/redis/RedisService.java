package dnd.diary.service.redis;

import dnd.diary.config.redis.RedisDao;
import dnd.diary.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static dnd.diary.enumeration.Result.REDIS_VALUE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisDao redisDao;

    public String getValues(String key) {
        String values = redisDao.getValues(key);

        if (values.isBlank()) {
            throw new CustomException(REDIS_VALUE_NOT_FOUND);
        }

        return values;
    }

    public void logoutFromRedis(String email, String accessToken, Long accessTokenExpiration) {
        String redisEmail = redisDao.getValues(email);
        redisDao.deleteValues(redisEmail);
        redisDao.setValues(accessToken, "logout", Duration.ofMillis(accessTokenExpiration));
    }
}
