package dnd.diary.service.user;

import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.response.user.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

        List<User> searchByKeywordList = userRepository.findByNickNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        List<UserSearchResponse.UserSearchInfo> userSearchInfoList = new ArrayList<>();

        for (User user : searchByKeywordList) {
            UserSearchResponse.UserSearchInfo userSearchInfo = UserSearchResponse.UserSearchInfo.builder()
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userNickName(user.getNickName())
                    .profileImageUrl(user.getProfileImageUrl())
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
        if(existsByEmail){
            throw new CustomException(Result.DUPLICATION_USER);
        }
        if (existsByNickName){
            throw new CustomException(Result.DUPLICATION_NICKNAME);
        }
    }
}