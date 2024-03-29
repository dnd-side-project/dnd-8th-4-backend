package dnd.diary.service.user;

import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserImage;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.user.UserImageRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.service.UserServiceRequest;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.response.user.UserResponse;
import dnd.diary.response.user.UserSearchResponse;
import dnd.diary.service.redis.RedisService;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static dnd.diary.enumeration.Result.NOT_FOUND_USER;
import static dnd.diary.enumeration.Result.NOT_FOUND_USER_IMAGE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final UserImageRepository userImageRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final S3Service s3Service;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public UserResponse.Login createUserAccount(UserServiceRequest.CreateUser request) {
        validateDuplicateNickName(request.getNickName());

        User user = userRepository.save(
                createEntityUserFromDto(request)
        );

        // 토큰 발급
        String accessToken = tokenProvider.createToken(
                user.getId(), getAuthentication(request.getEmail(), request.getPassword()));
        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        return UserResponse.Login.response(user, accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserResponse.Login login(UserServiceRequest.Login request) {
        validateMatchingPasswords(request.getEmail(), request.getPassword());

        User user = userRepository.findOneWithAuthoritiesByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_USER)
                );

        // 토큰 발급
        String accessToken = tokenProvider.createToken(
                user.getId(), getAuthentication(request.getEmail(), request.getPassword())
        );
        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        return UserResponse.Login.response(user, accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserResponse.Detail findMyListUser(Long userId) {
        User user = getUser(userId);
        return UserResponse.Detail.response(user);
    }

    @Transactional(readOnly = true)
    public Boolean emailCheckMatch(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse.ContentList> listMyBookmark(Long userId, Integer page) {
        // 삭제된 게시글이 Exception을 일으키지 않도록 ContentId를 QueryDSL로 얻어서 Content를 조회
        List<Long> contentIdList = contentRepository.findContentIdList(userId);
        return getContentLists(page, contentIdList);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse.ContentList> listSearchMyComment(Long userId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt");
        Page<Content> contentPage = contentRepository.searchMyCommentPosts(userId,pageRequest);

        return contentPage.map((Content content) ->
                UserResponse.ContentList.response(content, getViews(content))
        );
    }

    @Transactional(readOnly = true)
    public Page<UserResponse.ContentList> listSearchMyContent(Long userId, Integer page) {
        PageRequest pageable = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt");
        Page<Content> pages = contentRepository.findByUserIdAndDeletedYn(userId, false, pageable);

        return pages.map((Content content) ->
                UserResponse.ContentList.response(content, getViews(content)));
    }

    @Transactional(readOnly = true)
    public Boolean logout(Long userId, String accessToken) {
        String email = getUser(userId).getEmail();
        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);

        return redisService.logoutFromRedis(email, accessToken, accessTokenExpiration);
    }

    @Transactional
    public Boolean deleteUser(Long userId, String accessToken) {
        User user = getUser(userId);
        String email = getUser(userId).getEmail();

        userRepository.delete(user);

        return redisService.logoutFromRedis(email, accessToken, tokenProvider.getExpiration(accessToken));
    }

    @Transactional
    public UserResponse.Update userUpdateProfile(
            Long userId, String nickName, MultipartFile file
    ) {
        User user = getUser(userId);

        // 이미지 설정
        String fileUrl = (file != null) ?
                s3Service.saveProfileImage(file) : setDefaultProfileImage();

        user.updateUserProfile(nickName, fileUrl);

        return UserResponse.Update.response(user);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse.UserSearchInfo> searchUserList(String keyword) {
        return userRepository.searchNickname(keyword);
    }


    // method
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );
    }

    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private int getRandomNumber(int max) {
        return (int) ((Math.random() * (max - 1)) + 1);
    }

    private User createEntityUserFromDto(UserServiceRequest.CreateUser request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickName(request.getNickName())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(
                        // 사용자 기본 프로필 추가
                        setDefaultProfileImage()
                )
                .authorities(
                        getAuthorities()
                )
                .mainLevel(1L)
                .subLevel(0.0)
                .build();
    }

    private static Set<Authority> getAuthorities() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private String setDefaultProfileImage() {
        String imageUrl = "";

        int sampleGroupImageCount = userImageRepository.findAll().size();
        int randomIdx = getRandomNumber(sampleGroupImageCount);
        UserImage sampleUserImage = userImageRepository.findById((long) randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_USER_IMAGE));
        imageUrl = sampleUserImage.getUserImageUrl();

        return imageUrl;
    }

    private Page<UserResponse.ContentList> getContentLists(Integer page, List<Long> distinctContentIdListByUserId) {
        Page<Content> pageMyComment = contentRepository.findByIdInAndDeletedYn(
                distinctContentIdListByUserId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );
        return pageMyComment.map((Content content) ->
                UserResponse.ContentList.response(content, getViews(content))
        );
    }

    // Validate

    private void validateDuplicateNickName(String nickName) {
        Boolean existsByNickName = userRepository.existsByNickName(nickName);
        if (existsByNickName) {
            throw new CustomException(Result.DUPLICATION_NICKNAME);
        }
    }
    private void validateMatchingPasswords(String email, String enteredPassword) {
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(Result.NOT_MATCHED_ID_OR_PASSWORD)
        );
        if (passwordEncoder.matches(enteredPassword, findUser.getPassword()) == false) {
            throw new CustomException(Result.NOT_MATCHED_ID_OR_PASSWORD);
        }
    }

    private Integer getViews(Content content) {
        return redisService.getValuesInteger(content.getId());
    }
}