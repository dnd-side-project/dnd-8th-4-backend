package dnd.diary.controller;

import dnd.diary.dto.UserDto;
import dnd.diary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("auth")
    public ResponseEntity<UserDto.RegisterDto> register(
        @Valid @RequestBody final UserDto.RegisterDto request
    ){
        return userService.register(request);
    }

    @PostMapping("auth/login")
    public ResponseEntity<UserDto.LoginDto> login(
            @Valid @RequestBody final UserDto.LoginDto request
    ){
        return userService.login(request);
    }

    @GetMapping("auth")
    public ResponseEntity<UserDto.InfoDto> userMyList(){
        return userService.findMyListUser();
    }
}
