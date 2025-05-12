package univ_team1.dairyProject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column
    private String refreshToken;

    // ▼ 아래는 UserDetails 구현을 위한 메서드들 ▼

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 있다면 이곳에 넣을 수 있음
        return List.of(); // 혹은 List.of(new SimpleGrantedAuthority("ROLE_USER"))
    }

    @Override
    public String getUsername() {
        return this.email; // 로그인 ID로 사용할 필드
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 로직 없으면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 로직 없으면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격증명 만료 로직 없으면 true
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}
