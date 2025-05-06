package univ_team1.dairyProject.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

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
}