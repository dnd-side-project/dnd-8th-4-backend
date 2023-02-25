package dnd.diary.service.user;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.ContentImage;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.ContentDto;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.user.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final AmazonS3Client amazonS3Client;
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
        ).setParameter(1, user.getId());

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

    @Transactional
    public UserDto.UpdateDto userUpdateProfile(UserDetails userDetails, UserDto.UpdateDto request, MultipartFile file) {
        User user = getUser(userDetails.getUsername());
        String fileName = saveImage(file);
        return UserDto.UpdateDto.response(
                userRepository.save(
                        User.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .password(user.getPassword())
                                .name(user.getName())
                                .nickName(request.getNickName())
                                .phoneNumber(user.getPhoneNumber())
                                .profileImageUrl(amazonS3Client.getUrl(bucket,fileName).toString())
                                .level(user.getLevel())
                                .subLevel(user.getSubLevel())
                                .deleteAt(user.getDeleteAt())
                                .build()
                )
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

    private String saveImage(MultipartFile file) {
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        return fileName;
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }
}