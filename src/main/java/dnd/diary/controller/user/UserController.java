package dnd.diary.controller.user;

import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.user.UserResponse;
import dnd.diary.response.user.UserSearchResponse;
import dnd.diary.service.mission.MissionService;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("auth")
    public CustomResponseEntity<UserResponse.Login> createUserAccount(
            @RequestBody @Valid final UserRequest.CreateUser request
    ) {
        return CustomResponseEntity.success(userService.createUserAccount(request.toServiceRequest()));
    }

    // 로그인
    @PostMapping("auth/login")
    public CustomResponseEntity<UserResponse.Login> login(
            @RequestBody final UserRequest.Login request
    ) {
        return CustomResponseEntity.success(userService.login(request.toServiceRequest()));
    }

    // 정보 조회
    @GetMapping("auth/my/info")
    public CustomResponseEntity<UserResponse.Detail> findMyListUser(
            @AuthenticationPrincipal Long userId
    ) {
        return CustomResponseEntity.success(userService.findMyListUser(userId));
    }

    // 프로필 수정
    @PatchMapping("auth")
    public CustomResponseEntity<UserResponse.Update> updateProfileUser(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) final String nickName,
            @RequestPart(required = false) final MultipartFile file
    ) {
        return CustomResponseEntity.success(userService.userUpdateProfile(userId, nickName, file));
    }

    // 로그아웃
    @PostMapping("auth/logout")
    public CustomResponseEntity<Boolean> login(
            @AuthenticationPrincipal final Long userId,
            @RequestHeader(value = "Authorization") String auth
    ) {
        return CustomResponseEntity.success(userService.logout(userId, auth.substring(7)));
    }

    // 회원 탈퇴
    @DeleteMapping("auth")
    public CustomResponseEntity<Boolean> userDelete(
            @AuthenticationPrincipal final Long userId,
            @RequestHeader(value = "Authorization") String auth
    ) {
        return CustomResponseEntity.success(userService.deleteUser(userId, auth.substring(7)));
    }

    // 유저 검색
    @GetMapping("/user/search")
    public CustomResponseEntity<List<UserSearchResponse.UserSearchInfo>> searchUserList(
            @RequestParam String keyword
    ) {
        return CustomResponseEntity.success(userService.searchUserList(keyword));
    }

    // 북마크 글 조회
    @GetMapping("auth/my/bookmark")
    public CustomResponseEntity<Page<UserResponse.ContentList>> myBookmarkList(
            @AuthenticationPrincipal Long userId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listMyBookmark(userId, page));
    }

    // 작성한 글 조회
    @GetMapping("auth/my/content")
    public CustomResponseEntity<Page<UserResponse.ContentList>> searchMyContentList(
            @AuthenticationPrincipal Long userId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listSearchMyContent(userId, page));
    }

    // 작성한 댓글 조회
    @GetMapping("auth/my/comment")
    public CustomResponseEntity<Page<UserResponse.ContentList>> searchMyCommentList(
            @AuthenticationPrincipal Long userId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listSearchMyComment(userId, page));
    }

    // 이메일 중복 검사
    @GetMapping("auth/check")
    public CustomResponseEntity<Boolean> checkMatchEmail(
            @RequestParam final String email
    ) {
        return CustomResponseEntity.success(userService.emailCheckMatch(email));
    }

}
