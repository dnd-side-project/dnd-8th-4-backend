package dnd.diary;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.common.TestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
        return "배포테스트";
    }

    @GetMapping("/test")
    public CustomResponseEntity<TestResponse> testCheck() {
        return CustomResponseEntity.success(new TestResponse("테스트 메세지"));
    }
}
