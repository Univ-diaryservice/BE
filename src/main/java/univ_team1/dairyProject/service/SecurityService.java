package univ_team1.dairyProject.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import univ_team1.dairyProject.domain.User;
import univ_team1.dairyProject.dto.LoginRequest;
import univ_team1.dairyProject.dto.SignupRequest;
import univ_team1.dairyProject.repository.UserRepository;
import univ_team1.dairyProject.util.JwtUtil;
import java.util.Optional;


@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SecurityService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public ResponseEntity<String> signup(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다");
        }

//        if (SignupRequest.getPassword().length() < 10) {
//            throw new RuntimeException("Password must be at least 10 characters");
//        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserName(signupRequest.getUserNickName());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }
    
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다");
        } else if (!passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }


        String accessToken = jwtUtil.createAccessToken(loginRequest.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(loginRequest.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", accessToken);
        headers.add("Refresh-Token", refreshToken);

        user.get().setAccessToken(accessToken);
        user.get().setRefreshToken(refreshToken);
        userRepository.save(user.get());

        return ResponseEntity.ok().headers(headers).body("로그인 성공");
    }



    public ResponseEntity<String> refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰");
        }
        String email = jwtUtil.extractEmail(refreshToken);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 없음");
        }


        if (!refreshToken.equals(user.get().getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰 불일치");
        }
        String newAccess = jwtUtil.createAccessToken(email);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccess);
        return ResponseEntity.ok().headers(headers).body("토큰 재발급 완료");
    }

    public ResponseEntity<String> logout(String accessToken) {
        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 없음"));
        user.setRefreshToken(null);
        user.setAccessToken(null);
        userRepository.save(user);
        return ResponseEntity.ok("로그아웃 완료");
    }
}

