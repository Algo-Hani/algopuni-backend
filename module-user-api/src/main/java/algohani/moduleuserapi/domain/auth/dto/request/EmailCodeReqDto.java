package algohani.moduleuserapi.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailCodeReqDto {

    public record Send(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        String email
    ) {

    }

    public record Verify(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "인증 코드를 입력해주세요.")
        String code
    ) {

    }
}
