package univ_team1.dairyProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ_team1.dairyProject.domain.Diary;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary,Long> {
    List<Diary> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
