package univ_team1.dairyProject.dto;

// 필요한 클래스 임포트
import lombok.Data;
import univ_team1.dairyProject.domain.enums.Emotion;

// 월별 감정 통계 데이터를 담는 DTO
@Data
public class EmotionStatResponse {
  // 감정 이름 (예: "HAPPY")
  private String emotion;
  // 감정별 개수 (예: 10)
  private Long count;

  // 생성자: 감정과 개수만 받아 초기화
  public EmotionStatResponse(Emotion emotion, Long count) {
    this.emotion = emotion.name();
    this.count = count;
  }
}
//letsgo