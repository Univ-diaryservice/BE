package univ_team1.dairyProject.controller;


import org.springframework.http.HttpHeaders;
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
@RequestMapping("/auth")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
        return securityService.signup(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return securityService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return securityService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return securityService.logout(token);
    }
}
