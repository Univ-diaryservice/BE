package univ_team1.dairyProject.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import univ_team1.dairyProject.dto.LoginRequest;
import univ_team1.dairyProject.dto.SignupRequest;
import univ_team1.dairyProject.service.SecurityService;


@Controller
@Tag(name = "시큐리티 api", description = "회원가입, 로그인, 로그아웃, 리프레시")
@RequestMapping("/auth")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }


    @Operation(summary = "회원가입", description = "회원가입 dto")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
        return securityService.signup(signupRequest);
    }

    @Operation(summary = "로그인", description = "로그인 요청")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return securityService.login(loginRequest);
    }

    @Operation(summary = "리프레시 토큰", description = "리프레시 토큰을 이용해 새로운 액세스 토큰 발급")
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return securityService.refresh(refreshToken);
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃 (Bearer 토큰 필요)")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
        String token = authHeader.replaceFirst("(?i)^Bearer ", "");
        return securityService.logout(token);
    }
}
