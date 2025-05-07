package univ_team1.dairyProject.util;

import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class JwtUtil {
    private final String secret = "mysecretkey";
    private final long ACCESS_EXP = 1000 * 60 * 15;
    private final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7;

    private String generateToken(String subject, long expMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expMillis))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String createAccessToken(String email) {
        return generateToken(email, ACCESS_EXP);
    }

    public String createRefreshToken(String email) {
        return generateToken(email, REFRESH_EXP);
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public String verifyAndRefreshTokens(String accessToken, String refreshToken) {
        // 1. accessToken이 아직 유효하면 그대로 반환
        if (validateToken(accessToken)) {
            return accessToken;
        }

        // 2. accessToken 만료됐지만 refreshToken이 유효하면 새 accessToken 발급
        if (validateToken(refreshToken)) {
            String email = extractEmail(refreshToken);
            return createAccessToken(email);
        }

        // 3. 둘 다 유효하지 않으면 예외 발생
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다");
    }
}