package univ_team1.dairyProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateDiaryRequest {

    private String title;
    private String content;
    private String emotion;
    private String weather;

}
