package dnd.diary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
<<<<<<< HEAD
        return "배포 테스트";
=======
        return "됐나?";
>>>>>>> kevin
    }
}
