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

//        user.get().setAccessToken(accessToken);
        user.get().setRefreshToken(refreshToken);
        userRepository.save(user.get());

        return ResponseEntity.ok().headers(headers).body("로그인 성공");
    }



    public ResponseEntity<String> refresh(String refreshToken, String accessToken) {
        // 1. 액세스 토큰이 유효한지 먼저 체크
        if (jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.ok("액세스 토큰은 아직 유효합니다.");
        }

        // 2. 리프레시 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 리프레시 토큰");
        }

        // 3. 리프레시 토큰에서 이메일 추출
        String email = jwtUtil.extractEmail(refreshToken);

        // 4. 사용자 존재 여부 확인
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 없음");
        }

        // 5. 리프레시 토큰과 저장된 토큰 비교
        if (!refreshToken.equals(user.get().getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "토큰 불일치");
        }

        // 6. 새로운 액세스 토큰 발급
        String newAccessToken = jwtUtil.createAccessToken(email);

        // 7. 새로운 액세스 토큰을 클라이언트에게 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccessToken);

        return ResponseEntity.ok().headers(headers).body("새로운 액세스 토큰이 발급되었습니다.");
    }

    public ResponseEntity<String> logout(String accessToken) {
        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 없음"));
        user.setRefreshToken(null);
//        user.setAccessToken(null);
        userRepository.save(user);
        return ResponseEntity.ok("로그아웃 완료");
    }
}

