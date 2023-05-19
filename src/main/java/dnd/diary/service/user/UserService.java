package dnd.diary.service.user;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserImage;
import dnd.diary.request.content.ContentDto;
import dnd.diary.request.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.content.BookmarkRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.repository.content.ContentRepository;
import dnd.diary.repository.user.UserImageRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.request.service.UserServiceRequest;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.user.UserResponse;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

import static dnd.diary.enumeration.Result.NOT_FOUND_USER_IMAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final UserImageRepository userImageRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final AmazonS3Client amazonS3Client;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisDao redisDao;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public UserResponse.CreateUser createUserAccount(UserServiceRequest.CreateUser request) {
        validateRegister(request);

        // 사용자 기본 프로필 추가
        User user = userRepository.save(
                createEntityUserFromDto(request)
        );

        // 토큰 발급
        String accessToken = tokenProvider.createToken(getAuthentication(request.getEmail(), request.getPassword()));
        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        return UserResponse.CreateUser.response(user, accessToken, refreshToken);
    }

    private String setDefaultProfileImage(String profileImageUrl) {
        String imageUrl = "";

        if (profileImageUrl.isBlank()) {
            int sampleGroupImageCount = userImageRepository.findAll().size();
            int randomIdx = getRandomNumber(1, sampleGroupImageCount);
            UserImage sampleUserImage = userImageRepository.findById((long) randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_USER_IMAGE));
            imageUrl = sampleUserImage.getUserImageUrl();
        }

        return imageUrl;
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
                tokenProvider.createRefreshToken(request.getEmail())
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
    public Page<UserDto.BookmarkDto> listMyBookmark(UserDetails userDetails, Integer page) {

        // 삭제된 게시글이 Exception을 일으키지 않도록 ContentId를 JPA로 얻어서 Content를 조회
        List<Long> contentIdList = bookmarkRepository.findContentIdList(getUser(userDetails.getUsername()).getId());
        Page<Content> bookmarkPage = contentRepository.findByIdInAndDeletedYn(
                contentIdList, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return bookmarkPage.map((Content content) -> UserDto.BookmarkDto.response(
                        content
                        , content.getContentImages()
                                .stream()
                                .map(ContentDto.ImageResponseDto::response)
                                .toList()
                        , Integer.parseInt(
                                redisDao.getValues(content.getId().toString())
                        )
                )
        );
    }

    @Transactional
    public Page<UserDto.myCommentListDto> listSearchMyComment(
            UserDetails userDetails, Integer page
    ) {
        User user = getUser(userDetails.getUsername());
        List<Long> distinctContentIdListByUserId = commentRepository.findDistinctContentIdListByUserId(user.getId());

        Page<Content> pageMyComment = contentRepository.findByIdInAndDeletedYn(
                distinctContentIdListByUserId, false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return pageMyComment.map((Content content) ->
                UserDto.myCommentListDto.response(
                        content,
                        content.getContentImages()
                                .stream()
                                .map(ContentDto.ImageResponseDto::response)
                                .toList(),
                        Integer.parseInt(redisDao.getValues(content.getId().toString()))
                )
        );
    }

    @Transactional
    public Page<UserDto.myContentListDto> listSearchMyContent(UserDetails userDetails, Integer page) {
        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        Page<Content> pageMyContent = contentRepository.findByUserIdAndDeletedYn(
                user.getId(), false, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "createdAt")
        );

        return pageMyContent.map((Content content) ->
                UserDto.myContentListDto.response(
                        content,
                        content.getContentImages()
                                .stream()
                                .map(ContentDto.ImageResponseDto::response)
                                .toList(),
                        Integer.parseInt(redisDao.getValues(content.getId().toString()))
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
    public UserDto.UpdateDto userUpdateProfile(
            UserDetails userDetails, String nickName, MultipartFile file
    ) {
        User user = getUser(userDetails.getUsername());
        String fileName = null;
        String fileUrl = null;

        if (file != null) {
            fileName = saveImage(file);
            fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        } else {
            int sampleGroupImageCount = userImageRepository.findAll().size();
            int randomIdx = getRandomNumber(1, sampleGroupImageCount);
            UserImage sampleUserImage = userImageRepository.findById((long) randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_USER_IMAGE));
            fileUrl = sampleUserImage.getUserImageUrl();
        }

        String beforeNickname = user.getNickName();
        if (nickName == null || "".equals(nickName)) {
            nickName = beforeNickname;
        }

        user.updateUserProfile(nickName, fileUrl);

        return UserDto.UpdateDto.response(user);
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

    public UserSearchResponse searchUserList(String keyword) {

        List<User> searchByKeywordList = userRepository.findByNickNameContainingIgnoreCase(keyword);
        List<UserSearchResponse.UserSearchInfo> userSearchInfoList = new ArrayList<>();

        for (User user : searchByKeywordList) {
            // 유저 목록 검색 시 프로필 이미지는 기본 이미지 랜덤 세팅
            int sampleGroupImageCount = userImageRepository.findAll().size();
            int randomIdx = getRandomNumber(1, sampleGroupImageCount);
            UserImage sampleUserImage = userImageRepository.findById((long) randomIdx).orElseThrow(() -> new CustomException(NOT_FOUND_USER_IMAGE));
            String imageUrl = sampleUserImage.getUserImageUrl();

            UserSearchResponse.UserSearchInfo userSearchInfo = UserSearchResponse.UserSearchInfo.builder()
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userNickName(user.getNickName())
                    .profileImageUrl(imageUrl)
                    .build();

            userSearchInfoList.add(userSearchInfo);
        }

        return UserSearchResponse.builder()
                .userSearchInfoList(userSearchInfoList)
                .build();
    }


    // Validate
    private void validateRegister(UserServiceRequest.CreateUser request) {
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

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private User createEntityUserFromDto(UserServiceRequest.CreateUser request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickName(request.getNickName())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(
                        setDefaultProfileImage(request.getProfileImageUrl())
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
}