package univ_team1.dairyProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.dto.DailyEmotionResponse;
import univ_team1.dairyProject.dto.EmotionStatResponse;
import univ_team1.dairyProject.service.StatsService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 통계 API를 처리하는 컨트롤러
@RestController
@RequestMapping("/api/diaries")
@Validated
public class StatsController {

  @Autowired
  private StatsService statsService;

  @GetMapping("/emotions/stats")
  public ResponseEntity<Map<Emotion, Long>> getMonthlyEmotionStats(
      @RequestParam @NotNull(message = "Year is required") Integer year,
      @RequestParam @NotNull(message = "Month is required") @Min(1) @Max(12) Integer month) {
    Map<Emotion, Long> stats = statsService.getMonthlyEmotionStats(year, month);
    return ResponseEntity.ok(stats);
  }
  //새로운 한 주의 감정 값을 반환
  @GetMapping("/last-7-days")
  public ResponseEntity<List<DailyEmotionResponse>> getLast7DaysEmotions() {
    List<DailyEmotionResponse> response = statsService.getCurrentWeekEmotions();
    return ResponseEntity.ok(response);
  }
}