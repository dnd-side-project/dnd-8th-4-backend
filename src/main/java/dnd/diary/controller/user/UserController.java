package dnd.diary.controller.user;

import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
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

    // 회원가입
    @PostMapping("auth")
    public CustomResponseEntity<UserDto.RegisterDto> register(
            @Valid @RequestBody final UserDto.RegisterDto request
    ) {
        return CustomResponseEntity.success(userService.register(request));
    }

    // 이메일 중복 검사
    @GetMapping("auth/check")
    public CustomResponseEntity<Result> checkMatchEmail(
        @RequestParam final String email
    ){
        return userService.emailCheckMatch(email);
    }

    // 로그인
    @PostMapping("auth/login")
    public CustomResponseEntity<UserDto.LoginDto> login(
            @Valid @RequestBody final UserDto.LoginDto request
    ) {
        return CustomResponseEntity.success(userService.login(request));
    }

    // 정보 조회
    @GetMapping("auth")
    public CustomResponseEntity<UserDto.InfoDto> userMyList(
    ) {
        return CustomResponseEntity.success(userService.findMyListUser());
    }

    // 유저 검색
    @GetMapping("/user/search")
    public CustomResponseEntity<UserSearchResponse> searchUserList(@RequestParam String keyword) {
        return CustomResponseEntity.success(userService.searchUserList(keyword));
    }
}
