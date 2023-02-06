package dnd.diary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionTest {

    @GetMapping("/test")
    public String ec2ConnectionTest() {
        return "Hello world!";
    }
}
