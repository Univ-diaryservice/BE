package univ_team1.dairyProject.SecurityTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import univ_team1.dairyProject.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void createAccessTokenTest() {
        // when
        String accessToken = jwtUtil.createAccessToken(testEmail);

        // then
        assertNotNull(accessToken);
        assertTrue(accessToken.length() > 20); // 실제 토큰인지 대략적으로 확인
        assertTrue(jwtUtil.validateToken(accessToken)); // 생성된 토큰이 유효한지 확인
        assertEquals(testEmail, jwtUtil.extractEmail(accessToken)); // 이메일이 정확히 인코딩되었는지 확인
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createRefreshTokenTest() {
        // when
        String refreshToken = jwtUtil.createRefreshToken(testEmail);

        // then
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 20);
        assertTrue(jwtUtil.validateToken(refreshToken));
        assertEquals(testEmail, jwtUtil.extractEmail(refreshToken));
    }

    @Test
    @DisplayName("이메일 추출 테스트")
    void extractEmailTest() {
        // given
        String accessToken = jwtUtil.createAccessToken(testEmail);

        // when
        String extractedEmail = jwtUtil.extractEmail(accessToken);

        // then
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 유효한 토큰")
    void validateTokenTest_validToken() {
        // given
        String accessToken = jwtUtil.createAccessToken(testEmail);

        // when & then
        assertTrue(jwtUtil.validateToken(accessToken));
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 유효하지 않은 토큰")
    void validateTokenTest_invalidToken() {
        // given
        String invalidToken = "invalid.token.string";

        // when & then
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 만료된 토큰")
    void validateTokenTest_expiredToken() throws Exception {
        // given
        // 리플렉션을 사용하여 private 필드에 직접 접근하는 대신, 만료된 토큰을 직접 생성
        String expiredToken = Jwts.builder()
                .setSubject(testEmail)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 과거 시간 설정
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, "mysecretkey") // JwtUtil의 secret과 동일하게
                .compact();

        // when & then
        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - 액세스 토큰 유효")
    void verifyAndRefreshTokensTest_validAccessToken() {
        // given
        String accessToken = jwtUtil.createAccessToken(testEmail);
        String refreshToken = jwtUtil.createRefreshToken(testEmail);

        // when
        String resultToken = jwtUtil.verifyAndRefreshTokens(accessToken, refreshToken);

        // then
        assertEquals(accessToken, resultToken); // 액세스 토큰이 유효하면 그대로 반환
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - 액세스 토큰 만료, 리프레시 토큰 유효")
    void verifyAndRefreshTokensTest_expiredAccessValidRefresh() throws Exception {
        // given
        String expiredAccessToken = Jwts.builder()
                .setSubject(testEmail)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 과거 시간 설정
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, "mysecretkey")
                .compact();
        String refreshToken = jwtUtil.createRefreshToken(testEmail);

        // when
        String newAccessToken = jwtUtil.verifyAndRefreshTokens(expiredAccessToken, refreshToken);

        // then
        assertNotEquals(expiredAccessToken, newAccessToken); // 새 토큰이 발급되었는지 확인
        assertTrue(jwtUtil.validateToken(newAccessToken)); // 새 토큰이 유효한지 확인
        assertEquals(testEmail, jwtUtil.extractEmail(newAccessToken)); // 새 토큰에 이메일이 정확히 인코딩되었는지 확인
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - 둘 다 만료")
    void verifyAndRefreshTokensTest_bothExpired() throws Exception {
        // given
        String expiredAccessToken = Jwts.builder()
                .setSubject(testEmail)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, "mysecretkey")
                .compact();
        String expiredRefreshToken = Jwts.builder()
                .setSubject(testEmail)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, "mysecretkey")
                .compact();

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jwtUtil.verifyAndRefreshTokens(expiredAccessToken, expiredRefreshToken);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("토큰이 유효하지 않습니다", exception.getReason());
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰의 만료 시간 차이 검증")
    void tokenExpirationTimeTest() throws Exception {
        // given
        String accessToken = jwtUtil.createAccessToken(testEmail);
        String refreshToken = jwtUtil.createRefreshToken(testEmail);

        // when - 토큰에서 만료 시간 추출
        Claims accessClaims = Jwts.parser().setSigningKey("mysecretkey").parseClaimsJws(accessToken).getBody();
        Claims refreshClaims = Jwts.parser().setSigningKey("mysecretkey").parseClaimsJws(refreshToken).getBody();

        Date accessExpiration = accessClaims.getExpiration();
        Date refreshExpiration = refreshClaims.getExpiration();

        // then
        assertTrue(refreshExpiration.after(accessExpiration)); // 리프레시 토큰이 액세스 토큰보다 더 오래 유효함

        // 현재 시간 기준으로 약 15분(900초) 후에 액세스 토큰 만료
        long accessExpiresInSeconds = (accessExpiration.getTime() - System.currentTimeMillis()) / 1000;
        assertTrue(accessExpiresInSeconds > 800 && accessExpiresInSeconds < 1000);

        // 현재 시간 기준으로 약 7일(604800초) 후에 리프레시 토큰 만료
        long refreshExpiresInSeconds = (refreshExpiration.getTime() - System.currentTimeMillis()) / 1000;
        assertTrue(refreshExpiresInSeconds > 604000 && refreshExpiresInSeconds < 605000);
    }
}