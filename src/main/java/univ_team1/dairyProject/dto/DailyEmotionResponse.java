package univ_team1.dairyProject.dto;
import lombok.Data;

import java.time.LocalDate;

// 1주일 포도 알맹이 데이터를 담는 DTO
@Data
public class DailyEmotionResponse {
  // 날짜 (예: 2025-05-01)
  private LocalDate date;
  // 감정 이름 (예: "HAPPY")
  private String emotion;

  // 날짜와 감정 이름 초기화
  public DailyEmotionResponse(LocalDate date, String emotion) {
    this.date = date;
    this.emotion = emotion;
  }

  //감정 데이터가 안 올 시 처리
  public DailyEmotionResponse(LocalDate date) {
    this.date = date;
    this.emotion = null;
  }
}