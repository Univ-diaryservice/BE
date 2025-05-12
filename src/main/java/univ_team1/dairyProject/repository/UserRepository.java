package univ_team1.dairyProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ_team1.dairyProject.domain.User;
import univ_team1.dairyProject.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
//    Optional<User> findByAccessToken(String accessToken);
}
