package univ_team1.dairyProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

  @Query("SELECT d.emotion, COUNT(d) FROM Diary d " +
      "WHERE YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month " +
      "GROUP BY d.emotion")
  List<Object[]> findEmotionStatisticsByMonth(
      @Param("year") int year,
      @Param("month") int month);

  @Query("SELECT d.createdAt, d.emotion FROM Diary d " +
      "WHERE d.createdAt BETWEEN :startDate AND :endDate " +
      "ORDER BY d.createdAt DESC")
  List<Object[]> findEmotionsForLast7Days(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}