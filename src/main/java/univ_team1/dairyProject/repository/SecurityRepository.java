package univ_team1.dairyProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ_team1.dairyProject.domain.UserEntity;

import java.util.Optional;

public interface SecurityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
