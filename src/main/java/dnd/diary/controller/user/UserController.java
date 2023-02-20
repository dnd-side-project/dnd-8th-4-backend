package dnd.diary.controller.user;

import dnd.diary.dto.userDto.UserDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.user.UserSearchResponse;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("auth")
    public CustomResponseEntity<UserDto.RegisterDto> register(
            @Valid @RequestBody final UserDto.RegisterDto request
    ) {
        return CustomResponseEntity.success(userService.register(request));
    }

    @PostMapping("auth/login")
    public CustomResponseEntity<UserDto.LoginDto> login(
            @Valid @RequestBody final UserDto.LoginDto request
    ) {
        return CustomResponseEntity.success(userService.login(request));
    }

    @GetMapping("auth")
    public CustomResponseEntity<UserDto.InfoDto> userMyList(
    ) {
        return CustomResponseEntity.success(userService.findMyListUser());
    }

    @GetMapping("/user/search")
    public CustomResponseEntity<UserSearchResponse> searchUserList(@RequestParam String keyword) {
        return CustomResponseEntity.success(userService.searchUserList(keyword));
    }
}
