package univ_team1.dairyProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary,Long> {
    List<Diary> findByDateBetween(LocalDate startDate, LocalDate endDate);


  @Query("SELECT d.emotion, COUNT(d) FROM Diary d " +
      "WHERE YEAR(d.date) = :year AND MONTH(d.date) = :month " +
      "GROUP BY d.emotion")
  List<Object[]> findEmotionStatisticsByMonth(
      @Param("year") int year,
      @Param("month") int month);

  @Query("SELECT d.date, d.emotion FROM Diary d " +
      "WHERE d.date BETWEEN :startDate AND :endDate " +
      "ORDER BY d.date DESC")
  List<Object[]> findEmotionsForLast7Days(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
//제발 푸시 돼라