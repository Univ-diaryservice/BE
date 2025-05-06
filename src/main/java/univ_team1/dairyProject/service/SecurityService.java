package univ_team1.dairyProject.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import univ_team1.dairyProject.domain.UserEntity;
import univ_team1.dairyProject.dto.LoginRequest;
import univ_team1.dairyProject.dto.SignupRequest;
import univ_team1.dairyProject.repository.SecurityRepository;
import univ_team1.dairyProject.util.JwtUtil;

import java.util.Optional;

@Service
public class SecurityService {
    public SecurityService(SecurityRepository securityRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.securityRepository = securityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private final SecurityRepository securityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;




    public ResponseEntity<String> signup(SignupRequest SignupRequest) {
        if (securityRepository.findByEmail(SignupRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다");
        }

//        if (SignupRequest.getPassword().length() < 10) {
//            throw new RuntimeException("Password must be at least 10 characters");
//        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(SignupRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(SignupRequest.getPassword()));
        securityRepository.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }
    
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        Optional<UserEntity> userEntity = securityRepository.findByEmail(loginRequest.getEmail());
        if (securityRepository.findByEmail(loginRequest.getEmail()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다");
        } else if (passwordEncoder.matches(loginRequest.getPassword(), userEntity.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }


        String accessToken = jwtUtil.createAccessToken(loginRequest.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(loginRequest.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", accessToken);
        headers.add("Refresh-Token", refreshToken);

        return ResponseEntity.ok().headers(headers).body("로그인 성공");


    }
    



}
