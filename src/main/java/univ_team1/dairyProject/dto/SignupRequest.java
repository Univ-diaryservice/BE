package univ_team1.dairyProject.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String userNickName;
    private String email;
    private String password;
}
