package algohani.moduleuserapi.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshReqDto(
    @NotBlank(message = "Refresh Token은 필수 값입니다.")
    String refreshToken
) {

}
