package univ_team1.dairyProject.SecurityTest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import univ_team1.dairyProject.domain.User;
import univ_team1.dairyProject.dto.LoginRequest;
import univ_team1.dairyProject.dto.SignupRequest;
import univ_team1.dairyProject.repository.UserRepository;
import univ_team1.dairyProject.service.SecurityService;
import org.junit.jupiter.api.Test;
import univ_team1.dairyProject.util.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SecurityServiceTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USERNAME = "testUser";

    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void signupTest(){
        SignupRequest signupRequest = new SignupRequest();
        String email = "jinwook219@gmail.com";
        String password = "justpassword";
        String userNickname = "jinwook219";

        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setUserNickName(userNickname);

        ResponseEntity<String> response = securityService.signup(signupRequest);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("회원가입 성공", response.getBody());
    }


    @Test
    void loginTest(){
        SignupRequest signupRequest = new SignupRequest();
        String email = "jinwook219@gmail.com";
        String password = "justpassword";
        String userNickname = "jinwook219";

        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setUserNickName(userNickname);

        securityService.signup(signupRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        ResponseEntity<String> result = securityService.login(loginRequest);

        assertNotNull(result.getHeaders().getFirst("Access-Token"));  // 키 이름 일치
        assertNotNull(result.getHeaders().getFirst("Refresh-Token")); // 키 이름 일치
        assertEquals("로그인 성공", result.getBody());
    }


    @Test
    void logoutTest(){
        SignupRequest signupRequest = new SignupRequest();
        String email = "jinwook219@gmail.com";
        String password = "justpassword";
        String userNickname = "jinwook219";

        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setUserNickName(userNickname);

        securityService.signup(signupRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        ResponseEntity<String> result = securityService.login(loginRequest);

        String accessToken = result.getHeaders().getFirst("Access-Token");

        ResponseEntity logoutResult = securityService.logout(accessToken);
        Optional<User> user = userRepository.findByEmail(email);

        assertEquals("로그아웃 완료", logoutResult.getBody());
        assertNull(user.get().getAccessToken());
        assertNull(user.get().getRefreshToken());
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 새 액세스 토큰 발급 성공")
    void refreshToken_ValidToken_ReturnsNewAccessToken() {
        // given - 테스트 전에 데이터 정리
        userRepository.deleteAll();

        // 테스트용 사용자 및 토큰 생성
        String validRefreshToken = jwtUtil.createRefreshToken(TEST_EMAIL);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setUserName(TEST_USERNAME);
        user.setRefreshToken(validRefreshToken);

        // 사용자 저장
        userRepository.save(user);

        // when
        ResponseEntity<String> response = securityService.refresh(validRefreshToken);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("토큰 재발급 완료", response.getBody());

        HttpHeaders headers = response.getHeaders();
        assertTrue(headers.containsKey("Authorization"));
        assertTrue(headers.getFirst("Authorization").startsWith("Bearer "));

        // 테스트 후 정리
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 예외 발생")
    void refreshToken_InvalidToken_ThrowsException() {
        // given
        String invalidToken = "invalid.refresh.token";

        // when & then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> securityService.refresh(invalidToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("유효하지 않은 리프레시 토큰", exception.getReason());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 리프레시 토큰으로 예외 발생")
    void refreshToken_UserNotFound_ThrowsException() {
        // given - 테스트 전에 데이터 정리
        userRepository.deleteAll();

        // 유효한 토큰 생성 (하지만 DB에 해당 사용자는 저장하지 않음)
        String nonExistingUserToken = jwtUtil.createRefreshToken("nonexisting@example.com");

        // when & then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> securityService.refresh(nonExistingUserToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("사용자 없음", exception.getReason());
    }

    @Test
    @DisplayName("리프레시 토큰 불일치 시 예외 발생")
    void refreshToken_TokenMismatch_ThrowsException() {
        // given - 테스트 전에 데이터 정리
        userRepository.deleteAll();

        // 테스트용 사용자 생성 (다른 리프레시 토큰 저장)
        String savedRefreshToken = jwtUtil.createRefreshToken(TEST_EMAIL);
        // 확실히 다른 리프레시 토큰을 생성하기 위해 잠시 대기
        try {
            Thread.sleep(100); // 토큰 생성 사이에 약간의 지연 추가
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String differentRefreshToken = jwtUtil.createRefreshToken(TEST_EMAIL);

        // 확인용 출력 (실제 테스트에서는 제거)
        System.out.println("savedRefreshToken: " + savedRefreshToken);
        System.out.println("differentRefreshToken: " + differentRefreshToken);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setUserName(TEST_USERNAME);
        user.setRefreshToken(savedRefreshToken); // DB에는 이 토큰 저장

        // 사용자 저장
        User savedUser = userRepository.save(user);

        // DB에 정상적으로 저장됐는지 확인
        User retrievedUser = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        System.out.println("DB에 저장된 토큰: " + retrievedUser.getRefreshToken());

        // when & then (요청은 다른 토큰으로)
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> securityService.refresh(differentRefreshToken)
        );

        System.out.println("예외 상태 코드: " + exception.getStatusCode());
        System.out.println("예외 메시지: " + exception.getReason());


        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("토큰 불일치", exception.getReason());

        // 테스트 후 정리
        userRepository.deleteAll();
    }


}
