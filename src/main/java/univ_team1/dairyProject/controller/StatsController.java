package univ_team1.dairyProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.dto.EmotionStatResponse;
import univ_team1.dairyProject.service.StatsService;
import univ_team1.dairyProject.dto.DailyEmotionResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diaries/stats")
@Validated
public class StatsController {

  @Autowired
  private StatsService statsService;

  //통계 조회 API 엔드포인트
  @GetMapping("/emotions")
  public ResponseEntity<?> getMonthlyEmotionStats(
      @RequestParam @NotNull(message = "연도를 입력하셔야 합니다") Integer year,
      @RequestParam @NotNull(message = "월을 입력하셔야 합니다") @Min(1) @Max(12) Integer month, //최소 1월, 최대 12월 입력
      @RequestParam(defaultValue = "false") boolean includeEmojis) {
    //서비스에서 통계 데이터 조회
    Map<Emotion, Long> stats = statsService.getMonthlyEmotionStats(year, month);
    //이모지 포함 여부에 따라 응답 형식 결정
    if (includeEmojis) {
      //이모지 포함 : 배열 형식으로 변환
      List<EmotionStatResponse> response = stats.entrySet().stream()
          .map(entry -> new EmotionStatResponse(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.ok(stats);
    }
  }

  @GetMapping("/last-7-days")
  public ResponseEntity<List<DailyEmotionResponse>> getLast7DaysEmotions() {
    List<DailyEmotionResponse> response = statsService.getLast7DaysEmotions();
    return ResponseEntity.ok(response);
  }
}
