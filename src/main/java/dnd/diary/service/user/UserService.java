package dnd.diary.service.user;

import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.user.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisDao redisDao;

    Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

    @Transactional
    public UserDto.RegisterDto register(UserDto.RegisterDto request) {
        validateRegister(request);

        return UserDto.RegisterDto.response(
                userRepository.save(
                        addUserFromRequest(request)
                ),
                tokenProvider.createToken(
                        getAuthentication(request.getEmail(), request.getPassword())
                ),
                getRtk(
                        request.getEmail()
                )
        );
    }

    @Transactional
    public UserDto.LoginDto login(UserDto.LoginDto request) {
        validateLogin(request);

        return UserDto.LoginDto.response(
                getUser(
                        request.getEmail()
                ),
                tokenProvider.createToken(
                        getAuthentication(request.getEmail(), request.getPassword())
                ),
                getRtk(
                        request.getEmail()
                )
        );
    }

    @Transactional
    public UserDto.InfoDto findMyListUser() {
        return UserDto.InfoDto.response(
                getUser(
                        SecurityContextHolder.getContext().getAuthentication().getName()
                )
        );
    }

    @Transactional
    public CustomResponseEntity<Result> emailCheckMatch(String email) {
        if (!userRepository.existsByEmail(email)) {
            return CustomResponseEntity.successEmailCheck();
        } else {
            throw new CustomException(Result.DUPLICATION_USER);
        }
    }

    @Transactional
    public CustomResponseEntity<Page<UserDto.BookmarkDto>> listMyBookmark(UserDetails userDetails, Integer page) {

        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserId(
                getUser(userDetails.getUsername()).getId(),
                PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        Page<UserDto.BookmarkDto> bookmarkDtoPage = bookmarkPage.map((Bookmark bookmark) -> UserDto.BookmarkDto.response(
                bookmark
                , bookmark.getContent().getContentImages()
                        .stream()
                        .map(ContentDto.ImageResponseDto::response)
                        .toList()
                , Integer.parseInt(
                        redisDao.getValues(bookmark.getContent().getId().toString())
                )
        )
        );
        return CustomResponseEntity.success(bookmarkDtoPage);
    }

    @Transactional
    public CustomResponseEntity<Page<UserDto.myCommentListDto>> listSearchMyComment(
            UserDetails userDetails, Integer page
    ) {
        User user = getUser(userDetails.getUsername());
        Query query = em.createNativeQuery(
                "" +
                        "SELECT DISTINCT content_id \n" +
                        "FROM comment AS c \n" +
                        "WHERE user_id = ?"
        ).setParameter(1,user.getId());

        List<Long> contentId = new ArrayList<>();
        List<BigInteger> contentIntegerId = query.getResultList();
        contentIntegerId.forEach(id ->
                contentId.add(id.longValue())
        );

        log.info(contentId.get(0).toString());
        log.info(contentId.get(0).getClass().toString());

        Page<Content> pageMyComment = contentRepository.findByIdIn(
                contentId, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return CustomResponseEntity.success(
                pageMyComment.map((Content content) ->
                        UserDto.myCommentListDto.response(
                                content,
                                content.getContentImages()
                                        .stream()
                                        .map(ContentDto.ImageResponseDto::response)
                                        .toList(),
                                Integer.parseInt(redisDao.getValues(content.getId().toString()))
                        )
                )
        );
    }

    @Transactional
    public CustomResponseEntity<Page<UserDto.myContentListDto>> listSearchMyContent(UserDetails userDetails, Integer page) {
        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        Page<Content> pageMyContent = contentRepository.findByUserId(
                user.getId(), PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return CustomResponseEntity.success(
                pageMyContent.map((Content content) ->
                        UserDto.myContentListDto.response(
                                content,
                                content.getContentImages()
                                        .stream()
                                        .map(ContentDto.ImageResponseDto::response)
                                        .toList(),
                                Integer.parseInt(redisDao.getValues(content.getId().toString()))
                        )
                )
        );
    }

    @Transactional
    public void logout(UserDetails userDetails, String auth) {
        String atk = auth.substring(7);
        if (redisDao.getValues(userDetails.getUsername()) != null) {
            redisDao.deleteValues(userDetails.getUsername());
        }
        redisDao.setValues(atk, "logout", Duration.ofMillis(
                        tokenProvider.getExpiration(atk)
                )
        );
    }

    @Transactional
    public void deleteUser(UserDetails userDetails, String auth) {
        String atk = auth.substring(7);
        if (redisDao.getValues(userDetails.getUsername()) != null) {
            redisDao.deleteValues(userDetails.getUsername());
        }
        redisDao.setValues(atk, "logout", Duration.ofMillis(
                        tokenProvider.getExpiration(atk)
                )
        );
        userRepository.delete(
                getUser(userDetails.getUsername())
        );
    }

    // method

    private User getUser(String email) {
        Optional<User> oneWithAuthoritiesByEmail = userRepository.
                findOneWithAuthoritiesByEmail(email);
        return oneWithAuthoritiesByEmail.orElseThrow(
                () -> new CustomException(Result.FAIL)
        );
    }
    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private User addUserFromRequest(UserDto.RegisterDto request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickName(request.getNickName())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())
                .authorities(Collections.singleton(authority))
                .build();
    }

    private String getRtk(String email) {
        String rtk = tokenProvider.createRefreshToken(
                email
        );
        redisDao.setValues(email, rtk, Duration.ofDays(14));
        return rtk;
    }

    public UserSearchResponse searchUserList(String keyword) {

        List<User> searchByKeywordList = userRepository.findByNickNameContainingIgnoreCase(keyword);
        List<UserSearchResponse.UserSearchInfo> userSearchInfoList = new ArrayList<>();

        for (User user : searchByKeywordList) {
            UserSearchResponse.UserSearchInfo userSearchInfo = UserSearchResponse.UserSearchInfo.builder()
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userNickName(user.getNickName())
                    .profileImageUrl("")   // default 이미지로 통일
                    .build();
            userSearchInfoList.add(userSearchInfo);
        }

        return UserSearchResponse.builder()
                .userSearchInfoList(userSearchInfoList)
                .build();
    }

    // Validate

    private void validateRegister(UserDto.RegisterDto request) {
        Boolean existsByEmail = userRepository.existsByEmail(request.getEmail());
        Boolean existsByNickName = userRepository.existsByNickName(request.getNickName());
        if (existsByEmail) {
            throw new CustomException(Result.DUPLICATION_USER);
        }
        if (existsByNickName) {
            throw new CustomException(Result.DUPLICATION_NICKNAME);
        }
    }

    private void validateLogin(
            UserDto.LoginDto request
    ) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(Result.NOT_FOUND_USER);
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                userRepository.findOneWithAuthoritiesByEmail(request.getEmail())
                        .orElseThrow(
                                () -> new CustomException(Result.NOT_MATCHED_ID_OR_PASSWORD)
                        ).getPassword())
        ) {
            throw new CustomException(Result.NOT_MATCHED_ID_OR_PASSWORD);
        }
    }
}