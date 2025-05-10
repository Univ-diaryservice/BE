package univ_team1.dairyProject.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SignupRequest {

    @Schema(description = "사용자 닉네임", example = "jinwook", required = true)
    private String userNickName;

    @Schema(description = "사용자 이메일", example = "example@example.com", required = true)
    private String email;

    @Schema(description = "사용자 비밀번호", example = "samplepassword",required = true)
    private String password;
}
