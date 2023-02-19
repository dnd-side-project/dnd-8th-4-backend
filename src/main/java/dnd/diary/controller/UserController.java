package dnd.diary.controller;

import dnd.diary.dto.UserDto;
import dnd.diary.response.CustomResponseEntity;
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
    public CustomResponseEntity<UserDto.RegisterDto> register(
            @Valid @RequestBody final UserDto.RegisterDto request
    ){
        return CustomResponseEntity.success(userService.register(request));
    }

    @PostMapping("auth/login")
    public CustomResponseEntity<UserDto.LoginDto> login(
            @Valid @RequestBody final UserDto.LoginDto request
    ){
        return CustomResponseEntity.success(userService.login(request));
    }

    @GetMapping("auth")
    public CustomResponseEntity<UserDto.InfoDto> userMyList(){
        return CustomResponseEntity.success(userService.findMyListUser());
    }
}
