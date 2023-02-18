package dnd.diary.service;

import dnd.diary.config.Jwt.TokenProvider;
import dnd.diary.config.RedisDao;
import dnd.diary.domain.user.Authority;
import dnd.diary.domain.user.User;
import dnd.diary.dto.UserDto;
import dnd.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;

@Service
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
    public ResponseEntity<UserDto.RegisterDto> register(UserDto.RegisterDto request) {
        User user = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .name(request.getName())
                        .nickName(request.getNickName())
                        .phoneNumber(request.getPhoneNumber())
                        .profileImageUrl(request.getProfileImageUrl())
                        .authorities(Collections.singleton(authority))
                        .build()
        );


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String atk = tokenProvider.createToken(authentication);
        String rtk = tokenProvider.createRefreshToken(request.getEmail());

        redisDao.setValues(request.getEmail(), rtk, Duration.ofDays(14));

        return new ResponseEntity<>(UserDto.RegisterDto.response(
                user,
                atk,
                rtk
        ), HttpStatus.CREATED);
    }
}
