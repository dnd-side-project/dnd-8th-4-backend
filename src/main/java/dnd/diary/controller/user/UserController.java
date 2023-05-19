package dnd.diary.controller.user;

import java.util.List;

import dnd.diary.request.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.request.controller.user.UserRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.mission.MissionResponse;
import dnd.diary.response.user.UserResponse;
import dnd.diary.response.user.UserSearchResponse;
import dnd.diary.service.mission.MissionService;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MissionService missionService;

    // 회원가입
    @PostMapping("auth")
    public CustomResponseEntity<UserResponse.CreateUser> createUserAccount(
            @Valid @RequestBody final UserRequest.CreateUser request
    ) {
        return CustomResponseEntity.success(userService.createUserAccount(request.toServiceRequest()));
    }

    // 프로필 수정
    @PatchMapping("auth")
    public CustomResponseEntity<UserDto.UpdateDto> updateProfileUser(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(required = false) final String nickName,
            @RequestPart(required = false) final MultipartFile file
    ) {
        return CustomResponseEntity.success(userService.userUpdateProfile(userDetails,nickName,file));
    }

    // 로그인
    @PostMapping("auth/login")
    public CustomResponseEntity<UserDto.LoginDto> login(
            @Valid @RequestBody final UserDto.LoginDto request
    ) {
        return CustomResponseEntity.success(userService.login(request));
    }

    // 로그아웃
    @PostMapping("auth/logout")
    public CustomResponseEntity<Void> login(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestHeader(value = "Authorization") String auth
    ) {
        userService.logout(userDetails, auth);
        return CustomResponseEntity.successLogout();
    }

    // 회원 탈퇴
    @DeleteMapping("auth")
    public CustomResponseEntity<Void> userDelete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestHeader(value = "Authorization") String auth
    ) {
        userService.deleteUser(userDetails, auth);
        return CustomResponseEntity.successDelete();
    }

    // 정보 조회
    @GetMapping("auth/my/info")
    public CustomResponseEntity<UserDto.InfoDto> userMyList(
    ) {
        return CustomResponseEntity.success(userService.findMyListUser());
    }

    // 유저 검색
    @GetMapping("/user/search")
    public CustomResponseEntity<UserSearchResponse> searchUserList(@RequestParam String keyword) {
        return CustomResponseEntity.success(userService.searchUserList(keyword));
    }

    // 북마크 글 조회
    @GetMapping("auth/my/bookmark")
    public CustomResponseEntity<Page<UserDto.BookmarkDto>> myBookmarkList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listMyBookmark(userDetails, page));
    }

    // 작성한 글 조회
    @GetMapping("auth/my/content")
    public CustomResponseEntity<Page<UserDto.myContentListDto>> searchMyContentList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listSearchMyContent(userDetails, page));
    }
    
    // 작성한 댓글 조회
    @GetMapping("auth/my/comment")
    public CustomResponseEntity<Page<UserDto.myCommentListDto>> searchMyCommentList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(userService.listSearchMyComment(userDetails, page));
    }

    // 이메일 중복 검사
    @GetMapping("auth/check")
    public CustomResponseEntity<Result> checkMatchEmail(
            @RequestParam final String email
    ) {
        return userService.emailCheckMatch(email);
    }

    // 완료한 미션 조회
    @GetMapping("auth/my/mission/complete")
    public CustomResponseEntity<List<MissionResponse>> getCompleteMissionList() {
        return CustomResponseEntity.success(missionService.getCompleteMissionList());
    }
}
